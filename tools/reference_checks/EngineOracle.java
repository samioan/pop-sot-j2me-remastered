import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the ORIGINAL class files (pop2.e = Game, a = Actor, d = Prince, b = Level, c = Sprite) tick by
 * tick with scripted input and prints a fingerprint of every static field and every live actor field
 * after each iteration of Game.run(). The native engine_check prints the same fingerprints from the
 * translated C++; tools/check_engine.py diffs them. Drawing is a no-op here (engine_adapter), so this
 * compares game state, not pixels.
 *
 * The loop is the one sot/port/engine/engine_glue.cpp drives: the first pass shows the logo, loads
 * (Game.i) and waits one second's worth of iterations before Game.a(); each later iteration sets
 * Game.i to the clock, calls Game.d() and paints; the clock (oracle.OracleSyst, which stands in for
 * java.lang.System in the rewritten class files) advances by max(Game.H, 20) ms per iteration.
 *
 * usage: EngineOracle <script|-> <ticks> <trace> [dumpTick dumpFile] [record]
 * record: the bytes of record 1 of POP2RECORD to preload (the native side gets the same store).
 */
public final class EngineOracle {
    static final long SEED = 1469598103934665603L, PRIME = 1099511628211L;
    static long hash;
    static PrintWriter lines;
    static String prefix;

    static long mix(long h, long v) { return (h ^ v) * PRIME; }

