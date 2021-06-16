package ua.com.d_garage.deutschegarage.data.model.part;

public class PartRequestData {

    private final long vin;
    private final long noteId;

    public PartRequestData(long vin, long noteId) {
        this.vin = vin;
        this.noteId = noteId;
    }

    public long getVin() {
        return vin;
    }

    public long getNoteId() {
        return noteId;
    }
}
