package com.omega.example.todolist;

/**
 * TodoTask - Represents a single task in the TODO list
 */
public class TodoTask {
    private String text;
    private boolean completed;
    
    /**
     * Constructor
     * @param text The task text
     */
    public TodoTask(String text) {
        this.text = text;
        this.completed = false;
    }
    
    /**
     * Get the task text
     * @return The task text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Set the task text
     * @param text The new task text
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Check if the task is completed
     * @return True if completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }
    
    /**
     * Set the completion status of the task
     * @param completed The new completion status
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    /**
     * Toggle the completion status of the task
     */
    public void toggleCompleted() {
        this.completed = !this.completed;
    }
    
    /**
     * String representation of the task
     */
    @Override
    public String toString() {
        return text;
    }
}
