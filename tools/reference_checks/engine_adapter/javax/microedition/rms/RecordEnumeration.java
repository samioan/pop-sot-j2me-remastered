package javax.microedition.rms;

import java.util.ArrayList;
import java.util.List;

public class RecordEnumeration {
    final RecordStore store;
    final List<Integer> order = new ArrayList<>();
    int pos;
    RecordEnumeration(RecordStore s) { store = s; }
    public int numRecords() { return order.size(); }
    public boolean hasNextElement() { return pos < order.size(); }
    public int nextRecordId() throws InvalidRecordIDException {
        if (pos >= order.size()) throw new InvalidRecordIDException("end");
        return order.get(pos++);
    }
    public byte[] nextRecord() throws RecordStoreException { return store.getRecord(nextRecordId()); }
    public void reset() { pos = 0; }
    public void destroy() {}
}
