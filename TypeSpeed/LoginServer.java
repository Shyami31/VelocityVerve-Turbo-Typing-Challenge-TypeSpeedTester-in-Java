import java.io.*;
import java.net.*;

public class LoginServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Login Server is running. Waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String username = reader.readLine();
            String password = reader.readLine();

            // Simple check for demo purposes, replace with your authentication logic
            if (isValidUser(username, password)) {
                writer.println("LOGIN_SUCCESS");
            } else {
                writer.println("LOGIN_FAILED");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidUser(String username, String password) {
        // Replace with your actual user authentication logic
        return "admin".equals(username) && "password123".equals(password);
    }
}
