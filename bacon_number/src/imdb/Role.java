package imdb;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: miul
 * Date: 12/8/14
 * Time: 7:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Role implements Serializable{

    private int actor_id;
    private int movie_id;
    private String role;

    public Role() {

    }

    public int getActor_id() {
        return actor_id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public String getRole() {
        return role;
    }
}