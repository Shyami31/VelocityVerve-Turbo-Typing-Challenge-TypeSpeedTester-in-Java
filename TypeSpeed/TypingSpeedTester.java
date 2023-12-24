import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class TypingSpeedTester {
    public JFrame frame;
    private JTextArea textArea;
    private JButton startButton;
    private JLabel sentenceLabel;
    private JLabel timerLabel;
    private String[] sentences;
    String sentence;
    private long startTime;
    private Timer timer;
    private Random random;

    public TypingSpeedTester() {
        frame = new JFrame("Typing Speed Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 450);

        textArea = new JTextArea(10, 40);
        textArea.setEditable(false);

        // Create a panel for the image card
        JPanel imageCard = new JPanel();
        ImageIcon imageIcon = new ImageIcon("image2.jpg"); // Replace with your image file path
        JLabel imageLabel = new JLabel(imageIcon);
        imageCard.add(imageLabel);

        sentenceLabel = new JLabel();
        sentenceLabel.setOpaque(false);
        sentenceLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        timerLabel = new JLabel("Time: 0 seconds");
        timerLabel.setOpaque(false);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        startButton = new JButton("Start");

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(sentenceLabel, BorderLayout.NORTH);
        topPanel.add(timerLabel, BorderLayout.SOUTH);
        frame.setLayout(new BorderLayout());
        frame.add(imageCard, BorderLayout.NORTH);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTest();
                frame.remove(imageCard);
                frame.add(topPanel, BorderLayout.NORTH);
            }
        });

        timer = new Timer();
        random = new Random();
    }
    private double calculateAccuracy(String typedText, String originalText) {
        int minLen = Math.min(typedText.length(), originalText.length());
        int correctChars = 0;

        for (int i = 0; i < minLen; i++) {
            if (typedText.charAt(i) == originalText.charAt(i)) {
                correctChars++;
            }
        }

        double accuracy = (double) correctChars / originalText.length() * 100.0;
        return accuracy;
    }
    private String analyzeTypingPerformance(String typedText, String originalText) {
        int minLength = Math.min(typedText.length(), originalText.length());
        int errors = 0;
        char[] typedChars = typedText.toCharArray();
        char[] originalChars = originalText.toCharArray();

        for (int i = 0; i < minLength; i++) {
            if (typedChars[i] != originalChars[i]) {
                errors++;
            }
        }

        if (typedText.equals(""))
        {
            return "Nothing was typed to judge";
        }

        // Determine fingers to concentrate on based on error patterns
        if (errors == 0) {
            return "You typed perfectly";
        } else {
            String errorPositions = "";
            for (int i = 0; i < minLength; i++) {
                if (typedChars[i] != originalChars[i]) {
                    errorPositions += (i + 1) + " ";
                }
            }

            if (errorPositions.contains("1 2 3")) {
                return "Concentrate on your left-hand fingers (ASDF).";
            } else if (errorPositions.contains("6 7 8")) {
                return "Concentrate on your right-hand fingers (JKL;).";
            } else {
                return "Concentrate on improving your typing accuracy.";
            }
        }
    }
    private void startTest() {
        sentence = getRandomSentenceFromDatabase(); // Fetch a random sentence from the database

        sentenceLabel.setText("<html><font color='black'><span style=\"color: black; background-color: pink;\">" + sentence + "</span></font></html");
        textArea.setText("");
        textArea.setEditable(true);
        startTime = System.currentTimeMillis();

        startButton.setText("Stop");
        startButton.removeActionListener(startButton.getActionListeners()[0]);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTest();
            }
        });

        // Start the timer here
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                int elapsedSeconds = (int) (elapsedTime / 1000);
                timerLabel.setText("Time: " + elapsedSeconds + " seconds");

                if (elapsedSeconds >= 60) {
                    stopTest();
                }
            }
        }, 0, 1000);  // Update the timer every 1 second
    }

    // Add this method to retrieve a random sentence from the database
    private String getRandomSentenceFromDatabase() {
        try {
            // Load the JDBC driver (assuming you haven't loaded it elsewhere in your application)
            Class.forName("org.sqlite.JDBC");

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:sqlite:sentence_database.db");

            // Retrieve a random sentence from the database
            String selectSQL = "SELECT sentence FROM sentences ORDER BY RANDOM() LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("sentence");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    private void stopTest() {
        textArea.setEditable(false);
        timer.cancel();
        long endTime = System.currentTimeMillis();
        String typedText = textArea.getText();
        double accuracy = calculateAccuracy(typedText, sentence);
        int typedChars = typedText.length();
        double charsPerMinute = (typedChars / ((endTime - startTime) / 1000.0) * 60);

        JOptionPane.showMessageDialog(frame, "Typing speed: " + charsPerMinute + " characters per minute"+
                "\nTyping speed: " + (charsPerMinute / 4.7) + " words per minute"+"\nAccuracy: " + accuracy + "%");

        // Call the analyzeTypingPerformance function
        String analysis = analyzeTypingPerformance(typedText, sentence);
        JOptionPane.showMessageDialog(frame, "Typing Analysis:\n" + analysis);

        startButton.setText("Start");
        startButton.removeActionListener(startButton.getActionListeners()[0]);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTest();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TypingSpeedTester tester = new TypingSpeedTester();
                tester.frame.setVisible(true);
            }
        });
    }
}