package dataaccess;

public class GameData {
    private String gameID;
    private String gameName;
    private String whitePlayer;
    private String blackPlayer;

    // Constructor
    public GameData(String gameID, String gameName, String whitePlayer, String blackPlayer) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
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

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameData gameData = (GameData) o;

        if (!gameID.equals(gameData.gameID)) return false;
        if (!gameName.equals(gameData.gameName)) return false;
        if (!whitePlayer.equals(gameData.whitePlayer)) return false;
        return blackPlayer.equals(gameData.blackPlayer);
    }

    @Override
    public int hashCode() {
        int result = gameID.hashCode();
        result = 31 * result + gameName.hashCode();
        result = 31 * result + whitePlayer.hashCode();
        result = 31 * result + blackPlayer.hashCode();
        return result;
    }
}

