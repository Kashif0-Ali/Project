package todoproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import todoproject.Main;
import todoproject.model.*;
import todoproject.util.DateUtil;
import todoproject.util.PriorityStringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class MainController {
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, LocalDate> dueDateColumn;
    @FXML private TableColumn<Task, Task.Priority> priorityColumn;
    @FXML private TableColumn<Task, Category> categoryColumn;
    @FXML private TableColumn<Task, Boolean> completedColumn;
    
    @FXML private BorderPane mainBorderPane;
    @FXML private ComboBox<Category> categoryFilterComboBox;
    @FXML private ComboBox<Task.Priority> priorityFilterComboBox;
    @FXML private DatePicker dueDateFilterDatePicker;
    @FXML private CheckBox showCompletedCheckBox;
    @FXML private TextField searchField;
    
    private TaskManager taskManager;

    public void initialize() {
        taskManager = new TaskManager();
        
        // Initialize table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DateUtil.format(item));
                }
            }
        });
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        completedColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
        
        // Initialize filters
        categoryFilterComboBox.setItems(FXCollections.observableArrayList(taskManager.getCategories()));
        categoryFilterComboBox.getItems().add(0, null); // Add null for "All categories"
        categoryFilterComboBox.getSelectionModel().selectFirst();
        
        priorityFilterComboBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        priorityFilterComboBox.getItems().add(0, null); // Add null for "All priorities"
        priorityFilterComboBox.getSelectionModel().selectFirst();
        priorityFilterComboBox.setConverter(new PriorityStringConverter());
        
        showCompletedCheckBox.setSelected(false);
        
        // Set up filtering
        setupFiltering();
        
        // Load tasks
        refreshTaskTable();
    }
    
    private void setupFiltering() {
        FilteredList<Task> filteredTasks = new FilteredList<>(FXCollections.observableArrayList(taskManager.getTasks()));
        
        // Add listeners to filter controls
        categoryFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredTasks.setPredicate(task -> {
                if (newVal != null && !task.getCategory().equals(newVal)) {
                    return false;
                }
                return true;
            });
        });
        
        priorityFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredTasks.setPredicate(task -> {
                if (newVal != null && task.getPriority() != newVal) {
                    return false;
                }
                return true;
            });
        });
        
        dueDateFilterDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredTasks.setPredicate(task -> {
                if (newVal != null && !task.getDueDate().equals(newVal)) {
                    return false;
                }
                return true;
            });
        });
        
        showCompletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            filteredTasks.setPredicate(task -> {
                if (!newVal && task.isCompleted()) {
                    return false;
                }
                return true;
            });
        });
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredTasks.setPredicate(task -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase();
                return task.getTitle().toLowerCase().contains(lowerCaseFilter) || 
                       task.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        // Wrap the FilteredList in a SortedList 
        SortedList<Task> sortedTasks = new SortedList<>(filteredTasks);
        sortedTasks.comparatorProperty().bind(taskTable.comparatorProperty());
        
        // Add the sorted (and filtered) data to the table
        taskTable.setItems(sortedTasks);
    }
    
    @FXML
    private void handleAddTask() {
        Task newTask = showTaskEditDialog(null);
        if (newTask != null) {
            taskManager.addTask(newTask);
            refreshTaskTable();
        }
    }
    
    @FXML
    private void handleEditTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Task editedTask = showTaskEditDialog(selectedTask);
            if (editedTask != null) {
                taskManager.updateTask(selectedTask, editedTask);
                refreshTaskTable();
            }
        } else {
            showAlert("No Selection", "No Task Selected", "Please select a task in the table.");
        }
    }
    
    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Task: " + selectedTask.getTitle());
            alert.setContentText("Are you sure you want to delete this task?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                taskManager.removeTask(selectedTask);
                refreshTaskTable();
            }
        } else {
            showAlert("No Selection", "No Task Selected", "Please select a task in the table.");
        }
    }
    
    @FXML
    private void handleMarkComplete() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(true);
            taskManager.updateTask(selectedTask, selectedTask);
            refreshTaskTable();
        } else {
            showAlert("No Selection", "No Task Selected", "Please select a task in the table.");
        }
    }
    
    @FXML
    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Category");
        dialog.setHeaderText("Create a new category");
        dialog.setContentText("Category name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            // Simple color assignment - in a real app you might want a color picker
            String color = String.format("#%06x", (int)(Math.random() * 0xFFFFFF));
            Category newCategory = new Category(name, color);
            taskManager.addCategory(newCategory);
            
            // Refresh the category filter
            categoryFilterComboBox.setItems(FXCollections.observableArrayList(taskManager.getCategories()));
            categoryFilterComboBox.getItems().add(0, null);
            categoryFilterComboBox.getSelectionModel().selectFirst();
        });
    }
    
    @FXML
    private void handleClearFilters() {
        categoryFilterComboBox.getSelectionModel().selectFirst();
        priorityFilterComboBox.getSelectionModel().selectFirst();
        dueDateFilterDatePicker.setValue(null);
        showCompletedCheckBox.setSelected(false);
        searchField.clear();
    }
    
    private Task showTaskEditDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/TaskEditDialog.fxml"));
            DialogPane dialogPane = loader.load();
            
            TaskEditController controller = loader.getController();
            controller.setTaskManager(taskManager);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(task == null ? "Add New Task" : "Edit Task");
            
            if (task != null) {
                controller.setTask(task);
            }
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                return controller.getEditedTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void refreshTaskTable() {
        taskTable.getItems().setAll(taskManager.getTasks());
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}