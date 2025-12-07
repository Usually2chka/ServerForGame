package Entyties;

public class Player {
    public int hitPoint;
    public int damage;
    private int positionX;
    private int positionY;
    public boolean isDied;
    private String name;
    private int id;
    public boolean isTurned;
    public int lobbyId = -1;

    public Player() {
        hitPoint = 10;
        damage = 3;
        isDied = false;
    }

    public int getId() {
        return id;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}
