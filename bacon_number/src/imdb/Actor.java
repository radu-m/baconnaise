package imdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: miul
 * Date: 12/1/14
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Actor implements Serializable {
    private int id;
    private String first_name;
    private String last_name;
    private String gender;
    private int filmCount;
    private Actor prevActor;
    private int number;
    private HashMap<Actor, Movie> myCoStars;

    private ArrayList<Movie> movies;
    private Movie sharedMovie;

    private boolean visited;

    public Actor() {
        movies = new ArrayList<>();
        number = Integer.MAX_VALUE;
        myCoStars = null;
        prevActor = null;
        sharedMovie = null;
        visited = false;
    }

    public void toggleVisited() {
        visited = !visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public Integer getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return first_name + ' ' + last_name;
    }

    public Movie getSharedMovie() {
        return sharedMovie;
    }

    public void setSharedMovie(Movie sharedMovie) {
        this.sharedMovie = sharedMovie;
    }

    public Actor getPrevActor() {
        return prevActor;
    }

    public void setPrevActor(Actor prevActor) {
        this.prevActor = prevActor;
    }

    public HashMap<Actor, Movie> coStars() {
        if (myCoStars != null)
            return myCoStars;
        myCoStars = new HashMap<>();
        for (Movie m : movies) {
            for (Actor a : m.getActors()) {
                if (a != this && !a.isVisited()) {
                    myCoStars.put(a, m);
                }
            }
        }
        return myCoStars;
    }

    public void addMovies(Movie m) {
        movies.add(m);
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }
}