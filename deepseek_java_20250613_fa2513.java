package todoproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import todoproject.model.*;
import todoproject.util.PriorityStringConverter;

import java.time.LocalDate;

public class TaskEditController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<Task.Priority> priorityComboBox;
    @FXML private ComboBox<Category> categoryComboBox;
    
    private TaskManager taskManager;
    private Task task;
    
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        
        // Initialize controls
        priorityComboBox.getItems().setAll(Task.Priority.values());
        priorityComboBox.setConverter(new PriorityStringConverter());
        
        categoryComboBox.getItems().setAll(taskManager.getCategories());
    }
    
    public void setTask(Task task) {
        this.task = task;
        
        // Populate fields with task data
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        dueDatePicker.setValue(task.getDueDate());
        priorityComboBox.setValue(task.getPriority());
        categoryComboBox.setValue(task.getCategory());
    }
    
    public Task getEditedTask() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate dueDate = dueDatePicker.getValue();
        Task.Priority priority = priorityComboBox.getValue();
        Category category = categoryComboBox.getValue();
        
        if (title.isEmpty() || dueDate == null || priority == null || category == null) {
            return null;
        }
        
        if (task == null) {
            // Creating a new task
            return new Task(title, description, dueDate, priority, category);
        } else {
            // Editing existing task
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            task.setCategory(category);
            return task;
        }
    }
}