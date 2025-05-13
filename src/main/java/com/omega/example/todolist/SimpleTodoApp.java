package com.omega.example.todolist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * SimpleTodoApp - A simplified TODO list application without CUDA dependencies
 */
public class SimpleTodoApp {
    
    private JFrame frame;
    private JTextField taskInputField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton analyzeButton;
    private JList<TodoTask> taskList;
    private DefaultListModel<TodoTask> listModel;
    private JTextArea outputArea;
    
    private List<TodoTask> tasks = new ArrayList<>();
    
    /**
     * Constructor - initializes the UI
     */
    public SimpleTodoApp() {
        initializeUI();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Omega-AI TODO List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        taskInputField = new JTextField();
        taskInputField.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
        
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        
        JPanel taskPanel = new JPanel(new BorderLayout(5, 5));
        taskPanel.setBorder(BorderFactory.createTitledBorder("Tasks"));
        
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TodoTaskRenderer());
        
        JScrollPane taskScrollPane = new JScrollPane(taskList);
        taskScrollPane.setPreferredSize(new Dimension(300, 400));
        
        taskPanel.add(taskScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });
        
        analyzeButton = new JButton("Analyze Tasks");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeTasks();
            }
        });
        
        buttonPanel.add(deleteButton);
        buttonPanel.add(analyzeButton);
        
        taskPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Task Analysis"));
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setPreferredSize(new Dimension(400, 400));
        
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(taskPanel, BorderLayout.WEST);
        frame.add(outputPanel, BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
    }
    
    /**
     * Add a new task to the list
     */
    private void addTask() {
        String taskText = taskInputField.getText().trim();
        if (!taskText.isEmpty()) {
            TodoTask task = new TodoTask(taskText);
            tasks.add(task);
            listModel.addElement(task);
            taskInputField.setText("");
        }
    }
    
    /**
     * Delete the selected task from the list
     */
    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex);
            listModel.remove(selectedIndex);
        }
    }
    
    /**
     * Analyze tasks (simple version without LLM)
     */
    private void analyzeTasks() {
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please add some tasks first.", "No Tasks", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("Task Analysis (LLM functionality disabled):\n\n");
        analysis.append("Your tasks:\n");
        analysis.append(formatTaskList());
        analysis.append("\nSimple Analysis:\n");
        analysis.append("- You have " + tasks.size() + " tasks in your list.\n");
        
        if (tasks.size() > 3) {
            analysis.append("- Consider prioritizing your tasks to focus on the most important ones.\n");
        }
        
        if (tasks.size() > 5) {
            analysis.append("- Your task list is getting long. Consider breaking down larger tasks into smaller ones.\n");
        }
        
        outputArea.setText(analysis.toString());
    }
    
    /**
     * Format the task list as a numbered list
     * @return Formatted task list string
     */
    private String formatTaskList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1) + ". " + tasks.get(i).getText() + "\n");
        }
        return sb.toString();
    }
    
    /**
     * Show the application window
     */
    public void show() {
        frame.setVisible(true);
    }
    
    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleTodoApp app = new SimpleTodoApp();
                app.show();
            }
        });
    }
    
    /**
     * Custom renderer for todo tasks
     */
    private class TodoTaskRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof TodoTask) {
                TodoTask task = (TodoTask) value;
                label.setText(task.getText());
                
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createCompoundBorder(
                    label.getBorder(), 
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
            
            return label;
        }
    }
}
