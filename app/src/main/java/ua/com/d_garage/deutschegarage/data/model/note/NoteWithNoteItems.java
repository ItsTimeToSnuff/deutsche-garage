package ua.com.d_garage.deutschegarage.data.model.note;

import androidx.room.Embedded;
import androidx.room.Relation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class NoteWithNoteItems {

    @Embedded
    private final Note note;

    @Relation(parentColumn = "id", entityColumn = "noteId")
    private final List<NoteItemWithPart> noteItems;

    public NoteWithNoteItems(Note note, List<NoteItemWithPart> noteItems) {
        this.note = note;
        this.noteItems = noteItems;
    }

    public Note getNote() {
        return note;
    }

    public List<NoteItemWithPart> getNoteItems() {
        return noteItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteWithNoteItems that = (NoteWithNoteItems) o;
        return Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note);
    }

    @NotNull
    @Override
    public String toString() {
        return "NoteWithNoteItems{" +
                "note=" + note +
                ", noteItems=" + noteItems +
                '}';
    }
}
