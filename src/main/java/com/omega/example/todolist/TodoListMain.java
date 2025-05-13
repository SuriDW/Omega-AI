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
        try {
            try {
                com.omega.engine.gpu.CUDAModules.initContext();
                cudaAvailable = true;
                System.out.println("CUDA initialized successfully. LLM features enabled.");
            } catch (Exception e) {
                System.out.println("CUDA initialization failed. Running in CPU-only mode.");
                System.out.println("LLM functionality will be disabled.");
            }
            
            // Start the TODO list application
            TodoListApp app = new TodoListApp(cudaAvailable);
            app.show();
            
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        if (cudaAvailable) {
                            com.omega.engine.gpu.CUDAMemoryManager.free();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}
