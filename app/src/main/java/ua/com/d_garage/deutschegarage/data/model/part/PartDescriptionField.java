package ua.com.d_garage.deutschegarage.data.model.part;

import org.jetbrains.annotations.NotNull;

public class PartDescriptionField {

    private final String name;
    private final String value;

    public PartDescriptionField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @NotNull
    @Override
    public String toString() {
        return "PartDescriptionField{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
