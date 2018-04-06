package o.durlock.foodfinder;

/**
 * Class: Food
 * Purpose: This holds an instance of a restaurant with all the given information that you need.
 * Contains many getters and setters.
 */

public class Food {

    private String name, description, notes, type, who;
    private float rating;
    private int cost;

    public Food() {

    }

    public Food(String name, String description, String notes, String type, String who, float rating, int cost){
        this.name = name;
        this.description = description;
        this.notes = notes;
        this.type = type;
        this.who = who;
        this.rating = rating;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNotes() {
        return notes;
    }

    public String getType() {
        return type;
    }

    public String getWho() {
        return who;
    }

    public float getRating() {
        return rating;
    }

    public int getCost() {
        return cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
