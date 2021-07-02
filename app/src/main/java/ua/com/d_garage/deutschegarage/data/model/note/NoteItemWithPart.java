package ua.com.d_garage.deutschegarage.data.model.note;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

import java.util.Objects;

@DatabaseView(
        "SELECT Note.id as noteId, Part.*, NoteItem.quantity " +
        "FROM NoteItem " +
        "INNER JOIN Note, Part " +
        "WHERE noteId = Note.id AND partId = Part.id"
)
public class NoteItemWithPart {

    private final Long noteId;

    @Embedded
    private final Part part;

    private final int quantity;

    public NoteItemWithPart(Long noteId, Part part, int quantity) {
        this.noteId = noteId;
        this.part = part;
        this.quantity = quantity;
    }

    public Long getNoteId() {
        return noteId;
    }

    public Part getPart() {
        return part;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteItemWithPart that = (NoteItemWithPart) o;
        return Objects.equals(noteId, that.noteId) && Objects.equals(part, that.part);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, part);
    }

    @NotNull
    @Override
    public String toString() {
        return "NoteItemWithPart{" +
                "noteId=" + noteId +
                ", part=" + part +
                ", quantity=" + quantity +
                '}';
    }
}
