package todoproject.model;

import javafx.beans.property.*;

public class Category {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty color = new SimpleStringProperty();

    public Category(String name, String color) {
        this.name.set(name);
        this.color.set(color);
    }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getColor() { return color.get(); }
    public void setColor(String color) { this.color.set(color); }
    public StringProperty colorProperty() { return color; }

    @Override
    public String toString() {
        return getName();
    }
}