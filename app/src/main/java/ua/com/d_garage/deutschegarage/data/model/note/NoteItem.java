package ua.com.d_garage.deutschegarage.data.model.note;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

import java.util.Objects;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Note.class,
                parentColumns = "id",
                childColumns = "noteId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(
                entity = Part.class,
                parentColumns = "id",
                childColumns = "partId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
})
public class NoteItem {

    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @ColumnInfo(index = true)
    private final Long noteId;

    @ColumnInfo(index = true)
    private final Long partId;

    private final int quantity;

    public NoteItem(Long id, Long noteId, Long partId, int quantity) {
        this.id = id;
        this.noteId = noteId;
        this.partId = partId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public Long getPartId() {
        return partId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteItem noteItem = (NoteItem) o;
        return Objects.equals(id, noteItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NotNull
    @Override
    public String toString() {
        return "NoteItem{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", partId=" + partId +
                ", quantity=" + quantity +
                '}';
    }
}
