package com.omega.example.todolist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import com.omega.common.data.Tensor;
import com.omega.engine.gpu.CUDAMemoryManager;
import com.omega.engine.gpu.CUDAModules;
import com.omega.engine.nn.layer.gpu.RoPEKernel;
import com.omega.engine.nn.network.Llama3;
import com.omega.engine.nn.network.RunModel;
import com.omega.example.transformer.utils.ModelUtils;
import com.omega.example.transformer.utils.bpe.BPETokenizer3;

/**
 * TodoListApp - A TODO list application with LLM integration using Omega-AI
 */
public class TodoListApp {
    
    private JFrame frame;
    private JTextField taskInputField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton analyzeButton;
    private JList<TodoTask> taskList;
    private DefaultListModel<TodoTask> listModel;
    private JTextArea llmOutputArea;
    
    private Llama3 network;
    private BPETokenizer3 tokenizer;
    private boolean llmInitialized = false;
    
    private List<TodoTask> tasks = new ArrayList<>();
    
    private boolean cudaAvailable;
    
    /**
     * Constructor - initializes the UI
     */
    public TodoListApp() {
        this(false);
    }
    
    /**
     * Constructor with CUDA availability flag
     * @param cudaAvailable Whether CUDA is available
     */
    public TodoListApp(boolean cudaAvailable) {
        this.cudaAvailable = cudaAvailable;
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
        
        analyzeButton = new JButton("Analyze Tasks with LLM");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeTasks();
            }
        });
        
        buttonPanel.add(deleteButton);
        buttonPanel.add(analyzeButton);
        
        taskPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel llmPanel = new JPanel(new BorderLayout(5, 5));
        llmPanel.setBorder(BorderFactory.createTitledBorder("LLM Analysis"));
        
        llmOutputArea = new JTextArea();
        llmOutputArea.setEditable(false);
        llmOutputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        llmOutputArea.setLineWrap(true);
        llmOutputArea.setWrapStyleWord(true);
        
        JScrollPane llmScrollPane = new JScrollPane(llmOutputArea);
        llmScrollPane.setPreferredSize(new Dimension(400, 400));
        
        llmPanel.add(llmScrollPane, BorderLayout.CENTER);
        
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(taskPanel, BorderLayout.WEST);
        frame.add(llmPanel, BorderLayout.CENTER);
        
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
     * Analyze tasks using the LLM
     */
    private void analyzeTasks() {
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please add some tasks first.", "No Tasks", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!cudaAvailable) {
            llmOutputArea.setText("LLM functionality is disabled because CUDA is not available on this system.\n\n" +
                                 "Your tasks:\n" + formatTaskList());
            return;
        }
        
        if (!llmInitialized) {
            initializeLLM();
        }
        
        if (!llmInitialized) {
            llmOutputArea.setText("LLM initialization failed. Please check the console for errors.");
            return;
        }
        
        StringBuilder taskListStr = new StringBuilder();
        taskListStr.append("Here are my tasks:\n");
        taskListStr.append(formatTaskList());
        taskListStr.append("\nPlease analyze these tasks and suggest prioritization, categorization, or any insights:");
        
        String llmResponse = processWithLLM(taskListStr.toString());
        llmOutputArea.setText(llmResponse);
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
     * Initialize the LLM components
     */
    private void initializeLLM() {
        if (!cudaAvailable) {
            llmOutputArea.setText("LLM functionality is disabled because CUDA is not available on this system.");
            return;
        }
        
        try {
            boolean bias = false;
            boolean dropout = false;
            boolean flashAttention = false;
            int max_len = 512;
            int embedDim = 512;
            int head_num = 16;
            int nKVHeadNum = 8;
            int decoderNum = 8;
            int vocab_size = 6400;
            
            String vocabPath = System.getProperty("user.dir") + "/src/main/resources/models/vocab.json";
            String mergesPath = System.getProperty("user.dir") + "/src/main/resources/models/merges.txt";
            
            if (!new java.io.File(vocabPath).exists() || !new java.io.File(mergesPath).exists()) {
                JOptionPane.showMessageDialog(frame, 
                    "Model files not found. Please place vocab.json and merges.txt in src/main/resources/models/", 
                    "Model Files Missing", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            tokenizer = new BPETokenizer3(vocabPath, mergesPath);
            
            network = new Llama3(
                com.omega.engine.loss.LossType.softmax_with_cross_entropy_idx, 
                com.omega.engine.updater.UpdaterType.adamw, 
                head_num, 
                nKVHeadNum, 
                decoderNum, 
                vocab_size, 
                max_len, 
                embedDim, 
                bias, 
                dropout, 
                flashAttention
            );
            
            String modelPath = System.getProperty("user.dir") + "/src/main/resources/models/llama3-model.model";
            if (!new java.io.File(modelPath).exists()) {
                JOptionPane.showMessageDialog(frame, 
                    "Model file not found. Please place llama3-model.model in src/main/resources/models/", 
                    "Model File Missing", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ModelUtils.loadModel(network, modelPath);
            network.RUN_MODEL = RunModel.TEST;
            
            llmInitialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error initializing LLM: " + e.getMessage(), 
                "LLM Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Process input text with the LLM
     * @param inputText The text to process
     * @return The LLM's response
     */
    private String processWithLLM(String inputText) {
        try {
            String qaStr = tokenizer.sos_str() + "user\n" + inputText + tokenizer.eos_str() + "\n";
            int[] idx = tokenizer.encodeInt(qaStr);
            int startLen = idx.length;
            
            Tensor input = loadByTxtToIdx(null, idx);
            Tensor[] pos = RoPEKernel.getCosAndSin(input.number, network.embedDim, network.headNum);
            
            int max_len = 512;
            
            for (int t = 0; t < max_len - startLen; t++) {
                network.time = input.number;
                Tensor cos = pos[0];
                Tensor sin = pos[1];
                Tensor output = network.forward(cos, sin, input);
                output.syncHost();
                int nextIDX = output2NextIDXTopN(output, idx.length - 1, 8);
                idx = java.util.Arrays.copyOf(idx, idx.length + 1);
                idx[idx.length - 1] = nextIDX;
                if (nextIDX == tokenizer.eos) {
                    break;
                }
                input = loadByTxtToIdx(null, idx);
                RoPEKernel.getCosAndSin(input.number, network.embedDim, network.headNum, pos);
            }
            
            int[] awIdx = java.util.Arrays.copyOfRange(idx, startLen, idx.length);
            return tokenizer.decode(awIdx).replaceAll("<s>assistant\n", "");
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing with LLM: " + e.getMessage();
        }
    }
    
    /**
     * Load text to tensor
     * @param testInput The existing tensor or null
     * @param idxs The indices to load
     * @return The loaded tensor
     */
    private Tensor loadByTxtToIdx(Tensor testInput, int[] idxs) {
        testInput = Tensor.createTensor(testInput, idxs.length, 1, 1, 1, true);
        for (int t = 0; t < idxs.length; t++) {
            testInput.data[t] = idxs[t];
        }
        testInput.hostToDevice();
        return testInput;
    }
    
    /**
     * Get next token index from output
     * @param output The output tensor
     * @param idx The current index
     * @param topN The top N tokens to consider
     * @return The next token index
     */
    private int output2NextIDXTopN(Tensor output, int idx, int topN) {
        int vocab_size = output.width;
        float[] probs = new float[vocab_size];
        
        for (int i = 0; i < vocab_size; i++) {
            probs[i] = output.data[idx * vocab_size + i];
        }
        
        int[] indices = new int[topN];
        float[] values = new float[topN];
        
        for (int i = 0; i < topN; i++) {
            indices[i] = -1;
            values[i] = -Float.MAX_VALUE;
        }
        
        for (int i = 0; i < vocab_size; i++) {
            float val = probs[i];
            for (int j = 0; j < topN; j++) {
                if (val > values[j]) {
                    for (int k = topN - 1; k > j; k--) {
                        values[k] = values[k - 1];
                        indices[k] = indices[k - 1];
                    }
                    values[j] = val;
                    indices[j] = i;
                    break;
                }
            }
        }
        
        float sum = 0;
        for (int i = 0; i < topN; i++) {
            values[i] = (float) Math.exp(values[i]);
            sum += values[i];
        }
        
        for (int i = 0; i < topN; i++) {
            values[i] /= sum;
        }
        
        float rand = (float) Math.random();
        float cumulativeProb = 0;
        
        for (int i = 0; i < topN; i++) {
            cumulativeProb += values[i];
            if (rand < cumulativeProb) {
                return indices[i];
            }
        }
        
        return indices[0];
    }
    
    /**
     * Show the application window
     */
    public void show() {
        frame.setVisible(true);
    }
    
    /**
     * Clean up resources when closing
     */
    public void cleanup() {
        if (llmInitialized) {
            CUDAMemoryManager.free();
        }
    }
    
    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TodoListApp app = new TodoListApp();
                app.show();
                
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        app.cleanup();
                    }
                });
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
