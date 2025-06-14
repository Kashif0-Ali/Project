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