package com.omega.example.todolist;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleTodoApp - A console-based TODO list application without GUI dependencies
 */
public class ConsoleTodoApp {
    
    private List<TodoTask> tasks = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    
    /**
     * Start the application
     */
    public void start() {
        System.out.println("=== Omega-AI TODO List Application ===");
        System.out.println("(Console version - no GUI or LLM functionality)");
        System.out.println();
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    viewTasks();
                    break;
                case 3:
                    deleteTask();
                    break;
                case 4:
                    analyzeTasks();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        System.out.println("Thank you for using Omega-AI TODO List Application!");
        scanner.close();
    }
    
    /**
     * Display the main menu
     */
    private void displayMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1. Add Task");
        System.out.println("2. View Tasks");
        System.out.println("3. Delete Task");
        System.out.println("4. Analyze Tasks");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }
    
    /**
     * Get user choice from input
     * @return The user's choice
     */
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Add a new task
     */
    private void addTask() {
        System.out.print("Enter task description: ");
        String taskText = scanner.nextLine().trim();
        
        if (!taskText.isEmpty()) {
            TodoTask task = new TodoTask(taskText);
            tasks.add(task);
            System.out.println("Task added successfully!");
        } else {
            System.out.println("Task cannot be empty.");
        }
    }
    
    /**
     * View all tasks
     */
    private void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        
        System.out.println("\n=== TASKS ===");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getText());
        }
    }
    
    /**
     * Delete a task
     */
    private void deleteTask() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to delete.");
            return;
        }
        
        viewTasks();
        System.out.print("Enter the number of the task to delete: ");
        
        try {
            int taskNumber = Integer.parseInt(scanner.nextLine());
            if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                tasks.remove(taskNumber - 1);
                System.out.println("Task deleted successfully!");
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Analyze tasks (simple version without LLM)
     */
    private void analyzeTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to analyze.");
            return;
        }
        
        System.out.println("\n=== TASK ANALYSIS ===");
        System.out.println("(LLM functionality is disabled in console mode)");
        System.out.println("Simple Analysis:");
        System.out.println("- You have " + tasks.size() + " tasks in your list.");
        
        if (tasks.size() > 3) {
            System.out.println("- Consider prioritizing your tasks to focus on the most important ones.");
        }
        
        if (tasks.size() > 5) {
            System.out.println("- Your task list is getting long. Consider breaking down larger tasks into smaller ones.");
        }
    }
    
    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        ConsoleTodoApp app = new ConsoleTodoApp();
        app.start();
    }
}
