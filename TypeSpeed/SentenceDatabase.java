import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SentenceDatabase {
    private Connection connection;
    public SentenceDatabase() {
        connect();
        //createTable();
        //insertSentences();
    }

    private void connect() {
        try {
            // Load the JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:sqlite:sentence_database.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void createTable() {
        try {
            // Create the sentences table if it does not exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS TestingSentences (id INTEGER PRIMARY KEY AUTOINCREMENT, sentence TEXT)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    /*private void insertSentences() {
        try {
            // Insert sentences into the database
            String[] sentences = new String[]{
                            "In the depths of winter, I finally learned that within me there lay an invincible summer. It is not in the stars to hold our destiny but in ourselves. Life is either a daring adventure or nothing at all.\n",
                            "Two roads diverged in a wood, and I—I took the one less traveled by, And that has made all the difference. The only way to do great work is to love what you do. If you haven't found it yet, keep looking. Don't settle.\n",
                            "To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment. The purpose of our lives is to be happy. The greatest glory in living lies not in never falling, but in rising every time we fall.\n",
                            "The journey of a thousand miles begins with a single step. The only limit to our realization of tomorrow will be our doubts of today. Believe you can and you're halfway there. Success is not final, failure is not fatal: It is the courage to continue that counts.\n",
                            "It does not matter how slowly you go as long as you do not stop. The future belongs to those who believe in the beauty of their dreams. Life is really simple, but we insist on making it complicated. The only impossible journey is the one you never begin.\n"
            };

            String insertSQL = "INSERT INTO TestingSentences (sentence) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                for (String sentence : sentences) {
                    preparedStatement.setString(1, sentence);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    public String getRandomSentence() {
        try {
            // Retrieve a random sentence from the database

            String selectSQL = "SELECT sentence FROM TestingSentences ORDER BY RANDOM() LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("sentence");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SentenceDatabase sentenceDatabase = new SentenceDatabase();
        String randomSentence = sentenceDatabase.getRandomSentence();
        System.out.println(randomSentence);
    }
}
