package ui;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import model.AuthToken;

public class ChessClient {

    private static ServerFacade serverFacade;
    private static AuthToken currentToken = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        serverFacade = new ServerFacade(8080);
        while (true) {
            if (currentToken == null) {
                preloginMenu();
            } else {
                break;
                //postloginMenu(); // will implement this later
            }
        }
    }

    //Pre-login menu is for login, register, help, and quit
    private static void preloginMenu() {
        System.out.println("\nPrelogin Menu:");
        System.out.println("Help - Displays available commands");
        System.out.println("Quit - Exit the program");
        System.out.println("Login - Log in to your account");
        System.out.println("Register - Create a new account");

        String command = scanner.nextLine().toLowerCase().trim();

        switch (command) {
            case "help":
                displayHelp();
                break;
            case "quit":
                quitProgram();
                break;
            case "login":
                //login();
                break;
            case "register":
                register();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    //Display Help text
    private static void displayHelp() {
        System.out.println("Commands Available:");
        System.out.println("Help - Displays available commands");
        System.out.println("Quit - Exit the program");
        System.out.println("Login - Log in to your account");
        System.out.println("Register - Create a new account");
    }

    //Exit
    private static void quitProgram() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    //Handle user login
//    private static void login() {
//        System.out.println("Enter username: ");
//        String username = scanner.nextLine().trim();
//
//        System.out.println("Enter password: ");
//        String password = scanner.nextLine().trim();
//
//        try {
//            // Attempt to login using the ServerFacade
//            currentToken = serverFacade.login(username, password);
//            System.out.println("Login successful! Welcome " + currentToken.getUsername());
//        } catch (Exception e) {
//            System.out.println("Login failed: " + e.getMessage());
//        }
//    }

    //registration
    private static void register() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.println("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.println("Enter email: ");
        String email = scanner.nextLine().trim();

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format. Please try again.");
            return;
        }

        try {
            currentToken = serverFacade.register(username, password, email);
            System.out.println("Registration successful! Welcome " + currentToken.getUsername());
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

