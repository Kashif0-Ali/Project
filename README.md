# Project
.root {
    -fx-font-family: "Segoe UI";
    -fx-base: #ecf0f1;
}
.table-view {
    -fx-background-color: white;
}

.table-row-cell:completed {
    -fx-background-color: #e8f8f5;
}

.table-row-cell:overdue {
    -fx-background-color: #fadbd8;
}

.table-row-cell:high-priority {
    -fx-text-fill: #e74c3c;
    -fx-font-weight: bold;
}
.table-row-cell:medium-priority {
    -fx-text-fill: #f39c12;
}
.category-work {
    -fx-text-fill: #FF5733;
    -fx-font-weight: bold;
}

.category-personal {
    -fx-text-fill: #33FF57;
    -fx-font-weight: bold;
}

.category-study {
    -fx-text-fill: #3357FF;
    -fx-font-weight: bold;
}
.category-health {
    -fx-text-fill: #F033FF;
    -fx-font-weight: bold;
}
.button {
    -fx-background-radius: 5;
    -fx-padding: 5 10 5 10;
}
.tool-bar {
    -fx-background-color: #3498db;
    -fx-padding: 5;
}

.tool-bar .button {
    -fx-background-color: #2980b9;
    -fx-text-fill: white;
}

.tool-bar .button:hover {
    -fx-background-color: #1abc9c;
}

