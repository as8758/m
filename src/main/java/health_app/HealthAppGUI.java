package health_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;

public class HealthAppGUI extends JFrame {
    private UserProfile currentUser;
    private FoodDatabase db;
    private LogManager logManager;
    private RecipeManager recipeManager;
    private DataPersistence persistence;
    
    private JPanel mainPanel;
    private JTextArea logDisplay;
    private JComboBox<String> foodComboBox;
    private JTextField servingsField;
    private JButton addFoodButton;
    private JButton viewLogButton;
    private JButton manageFoodsButton;
    private ChartPanel chartPanel;
    
    public HealthAppGUI() {
        super("HealthNCare App");
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Initialize data
        persistence = new DataPersistence();
        db = persistence.loadFoodDatabase();
        
        if (db.foodMap.isEmpty()) {
            initializeFoodDatabase();
        }
        
        logManager = new LogManager(db, persistence);
        recipeManager = new RecipeManager(db, persistence);
        
        // Prompt for username
        String username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username != null && !username.trim().isEmpty()) {
            currentUser = new UserAuthenticator(persistence).login(username);
            updateFoodComboBox();
        } else {
            JOptionPane.showMessageDialog(this, "Username is required. Exiting...");
            System.exit(0);
        }
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout());
        logDisplay = new JTextArea(10, 40);
        logDisplay.setEditable(false);
        foodComboBox = new JComboBox<>();
        servingsField = new JTextField(5);
        addFoodButton = new JButton("Add Food");
        viewLogButton = new JButton("View Today's Log");
        manageFoodsButton = new JButton("Manage Foods");
    }
    
    private void setupLayout() {
        // Create top panel for food selection
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Food:"));
        topPanel.add(foodComboBox);
        topPanel.add(new JLabel("Servings:"));
        topPanel.add(servingsField);
        topPanel.add(addFoodButton);
        
        // Create center panel for log display and chart
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        JScrollPane scrollPane = new JScrollPane(logDisplay);
        centerPanel.add(scrollPane);
        
        // Initialize empty chart panel
        DefaultPieDataset dataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(
            "Macro Breakdown",
            dataset,
            true,
            true,
            false
        );
        chartPanel = new ChartPanel(chart);
        centerPanel.add(chartPanel);
        
        // Create bottom panel for buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(viewLogButton);
        bottomPanel.add(manageFoodsButton);
        
        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void setupEventHandlers() {
        addFoodButton.addActionListener(e -> addFoodToLog());
        viewLogButton.addActionListener(e -> viewTodaysLog());
        manageFoodsButton.addActionListener(e -> showManageFoodsDialog());
    }
    
    private void updateFoodComboBox() {
        foodComboBox.removeAllItems();
        for (Map.Entry<String, RecipeComponent> entry : db.foodMap.entrySet()) {
            foodComboBox.addItem(entry.getKey());
        }
    }
    
    private void addFoodToLog() {
        String foodName = (String) foodComboBox.getSelectedItem();
        if (foodName == null) {
            JOptionPane.showMessageDialog(this, "Please select a food first.");
            return;
        }
        
        try {
            double servings = Double.parseDouble(servingsField.getText());
            if (servings <= 0) {
                JOptionPane.showMessageDialog(this, "Servings must be greater than 0.");
                return;
            }
            
            logManager.logFood(currentUser, LocalDate.now(), foodName, servings);
            JOptionPane.showMessageDialog(this, "Food added to log successfully!");
            viewTodaysLog();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for servings.");
        }
    }
    
    private void viewTodaysLog() {
        DailyLog todayLog = currentUser.getLog(LocalDate.now());
        StringBuilder logText = new StringBuilder("Today's Log:\n\n");
        
        if (todayLog.entries.isEmpty()) {
            logText.append("No entries for today.");
            updateMacroChart(new DefaultPieDataset());
        } else {
            double totalCalories = 0;
            double totalFat = 0;
            double totalCarbs = 0;
            double totalProtein = 0;
            double totalSodium = 0;
            
            for (LogEntry entry : todayLog.entries) {
                RecipeComponent food = db.getFood(entry.foodName);
                if (food != null) {
                    double calories = food.getCalories() * entry.servings;
                    totalCalories += calories;
                    totalFat += food.getFat() * entry.servings;
                    totalCarbs += food.getCarbs() * entry.servings;
                    totalProtein += food.getProtein() * entry.servings;
                    totalSodium += food.getSodium() * entry.servings;
                    
                    logText.append(String.format("%s: %.1f servings (%.0f calories)\n", 
                        food.name, entry.servings, calories));
                }
            }
            logText.append(String.format("\nTotal Calories: %.0f", totalCalories));
            
            // Update the pie chart with macro data
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Fat", totalFat);
            dataset.setValue("Carbs", totalCarbs);
            dataset.setValue("Protein", totalProtein);
            dataset.setValue("Sodium", totalSodium);
            updateMacroChart(dataset);
        }
        
        logDisplay.setText(logText.toString());
    }
    
    private void updateMacroChart(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
            "Macro Breakdown (g)",
            dataset,
            true,
            true,
            false
        );
        chartPanel.setChart(chart);
    }
    
    private void showManageFoodsDialog() {
        JDialog manageDialog = new JDialog(this, "Manage Foods", true);
        manageDialog.setLayout(new BorderLayout());
        
        // Create food list panel
        JTextArea foodList = new JTextArea(10, 40);
        foodList.setEditable(false);
        updateFoodList(foodList);
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add New Food");
        JButton closeButton = new JButton("Close");
        
        addButton.addActionListener(e -> showAddFoodDialog(manageDialog));
        closeButton.addActionListener(e -> manageDialog.dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(closeButton);
        
        manageDialog.add(new JScrollPane(foodList), BorderLayout.CENTER);
        manageDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        manageDialog.pack();
        manageDialog.setLocationRelativeTo(this);
        manageDialog.setVisible(true);
    }
    
    private void updateFoodList(JTextArea foodList) {
        StringBuilder listText = new StringBuilder("Available Foods:\n\n");
        for (Map.Entry<String, RecipeComponent> entry : db.foodMap.entrySet()) {
            FoodItem food = (FoodItem) entry.getValue();
            listText.append(String.format("%s: %.0f calories, %.1f fat, %.1f carbs, %.1f protein, %.1f sodium\n",
                food.name, food.calories, food.fat, food.carbs, food.protein, food.sodium));
        }
        foodList.setText(listText.toString());
    }
    
    private void showAddFoodDialog(JDialog parent) {
        JDialog addDialog = new JDialog(parent, "Add New Food", true);
        addDialog.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JLabel nameLabel = new JLabel("Food Name:");
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Fruits", "Vegetables", "Protein", "Grains", "Dairy", "Nuts", "Snacks"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        inputPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel caloriesLabel = new JLabel("Calories:");
        JTextField caloriesField = new JTextField(20);
        inputPanel.add(caloriesLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(caloriesField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel fatLabel = new JLabel("Fat (g):");
        JTextField fatField = new JTextField(20);
        inputPanel.add(fatLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(fatField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel carbsLabel = new JLabel("Carbs (g):");
        JTextField carbsField = new JTextField(20);
        inputPanel.add(carbsLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(carbsField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel proteinLabel = new JLabel("Protein (g):");
        JTextField proteinField = new JTextField(20);
        inputPanel.add(proteinLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(proteinField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel sodiumLabel = new JLabel("Sodium (mg):");
        JTextField sodiumField = new JTextField(20);
        inputPanel.add(sodiumLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(sodiumField, gbc);
        
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String category = (String) categoryComboBox.getSelectedItem();
                double calories = Double.parseDouble(caloriesField.getText());
                double fat = Double.parseDouble(fatField.getText());
                double carbs = Double.parseDouble(carbsField.getText());
                double protein = Double.parseDouble(proteinField.getText());
                double sodium = Double.parseDouble(sodiumField.getText());
                
                FoodItem newFood = new FoodItem(name, category, calories, fat, carbs, protein, sodium);
                db.addFood(name, newFood);
                persistence.saveFoodDatabase(db);
                
                updateFoodComboBox();
                updateFoodList((JTextArea) ((JScrollPane) parent.getContentPane().getComponent(0)).getViewport().getView());
                addDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Please enter valid numbers for all nutritional values.");
            }
        });
        
        cancelButton.addActionListener(e -> addDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        addDialog.add(inputPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        addDialog.pack();
        addDialog.setLocationRelativeTo(parent);
        addDialog.setVisible(true);
    }
    
    private JPanel createLabeledField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        panel.add(textField);
        return panel;
    }
    
    private void initializeFoodDatabase() {
        db.addFood("bread", new FoodItem("bread", "Grains", 80.0, 1.0, 15.0, 3.0, 170.0));
        db.addFood("apple", new FoodItem("apple", "Fruits", 95.0, 0.3, 25.0, 0.5, 2.0));
        db.addFood("banana", new FoodItem("banana", "Fruits", 105.0, 0.4, 27.0, 1.3, 1.0));
        db.addFood("chicken breast", new FoodItem("chicken breast", "Protein", 165.0, 3.6, 0.0, 31.0, 74.0));
        db.addFood("rice", new FoodItem("rice", "Grains", 130.0, 0.3, 28.0, 2.7, 1.0));
        persistence.saveFoodDatabase(db);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HealthAppGUI());
    }
} 