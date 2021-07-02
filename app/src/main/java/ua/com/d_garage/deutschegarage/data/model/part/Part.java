package ua.com.d_garage.deutschegarage.data.model.part;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(indices = {@Index(value = {"vin"}, unique = true)})
public class Part {

    @PrimaryKey(autoGenerate = true)
    private final Long id;

    private final long vin;

    private final String name;

    public Part(Long id, long vin, String name) {
        this.id = id;
        this.vin = vin;
        this.name = name;
    }

    public Long getId() {
        return id;
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
        return vin == part.vin && Objects.equals(id, part.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vin);
    }

    @NotNull
    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", vin=" + vin +
                ", name='" + name + '\'' +
                '}';
    }
}
