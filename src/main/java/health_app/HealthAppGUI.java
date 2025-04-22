package health_app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import org.jfree.chart.title.TextTitle;
import java.awt.Font;

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
    
    // Define colors for a better look
    private final Color PRIMARY_COLOR = new Color(56, 142, 60);  // Green
    private final Color SECONDARY_COLOR = new Color(238, 238, 238);  // Light Grey
    private final Color ACCENT_COLOR = new Color(0, 121, 107);  // Teal
    private final Color TEXT_COLOR = new Color(33, 33, 33);  // Dark Grey
    private final Color BACKGROUND_COLOR = new Color(250, 250, 250);  // Off-White
    
    public HealthAppGUI() {
        super("HealthNCare App");
        setBackground(BACKGROUND_COLOR);
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
            viewTodaysLog(); // Show log on startup
        } else {
            JOptionPane.showMessageDialog(this, "Username is required. Exiting...");
            System.exit(0);
        }
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create styled components
        logDisplay = new JTextArea(10, 40);
        logDisplay.setEditable(false);
        logDisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logDisplay.setBackground(SECONDARY_COLOR);
        logDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        foodComboBox = new JComboBox<>();
        foodComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        foodComboBox.setBackground(Color.WHITE);
        
        servingsField = new JTextField(5);
        servingsField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        addFoodButton = createStyledButton("Add Food");
        viewLogButton = createStyledButton("View Today's Log");
        manageFoodsButton = createStyledButton("Manage Foods");
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    private void setupLayout() {
        // Create top panel for food selection with improved spacing
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Daily Food Tracker");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel foodLabel = new JLabel("Select Food:");
        foodLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel servingsLabel = new JLabel("Servings:");
        servingsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        inputPanel.add(foodLabel);
        inputPanel.add(foodComboBox);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(servingsLabel);
        inputPanel.add(servingsField);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(addFoodButton);
        
        topPanel.add(inputPanel);
        
        // Create center panel for log display and chart with improved proportions
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Styled log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(BACKGROUND_COLOR);
        JLabel logLabel = new JLabel("Food Log");
        logLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        logLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        logPanel.add(logLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(logDisplay);
        scrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Initialize chart panel with styling
        DefaultPieDataset dataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(
            "Macro Breakdown", dataset, true, true, false
        );
        
        // Style the chart
        chart.setBackgroundPaint(BACKGROUND_COLOR);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(BACKGROUND_COLOR);
        plot.setOutlinePaint(null);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.setTitle(new TextTitle("Macro Breakdown", 
                        new Font("SansSerif", Font.BOLD, 16)));
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(BACKGROUND_COLOR);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        
        centerPanel.add(logPanel);
        centerPanel.add(chartPanel);
        
        // Create bottom panel for buttons with improved spacing
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
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
        setSize(new Dimension(800, 600)); // Fixed size for better appearance
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
            servingsField.setText(""); // Clear the field after adding
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
            dataset.setValue("Fat (g)", totalFat);
            dataset.setValue("Carbs (g)", totalCarbs);
            dataset.setValue("Protein (g)", totalProtein);
            
            // Create a color scheme for the chart
            updateMacroChart(dataset);
        }
        
        logDisplay.setText(logText.toString());
    }
    
    private void updateMacroChart(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
            "Macro Breakdown",
            dataset,
            true,
            true,
            false
        );
        
        // Style the chart
        chart.setBackgroundPaint(BACKGROUND_COLOR);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(BACKGROUND_COLOR);
        plot.setOutlinePaint(null);
        plot.setSectionPaint("Fat (g)", new Color(255, 99, 71));  // Tomato red
        plot.setSectionPaint("Carbs (g)", new Color(70, 130, 180));  // Steel blue
        plot.setSectionPaint("Protein (g)", new Color(50, 205, 50));  // Lime green
        
        chart.setTitle(new TextTitle("Macro Breakdown", 
                        new Font("SansSerif", Font.BOLD, 16)));
        
        chartPanel.setChart(chart);
    }
    
    private void showManageFoodsDialog() {
        JDialog manageDialog = new JDialog(this, "Manage Foods", true);
        manageDialog.setLayout(new BorderLayout(10, 10));
        manageDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Available Foods");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create food list panel
        JTextArea foodList = new JTextArea(15, 50);
        foodList.setEditable(false);
        foodList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        foodList.setBackground(SECONDARY_COLOR);
        foodList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateFoodList(foodList);
        
        JScrollPane scrollPane = new JScrollPane(foodList);
        scrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton addButton = createStyledButton("Add New Food");
        JButton closeButton = createStyledButton("Close");
        
        addButton.addActionListener(e -> showAddFoodDialog(manageDialog));
        closeButton.addActionListener(e -> manageDialog.dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(closeButton);
        
        // Add components to dialog
        manageDialog.add(titlePanel, BorderLayout.NORTH);
        manageDialog.add(scrollPane, BorderLayout.CENTER);
        manageDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        manageDialog.setSize(600, 500);
        manageDialog.setLocationRelativeTo(this);
        manageDialog.setVisible(true);
    }
    
    private void updateFoodList(JTextArea foodList) {
        StringBuilder listText = new StringBuilder("Available Foods:\n\n");
        
        // Add column headers
        listText.append(String.format("%-20s %-10s %-8s %-8s %-8s %-8s\n", 
            "Name", "Calories", "Fat", "Carbs", "Protein", "Sodium"));
        listText.append("----------------------------------------------------------\n");
        
        for (Map.Entry<String, RecipeComponent> entry : db.foodMap.entrySet()) {
            FoodItem food = (FoodItem) entry.getValue();
            listText.append(String.format("%-20s %-10.0f %-8.1f %-8.1f %-8.1f %-8.1f\n",
                food.name, food.calories, food.fat, food.carbs, food.protein, food.sodium));
        }
        foodList.setText(listText.toString());
    }
    
    private void showAddFoodDialog(JDialog parent) {
        JDialog addDialog = new JDialog(parent, "Add New Food", true);
        addDialog.setLayout(new BorderLayout(10, 10));
        addDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Add New Food Item");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Create styled components
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        
        JLabel nameLabel = new JLabel("Food Name:");
        nameLabel.setFont(labelFont);
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(labelFont);
        String[] categories = {"Fruits", "Vegetables", "Protein", "Grains", "Dairy", "Nuts", "Snacks"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel caloriesLabel = new JLabel("Calories:");
        caloriesLabel.setFont(labelFont);
        JTextField caloriesField = new JTextField(20);
        caloriesField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel fatLabel = new JLabel("Fat (g):");
        fatLabel.setFont(labelFont);
        JTextField fatField = new JTextField(20);
        fatField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel carbsLabel = new JLabel("Carbs (g):");
        carbsLabel.setFont(labelFont);
        JTextField carbsField = new JTextField(20);
        carbsField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel proteinLabel = new JLabel("Protein (g):");
        proteinLabel.setFont(labelFont);
        JTextField proteinField = new JTextField(20);
        proteinField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel sodiumLabel = new JLabel("Sodium (mg):");
        sodiumLabel.setFont(labelFont);
        JTextField sodiumField = new JTextField(20);
        sodiumField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Add components to the panel
        gbc.anchor = GridBagConstraints.WEST;
        
        inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(caloriesLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(caloriesField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(fatLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(fatField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(carbsLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(carbsField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(proteinLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(proteinField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(sodiumLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(sodiumField, gbc);
        
        // Create buttons
        JButton addButton = createStyledButton("Add");
        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.setBackground(new Color(158, 158, 158)); // Gray color for cancel
        
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Please enter a food name.");
                    return;
                }
                
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
                updateFoodList((JTextArea) ((JScrollPane) parent.getContentPane().getComponent(1)).getViewport().getView());
                addDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Please enter valid numbers for all nutritional values.");
            }
        });
        
        cancelButton.addActionListener(e -> addDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        addDialog.add(titlePanel, BorderLayout.NORTH);
        addDialog.add(inputPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        addDialog.pack();
        addDialog.setLocationRelativeTo(parent);
        addDialog.setVisible(true);
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
        // Set look and feel to system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new HealthAppGUI());
    }
}
