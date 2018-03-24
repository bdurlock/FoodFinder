package o.durlock.foodfinder;

/**
 * Class: Food
 * Purpose: This holds an instance of a restaurant with all the given information that you need.
 * Contains many getters and setters.
 */

public class Food {

    private String name, distance, rating, description;

    public Food() {

    }

    public Food(String restaurant, String distance, String rating, String description){
        this.name = restaurant;
        this.distance = distance;
        this.rating = rating;
        this.description = description;
    }

    public String getName() { return name; }

    public String getDistance() { return distance; }

    public String getRating() { return rating; }

    public String getDescription() { return description; }

    public void setRestaurant(String name) { this.name = name; }

    public void setDistance(String distance) { this.distance = distance; }

    public void setRating(String rating) { this.rating = rating; }

    public void setDescription(String description) { this.description = description; }
}
