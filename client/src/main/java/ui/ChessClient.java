package ui;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import model.AuthToken;
import model.GameData;
import ui.DisplayChessBoard;

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
                postloginMenu();
            }
        }
    }

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
                login();
                break;
            case "register":
                register();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private static void displayHelp() {
        System.out.println("Commands Available:");
        System.out.println("Help - Displays available commands");
        System.out.println("Quit - Exit the program");
        System.out.println("Login - Log in to your account");
        System.out.println("Register - Create a new account");
    }

    private static void quitProgram() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

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

    private static void login() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.println("Enter password: ");
        String password = scanner.nextLine().trim();

        try {
            currentToken = serverFacade.login(username, password);
            System.out.println("Login successful! Welcome " + currentToken.getUsername());
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static void postloginMenu() {
        System.out.println("\nPostlogin Menu:");
        System.out.println("Help - Displays available commands");
        System.out.println("Logout - Log out of your account");
        System.out.println("Create Game - Create a new game");
        System.out.println("List Games - List all existing games");
        System.out.println("Play Game - Join an existing game");

        String command = scanner.nextLine().toLowerCase().trim();

        switch (command) {
            case "help":
                displayHelp();
                break;
            case "logout":
                logout();
                break;
            case "create game":
                createGame();
                break;
            case "list games":
                listGames();
                break;
            case "play game":
                //playGame();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    // Handle logging out
    private static void logout() {
        currentToken = null;
        System.out.println("You have been logged out.");
    }

    // Handle creating a game
    private static void createGame() {
        System.out.println("Enter a name for the new game: ");
        String gameName = scanner.nextLine().trim();

        if (gameName.isEmpty()) {
            System.out.println("Game name cannot be empty. Please try again.");
            return;
        }

        try {
            String gameID = serverFacade.createGame(gameName, currentToken);
            System.out.println("Game created successfully! Game ID: " + gameID);
        } catch (Exception e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }


    // Handle listing games
    private static void listGames() {
        try {
            List<GameData> games = serverFacade.listGames(currentToken); // Call listGames from ServerFacade
            if (games.isEmpty()) {
                System.out.println("No games available.");
            } else {
                System.out.println("Games currently available:");
                for (int i = 0; i < games.size(); i++) {
                    GameData game = games.get(i);
                    System.out.println((i + 1) + ". Game ID: " + game.getGameID() + ", Game Name: " + game.getGameName());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve games: " + e.getMessage());
        }
    }
    // handle playing a game
    public static void playGame() {
        try {
            List<GameData> games = serverFacade.listGames(currentToken); // Call listGames from ServerFacade
            if (games.isEmpty()) {
                System.out.println("No available games to join.");
                return;
            }
            System.out.println("Available Games:");
            for (int i = 0; i < games.size(); i++) {
                GameData game = games.get(i);
                System.out.println((i + 1) + ". " + game.getGameName() + " (White: " + game.getWhiteUsername() + ", Black: " + game.getBlackUsername() + ")");
            }
            System.out.print("Enter the number of the game you want to join: ");
            String input = scanner.nextLine().trim();

            int gameNumber;
            try {
                gameNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid game number.");
                return;
            }
            if (gameNumber < 1 || gameNumber > games.size()) {
                System.out.println("Invalid game number. Please try again.");
                return;
            }
            GameData selectedGame = games.get(gameNumber - 1);
            System.out.print("Enter the color you want to play (WHITE/BLACK): ");
            String playerColor = scanner.nextLine().trim().toUpperCase();
            try {
                serverFacade.joinGame(selectedGame.getGameID(), playerColor, currentToken);
                System.out.println("Successfully joined the game " + selectedGame.getGameName() + " as " + playerColor);
            } catch (Exception e) {
                System.out.println("Error joining game: " + e.getMessage());
                return;
            }
            DisplayChessBoard.displayChessBoard();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //handle observing game next (last one I think)
}


