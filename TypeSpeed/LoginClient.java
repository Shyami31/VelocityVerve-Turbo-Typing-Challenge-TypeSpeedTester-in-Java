import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginClient {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public LoginClient() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);  // Center the frame on the screen

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Typing Speed Tester Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        loginButton = new JButton("Login");
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(loginButton, constraints);

        frame.add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (login(username, password)) {
                    frame.setVisible(false);
                    frame.dispose();

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TypingSpeedTester tester = new TypingSpeedTester();
                            tester.frame.setVisible(true);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(frame, "Login failed. Invalid credentials.");
                }
            }
        });
    }

    private boolean login(String username, String password) {
        try {
            socket = new Socket("localhost", 12345);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(username);
            writer.println(password);

            String response = reader.readLine();
            return "LOGIN_SUCCESS".equals(response);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginClient loginClient = new LoginClient();
                loginClient.frame.setVisible(true);
            }
        });
    }
}
