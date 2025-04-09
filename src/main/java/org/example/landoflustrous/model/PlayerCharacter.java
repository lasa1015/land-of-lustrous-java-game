package org.example.landoflustrous.model;

public class PlayerCharacter extends Coordinated {

    private String name;
    private int carbonHP;
    private int gemNumber;
    private int gemScore;


    public PlayerCharacter(int x, int y, String name) {
        super(x, y);
        this.name = name;
        this.carbonHP = 1000;
        this.gemNumber = 0;
        this.gemScore = 0;
    }

    public int getGemNumber() {
        return gemNumber;
    }

    public int getGemScore() {
        return gemScore;
    }

    public void addGemNumber(int gemNumber) {
        this.gemNumber += gemNumber;

    }

    public void addGemScore(int gemScore) {
        this.gemScore += gemScore;
    }


    // Getter Setter Methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public int getCarbonHP() {
        return carbonHP;
    }


    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void substractCarbonHP(int routeCost) {
        this.carbonHP -= routeCost;
    }

}