    static long value(Object v) {
        if (v == null) return 0;
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Long) return (Long) v;
        if (v instanceof Short) return (Short) v;
        if (v instanceof Byte) return (Byte) v;
        if (v instanceof Character) return (Character) v;
        if (v instanceof Boolean) return ((Boolean) v) ? 1 : 0;
        if (v instanceof String) {
            String s = (String) v;
            long h = mix(SEED, s.length());
            for (int i = 0; i < s.length(); i++) h = mix(h, s.charAt(i));
            return h;
        }
        if (v.getClass().isArray()) {
            int n = Array.getLength(v);
            long h = mix(SEED, n);
            if (v instanceof int[]) { for (int x : (int[]) v) h = mix(h, x); }
            else if (v instanceof byte[]) { for (byte x : (byte[]) v) h = mix(h, x); }
            else if (v instanceof short[]) { for (short x : (short[]) v) h = mix(h, x); }
            else if (v instanceof boolean[]) { for (boolean x : (boolean[]) v) h = mix(h, x ? 1 : 0); }
            else if (v instanceof char[]) { for (char x : (char[]) v) h = mix(h, x); }
            else if (v instanceof long[]) { for (long x : (long[]) v) h = mix(h, x); }
            else for (int i = 0; i < n; i++) h = mix(h, value(Array.get(v, i)));
            return h;
        }
        return 1;
    }

    static String elements(Object v) {
        if (v == null || !v.getClass().isArray() || !v.getClass().getComponentType().isPrimitive() || Array.getLength(v) > 4096) return "";
        StringBuilder sb = new StringBuilder(" [");
        for (int i = 0; i < Array.getLength(v); i++) {
            if (i > 0) sb.append(',');
            Object e = Array.get(v, i);
            sb.append(e instanceof Boolean ? ((Boolean) e ? 1 : 0) : e instanceof Character ? (int) (Character) e : ((Number) e).longValue());
        }
        return sb.append(']').toString();
    }

    static void put(Field f, Object owner) throws Exception {
        long x = value(f.get(owner));
        hash = mix(hash, x);
        if (lines != null) lines.println(prefix + f.getName() + "=" + Long.toUnsignedString(x) + elements(f.get(owner)));
    }

    static List<Field> fields(Class<?> c, boolean statics) {
        List<Field> out = new ArrayList<>();
        for (Field f : c.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) != statics) continue;
            f.setAccessible(true);
            out.add(f);
        }
        return out;
    }

    /** Instance fields of an object, declaring classes from the top of the game's hierarchy down. */
    static void putObject(Object o) throws Exception {
        List<Class<?>> chain = new ArrayList<>();
        for (Class<?> c = o.getClass(); c != null && c.getName().startsWith("pop2."); c = c.getSuperclass()) chain.add(0, c);
        for (Class<?> c : chain) for (Field x : fields(c, false)) put(x, o);
    }

    static Field field(Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    static Method method(Class<?> c, String name, Class<?>... params) throws Exception {
        Method m = c.getDeclaredMethod(name, params);
        m.setAccessible(true);
        return m;
    }

    public static void main(String[] args) throws Exception {
        String script = args[0];
        int ticks = Integer.parseInt(args[1]);
        int dumpTick = args.length > 4 ? Integer.parseInt(args[3]) : -1;
        String dumpFile = args.length > 4 ? args[4] : null;
        if (args.length > 5 && !args[5].equals("-")) {
            javax.microedition.rms.RecordStore store = javax.microedition.rms.RecordStore.openRecordStore("POP2RECORD", true);
            byte[] record = Files.readAllBytes(Paths.get(args[5]));
            store.addRecord(record, 0, record.length);
        }

        List<int[]> events = new ArrayList<>();
        if (!script.equals("-"))
            for (String line : Files.readAllLines(Paths.get(script))) {
                String[] p = line.trim().split("\\s+");
                if (p.length == 3) events.add(new int[]{Integer.parseInt(p[0]), p[1].equals("+") ? 1 : 0, Integer.parseInt(p[2])});
            }

        ClassLoader boot = null;
        Class<?> midlet = Class.forName("pop2.PrinceSOT", true, boot);
        Class<?> game = Class.forName("pop2.e", false, boot);
        Class<?> clock = Class.forName("oracle.OracleSyst", true, boot);
        Field now = field(clock, "now");

        java.lang.reflect.Constructor<?> mctor = midlet.getDeclaredConstructor();
        mctor.setAccessible(true);
        Object midletInstance = mctor.newInstance();
        java.lang.reflect.Constructor<?> ctor = game.getDeclaredConstructor(midlet);
        ctor.setAccessible(true);
        Object canvas = ctor.newInstance(midletInstance);
        field(midlet, "c").set(null, canvas);
        Class<?> actor = Class.forName("pop2.a", true, boot), prince = Class.forName("pop2.d", true, boot);
        Class<?> level = Class.forName("pop2.b", true, boot), sprite = Class.forName("pop2.c", true, boot);

        Field aF = field(game, "aF"), e = field(game, "e"), aw = field(game, "aw"), H = field(game, "H"), l = field(game, "l"), gi = field(game, "i");
        Method paint = method(game, "paint", javax.microedition.lcdui.Graphics.class);
        Method load = method(game, "i"), title = method(game, "a"), update = method(game, "d");
        Method keyPressed = method(game, "keyPressed", int.class), keyReleased = method(game, "keyReleased", int.class);
        Field princeRef = field(level, "i"), actorList = field(level, "m");

        List<Field> gameStatics = fields(game, true), gameFields = fields(game, false);
        List<Field> actorStatics = fields(actor, true), princeStatics = fields(prince, true);
        List<Field> levelStatics = fields(level, true), spriteStatics = fields(sprite, true);

        long time = 1000000L;
        boolean booted = false, quit = false;
        int bootTicks = 0;
        javax.microedition.lcdui.Graphics g = new javax.microedition.lcdui.Graphics(176, 208);
        try (PrintWriter trace = new PrintWriter(Files.newBufferedWriter(Paths.get(args[2]), StandardCharsets.UTF_8))) {
            for (int t = 0; t < ticks && !quit; t++) {
                now.setLong(null, time);
                for (int[] ev : events) if (ev[0] == t) (ev[1] == 1 ? keyPressed : keyReleased).invoke(canvas, ev[2]);
                now.setLong(null, time);
                if (!booted) {
                    booted = true;
                    aF.setBoolean(canvas, true);
                    e.setBoolean(null, true);
                    aw.setInt(null, 6);
                    paint.invoke(canvas, g);
                    aF.setBoolean(canvas, false);
                    load.invoke(canvas);
                    bootTicks = (int) (1000 / Math.max(H.getLong(null), 20L));
                } else if (bootTicks > 0) {
                    if (--bootTicks == 0) title.invoke(null);
                } else if (l.getByte(null) != 0) {
                    gi.setLong(null, time);
                    update.invoke(canvas);
                    paint.invoke(canvas, g);
                } else {
                    // run() left its loop: notifyDestroyed, then destroyApp(true) (saves through Game.n)
                    quit = true;
                    method(midlet, "destroyApp", boolean.class).invoke(midletInstance, true);
                }
                time += Math.max(H.getLong(null), 20L);

                boolean dump = t == dumpTick;
                if (dump) lines = new PrintWriter(Files.newBufferedWriter(Paths.get(dumpFile), StandardCharsets.UTF_8));
                hash = SEED;
                prefix = "e.";
                for (Field x : gameStatics) put(x, null);
                for (Field x : gameFields) put(x, canvas);
                prefix = "a."; for (Field x : actorStatics) put(x, null);
                prefix = "d."; for (Field x : princeStatics) put(x, null);
                prefix = "b."; for (Field x : levelStatics) put(x, null);
                prefix = "c."; for (Field x : spriteStatics) put(x, null);
                Object p = princeRef.get(null);
                if (p != null) { prefix = "prince."; putObject(p); }
                Object[] list = (Object[]) actorList.get(null);
                if (list != null)
                    for (int i = 0; i < list.length; i++) {
                        if (list[i] == null) continue;
                        prefix = "actor[" + i + "].";
                        putObject(list[i]);
                    }
                if (dump) { lines.close(); lines = null; }
                trace.println(t + " " + Long.toUnsignedString(hash) + " " + aw.getInt(null));
            }
        }
    }
}
