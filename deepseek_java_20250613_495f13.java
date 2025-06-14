package todoproject.model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Task {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Priority> priority = new SimpleObjectProperty<>();
    private final ObjectProperty<Category> category = new SimpleObjectProperty<>();
    private final BooleanProperty completed = new SimpleBooleanProperty();

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public Task(String title, String description, LocalDate dueDate, Priority priority, Category category) {
        this.title.set(title);
        this.description.set(description);
        this.dueDate.set(dueDate);
        this.priority.set(priority);
        this.category.set(category);
        this.completed.set(false);
    }

    // Getters and setters for all properties
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate dueDate) { this.dueDate.set(dueDate); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    public Priority getPriority() { return priority.get(); }
    public void setPriority(Priority priority) { this.priority.set(priority); }
    public ObjectProperty<Priority> priorityProperty() { return priority; }

    public Category getCategory() { return category.get(); }
    public void setCategory(Category category) { this.category.set(category); }
    public ObjectProperty<Category> categoryProperty() { return category; }

    public boolean isCompleted() { return completed.get(); }
    public void setCompleted(boolean completed) { this.completed.set(completed); }
    public BooleanProperty completedProperty() { return completed; }
}