package ua.com.d_garage.deutschegarage.data.model.part;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.data.model.note.Note;

import java.util.Objects;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Note.class,
                parentColumns = "id",
                childColumns = "noteId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
})
public class Part {

    @PrimaryKey(autoGenerate = true)
    private final long id;
    @ColumnInfo(index = true)
    private final long noteId;
    private final long vin;
    private final String name;

    public Part(long id, long noteId, long vin, String name) {
        this.id = id;
        this.noteId = noteId;
        this.vin = vin;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public long getNoteId() {
        return noteId;
    }

    public long getVin() {
        return vin;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return noteId == part.noteId && vin == part.vin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, vin);
    }

    @NotNull
    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", vin=" + vin +
                ", name='" + name + '\'' +
                '}';
    }

}