.vbox {
    -fx-background-color: #f5f5f5;
    -fx-padding: 10;
    -fx-spacing: 5;
    -fx-border-color: #ddd;
    -fx-border-width: 0 1 0 0;
}
package todoproject.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final String DATE_PATTERN = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validDate(String dateString) {
        return parse(dateString) != null;
    }
}
package todoproject.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final String DATE_PATTERN = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validDate(String dateString) {
        return parse(dateString) != null;
    }
}
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
        categoryFilterComboBox.setItems(FXCollections.observableArrayList(taskManager.getCategories()));
        categoryFilterComboBox.getItems().add(0, null); // Add null for "All categories"
        categoryFilterComboBox.getSelectionModel().selectFirst();
        
        priorityFilterComboBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        priorityFilterComboBox.getItems().add(0, null); // Add null for "All priorities"
        priorityFilterComboBox.getSelectionModel().selectFirst();
        priorityFilterComboBox.setConverter(new PriorityStringConverter());
        
        showCompletedCheckBox.setSelected(false);
        
        setupFiltering();
        refreshTaskTable();
    }
    
    private void setupFiltering() {
        FilteredList<Task> filteredTasks = new FilteredList<>(FXCollections.observableArrayList(taskManager.getTasks()));
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
        SortedList<Task> sortedTasks = new SortedList<>(filteredTasks);
        sortedTasks.comparatorProperty().bind(taskTable.comparatorProperty());
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
            String color = String.format("#%06x", (int)(Math.random() * 0xFFFFFF));
            Category newCategory = new Category(name, color);
            taskManager.addCategory(newCategory);
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
package todoproject.model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private List<Category> categories;
    private static final String DATA_FILE = "tasks.dat";

    public TaskManager() {
        tasks = new ArrayList<>();
        categories = new ArrayList<>();
        loadDefaultCategories();
        loadTasks();
    }

    private void loadDefaultCategories() {
        categories.add(new Category("Work", "#FF5733"));
        categories.add(new Category("Personal", "#33FF57"));
        categories.add(new Category("Study", "#3357FF"));
        categories.add(new Category("Health", "#F033FF"));
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        saveTasks();
    }

    public void updateTask(Task oldTask, Task newTask) {
        int index = tasks.indexOf(oldTask);
        if (index != -1) {
            tasks.set(index, newTask);
            saveTasks();
        }
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksDueToday() {
        List<Task> dueToday = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task.getDueDate().equals(today) && !task.isCompleted()) {
                dueToday.add(task);
            }
        }
        return dueToday;
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public void addCategory(Category category) {
        categories.add(category);
        saveTasks();
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            tasks = (List<Task>) ois.readObject();
            categories = (List<Category>) ois.readObject();
        } catch (FileNotFoundException e) {
            // First run, file doesn't exist yet
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
            oos.writeObject(categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
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
            return new Task(title, description, dueDate, priority, category);
        } else {
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            task.setCategory(category);
            return task;
        }
    }
}
This XML file does not appear to have any style information associated with it. The document tree is shown below.
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.cell.*?>
<BorderPane xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="todoproject.controller.MainController">
<top>
<ToolBar>
<Button text="Add Task" onAction="#handleAddTask"/>
<Button text="Edit Task" onAction="#handleEditTask"/>
<Button text="Delete Task" onAction="#handleDeleteTask"/>
<Button text="Mark Complete" onAction="#handleMarkComplete"/>
<Separator orientation="VERTICAL"/>
<Button text="Add Category" onAction="#handleAddCategory"/>
<Separator orientation="VERTICAL"/>
<Button text="Clear Filters" onAction="#handleClearFilters"/>
</ToolBar>
</top>
<left>
<VBox spacing="10" style="-fx-padding: 10;">
<Label text="FILTERS" font="${new Font('System Bold', 12)}"/>
<Label text="Category:"/>
<ComboBox fx:id="categoryFilterComboBox"/>
<Label text="Priority:"/>
<ComboBox fx:id="priorityFilterComboBox"/>
<Label text="Due Date:"/>
<DatePicker fx:id="dueDateFilterDatePicker"/>
<CheckBox fx:id="showCompletedCheckBox" text="Show Completed"/>
<Label text="Search:"/>
<TextField fx:id="searchField"/>
</VBox>
</left>
<center>
<TableView fx:id="taskTable">
<columns>
<TableColumn text="Title" fx:id="titleColumn" prefWidth="150"/>
<TableColumn text="Description" fx:id="descriptionColumn" prefWidth="250"/>
<TableColumn text="Due Date" fx:id="dueDateColumn" prefWidth="100"/>
<TableColumn text="Priority" fx:id="priorityColumn" prefWidth="80"/>
<TableColumn text="Category" fx:id="categoryColumn" prefWidth="100"/>
<TableColumn text="Completed" fx:id="completedColumn" prefWidth="80"/>
</columns>
</TableView>
</center>
</BorderPane>
This XML file does not appear to have any style information associated with it. The document tree is shown below.
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<DialogPane xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="todoproject.controller.TaskEditController">
<content>
<GridPane hgap="10" vgap="10">
<Label text="Title:" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
<TextField fx:id="titleField" GridPane.rowIndex="0" GridPane.columnIndex="1"/>
<Label text="Description:" GridPane.rowIndex="1" GridPane.columnIndex="0"/>
<TextArea fx:id="descriptionArea" GridPane.rowIndex="1" GridPane.columnIndex="1"/>
<Label text="Due Date:" GridPane.rowIndex="2" GridPane.columnIndex="0"/>
<DatePicker fx:id="dueDatePicker" GridPane.rowIndex="2" GridPane.columnIndex="1"/>
<Label text="Priority:" GridPane.rowIndex="3" GridPane.columnIndex="0"/>
<ComboBox fx:id="priorityComboBox" GridPane.rowIndex="3" GridPane.columnIndex="1"/>
<Label text="Category:" GridPane.rowIndex="4" GridPane.columnIndex="0"/>
<ComboBox fx:id="categoryComboBox" GridPane.rowIndex="4" GridPane.columnIndex="1"/>
</GridPane>
</content>
<buttonTypes>
<ButtonType text="OK" buttonData="OK_DONE"/>
<ButtonType text="Cancel" buttonData="CANCEL_CLOSE"/>
</buttonTypes>
</DialogPane>
package todoproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();
            
            primaryStage.setTitle("Advanced To-Do Application");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
