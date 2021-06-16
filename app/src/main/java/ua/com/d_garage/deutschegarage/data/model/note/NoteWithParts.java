package ua.com.d_garage.deutschegarage.data.model.note;

import androidx.room.Embedded;
import androidx.room.Relation;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

import java.util.List;
import java.util.Objects;

public class NoteWithParts {

    @Embedded
    private final Note note;
    @Relation(parentColumn = "id", entityColumn = "noteId")
    private final List<Part> parts;

    public NoteWithParts(Note note, List<Part>parts) {
        this.note = note;
        this.parts = parts;
    }

    public Note getNote() {
        return note;
    }

    public List<Part> getParts() {
        return parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteWithParts that = (NoteWithParts) o;
        return Objects.equals(note, that.note) && Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note, parts);
    }

}
