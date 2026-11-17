package oracle;

/**
 * Stands in for java.lang.System inside the original class files (tools/check_engine.py rewrites the
 * class reference): the clock is the simulation clock the native glue drives, properties come from
 * -Doracle.sys.<key>, everything else delegates.
 */
public final class OracleSyst {
    public static long now = 1000000L;
    public static final java.io.PrintStream out = System.out;
    public static final java.io.PrintStream err = System.err;
    public static long currentTimeMillis() { return now; }
    public static void arraycopy(Object a, int b, Object c, int d, int e) { System.arraycopy(a, b, c, d, e); }
    public static String getProperty(String key) { return System.getProperty("oracle.sys." + key); }
    public static void gc() {}
    public static int identityHashCode(Object o) { return System.identityHashCode(o); }
}
