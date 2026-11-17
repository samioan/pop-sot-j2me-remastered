package javax.microedition.rms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Oracle-only record stores kept in memory, with the native engine's id and enumeration order. */
public class RecordStore {
    public static final Map<String, RecordStore> STORES = new HashMap<>();
    final List<int[]> ids = new ArrayList<>();
    final List<byte[]> data = new ArrayList<>();
    int nextId = 1;
    public static RecordStore openRecordStore(String name, boolean create) throws RecordStoreException {
        String n = name.startsWith("/") ? name.substring(1) : name;
        RecordStore s = STORES.get(n);
        if (s == null) {
            if (!create) throw new RecordStoreNotFoundException(n);
            s = new RecordStore();
            STORES.put(n, s);
        }
        return s;
    }
    public static void deleteRecordStore(String name) throws RecordStoreException {
        if (STORES.remove(name.startsWith("/") ? name.substring(1) : name) == null) throw new RecordStoreNotFoundException(name);
    }
    int index(int id) throws InvalidRecordIDException {
        for (int i = 0; i < ids.size(); i++) if (ids.get(i)[0] == id) return i;
        throw new InvalidRecordIDException(Integer.toString(id));
    }
    public int getNumRecords() { return ids.size(); }
    public int getNextRecordID() { return nextId; }
    public int getSize() { int n = 0; for (byte[] b : data) n += b.length; return n; }
    public int getSizeAvailable() { return 1 << 20; }
    public int addRecord(byte[] d, int o, int l) {
        ids.add(new int[]{nextId});
        data.add(l > 0 ? java.util.Arrays.copyOfRange(d, o, o + l) : new byte[0]);
        return nextId++;
    }
    public void setRecord(int id, byte[] d, int o, int l) throws InvalidRecordIDException {
        data.set(index(id), l > 0 ? java.util.Arrays.copyOfRange(d, o, o + l) : new byte[0]);
    }
    public void deleteRecord(int id) throws InvalidRecordIDException { int i = index(id); ids.remove(i); data.remove(i); }
    public int getRecord(int id, byte[] out, int o) throws InvalidRecordIDException {
        byte[] b = data.get(index(id));
        System.arraycopy(b, 0, out, o, b.length);
        return b.length;
    }
    public byte[] getRecord(int id) throws InvalidRecordIDException { byte[] b = data.get(index(id)); return b.length == 0 ? null : b.clone(); }
    public int getRecordSize(int id) throws InvalidRecordIDException { return data.get(index(id)).length; }
    public RecordEnumeration enumerateRecords(RecordFilter f, RecordComparator c, boolean k) {
        RecordEnumeration e = new RecordEnumeration(this);
        for (int i = ids.size() - 1; i >= 0; i--) e.order.add(ids.get(i)[0]);
        return e;
    }
    public void closeRecordStore() {}
}
