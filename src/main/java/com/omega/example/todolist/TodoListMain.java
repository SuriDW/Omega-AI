package com.omega.example.todolist;

/**
 * TodoListMain - Main entry point for the TODO list application
 */
public class TodoListMain {
    
    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        boolean cudaAvailable = false;
        
        // Start the TODO list application without trying to initialize CUDA
        try {
            TodoListApp app = new TodoListApp(cudaAvailable);
            app.show();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
