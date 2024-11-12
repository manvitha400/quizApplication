import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;

public class QuizApp extends JFrame implements ActionListener {
    private JTextField idField, nameField;
    private JButton startButton, nextButton, prevButton, submitButton;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private ButtonGroup group;

    private int currentQuestion = 0;
    private int[] answers = new int[5]; // Store user answers (-1 means not answered yet)
    private final int[] correctAnswers = {1, 0, 1, 0, 1}; // Correct answers (index-based)

    private final String[] questions = {
            "1. What is the number of primitive datatypes in Java?",
            "2. Exception created by try block is caught in which block?",
            "3. Identify the return type of a method that does not return any value?",
            "4. Which of the following is used to find and fix bugs in the program?",
            "5. Identify the modifier which cannot be used for a constructor?"
    };

    private final String[][] optionsText = {
            {"7", "8", "9"}, // Options for Question 1
            {"catch", "throw", "finally"}, // Options for Question 2
            {"double", "void", "int"}, // Options for Question 3
            {"JDB", "JDK", "JVM"}, // Options for Question 4
            {"Public", "Private", "Static"} // Options for Question 5
    };

    private JPanel topPanel, centerPanel, bottomPanel;

    // Constructor to set up the UI
    public QuizApp() {
        setTitle("Quiz Application");
        setLayout(new BorderLayout());

        // Top panel for ID and name input
        topPanel = new JPanel(new GridLayout(3, 2));
        topPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        topPanel.add(idField);
        topPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        topPanel.add(nameField);

        startButton = new JButton("Start Quiz");
        startButton.addActionListener(this);
        topPanel.add(new JLabel()); // Empty label for alignment
        topPanel.add(startButton);

        // Center panel for questions and options
        centerPanel = new JPanel(new GridLayout(5, 1));
        questionLabel = new JLabel("Please start the quiz by entering your ID and Name.");
        centerPanel.add(questionLabel);

        options = new JRadioButton[3]; // Create radio button options
        group = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            options[i] = new JRadioButton();
            options[i].setVisible(false); // Hide options initially
            options[i].addActionListener(this); // Handle user selection
            group.add(options[i]);
            centerPanel.add(options[i]);
        }
        centerPanel.setVisible(false);

        // Bottom panel for navigation buttons
        bottomPanel = new JPanel(new FlowLayout());
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit");

        prevButton.addActionListener(this);
        nextButton.addActionListener(this);
        submitButton.addActionListener(this);

        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(submitButton);
        bottomPanel.setVisible(false);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            if (validateUserInput()) {
                topPanel.setVisible(false); // Hide ID and Name input panel
                centerPanel.setVisible(true);
                bottomPanel.setVisible(true);
                updateQuestion(); // Display the first question
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both ID and Name.");
            }
        } else if (e.getSource() == nextButton) {
            saveAnswer(); // Save the current answer
            if (currentQuestion < questions.length - 1) {
                currentQuestion++;
                updateQuestion();
            }
        } else if (e.getSource() == prevButton) {
            saveAnswer(); // Save the current answer
            if (currentQuestion > 0) {
                currentQuestion--;
                updateQuestion();
            }
        } else if (e.getSource() == submitButton) {
            saveAnswer(); // Save the final answer
            int score = calculateScore();
            JOptionPane.showMessageDialog(this, "Your Score: " + score + "/5");
            saveDetailsToFile(score);
        } else {
            // Handle radio button selection
            for (int i = 0; i < options.length; i++) {
                if (e.getSource() == options[i]) {
                    answers[currentQuestion] = i; // Save the selected answer
                }
            }
        }
    }

    private boolean validateUserInput() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        return !id.isEmpty() && !name.isEmpty();
    }

    private void updateQuestion() {
        questionLabel.setText(questions[currentQuestion]);
        group.clearSelection(); // Clear previous selection

        // Set options for the current question and make them visible
        for (int i = 0; i < options.length; i++) {
            options[i].setText(optionsText[currentQuestion][i]);
            options[i].setVisible(true); // Show options only when updating the question
        }

        if (answers[currentQuestion] != -1) {
            // Select the saved answer if it exists
            options[answers[currentQuestion]].setSelected(true);
        } else {
            group.clearSelection(); // Ensure no selection by default
        }

        prevButton.setEnabled(currentQuestion > 0);
        nextButton.setEnabled(currentQuestion < questions.length - 1);
        submitButton.setEnabled(currentQuestion == questions.length - 1);
    }

    private void saveAnswer() {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                answers[currentQuestion] = i; // Save selected option index
                return;
            }
        }
        answers[currentQuestion] = -1; // No answer selected
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < answers.length; i++) {
            if (answers[i] == correctAnswers[i]) {
                score++;
            }
        }
        return score;
    }

    private void saveDetailsToFile(int score) {
        String id = idField.getText();
        String name = nameField.getText();
        try (FileWriter writer = new FileWriter("quiz_results.txt", true)) {
            writer.write("ID: " + id + ", Name: " + name + ", Score: " + score + "/5\n");
            JOptionPane.showMessageDialog(this, "Details saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving details: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizApp());
    }
}
