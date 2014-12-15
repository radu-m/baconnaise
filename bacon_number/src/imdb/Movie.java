package imdb;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: miul
 * Date: 12/1/14
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Movie implements Serializable {
    private int id;
    private String name;
    private Integer year;
    private String rank;
    private ArrayList<Actor> actors;

    public Movie(){
        actors = new ArrayList<>();
    }

    public void setId(Integer id){
        id = id;
    }

    public void setYear(Integer y){
        year = y;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getYear(){
        return year;
    }

    public String getRank() {
        return rank;
    }

    public Integer getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public boolean hasActor(int id){
        for(Actor a: actors){
            if(a.getId() == id) return true;
        }
        return false;
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    public void addActor(Actor person) {
        actors.add(person);
    }

}
