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
        try {
            com.omega.engine.gpu.CUDAModules.initContext();
            
            TodoListApp app = new TodoListApp();
            app.show();
            
        } catch (Exception e) {
            System.err.println("Error initializing CUDA context: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    com.omega.engine.gpu.CUDAMemoryManager.free();
                }
            });
        }
    }
}
