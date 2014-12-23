package imdb;

import com.google.gson.annotations.Expose;

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
public class Actor implements Serializable, Comparable<Actor> {
    @Expose private int id;
    @Expose private String first_name;
    @Expose private String last_name;
    @Expose private String gender;
    @Expose private int filmCount;
    private Actor prevActor;
    private int number;

    private double avgNumber;
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

    public double getAvgNumber() {
        return avgNumber;
    }

    public void setAvgNumber(double avgNumber) {
        this.avgNumber = avgNumber;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Integer myCoStarsSize() {
        int size = 0;
        if (myCoStars != null) {
            size = myCoStars.size();
        }
        return size;
    }

    /**
     *if (this == aThat) return EQUAL;
     * @param a Actor
     * @return int
     */
    @Override
    public int compareTo(Actor a) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == a) return EQUAL;
        if (this.myCoStarsSize() < a.myCoStarsSize()) return BEFORE;
        if (this.myCoStarsSize() > a.myCoStarsSize()) return AFTER;
        return 0;
    }
}