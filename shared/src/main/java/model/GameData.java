package model;

public class GameData {
    private String gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;

    // Constructor
    public GameData(String gameID, String gameName, String whiteUsername, String blackUsername) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
    }

    // Getters and setters
    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameData gameData = (GameData) o;

        if (!gameID.equals(gameData.gameID)) return false;
        if (!gameName.equals(gameData.gameName)) return false;
        if (!whiteUsername.equals(gameData.whiteUsername)) return false;
        return blackUsername.equals(gameData.blackUsername);
    }

    @Override
    public int hashCode() {
        int result = gameID.hashCode();
        result = 31 * result + gameName.hashCode();
        result = 31 * result + whiteUsername.hashCode();
        result = 31 * result + blackUsername.hashCode();
        return result;
    }
}

