package parse;
/**
 * Created with IntelliJ IDEA.
 * User: miul
 * Date: 12/8/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import imdb.*;

public class BaconJsonParser {

    private final File folder = new File("/Users/philipriley/Documents/ITU/Algorithm Design 2/imdb_json_tables");
    private HashMap<String, String> jsonFiles = new LinkedHashMap<>();

    private Map<Integer, Actor> actors = new HashMap<>();
    private Map<String, Integer> actorNames = new HashMap<>();
    private Map<Integer, HashSet<Role>> roles = new HashMap<>();
    private Map<Integer, Movie> movies = new HashMap<>();
    private Map<Integer, Director> directors = new HashMap<>();
    private List<String> fileNames = new ArrayList<>();
    private Actor actor;
    private Movie movie;

    public BaconJsonParser() throws FileNotFoundException {
        listFilesForFolder(folder);
        Gson gson = new Gson();
//        Iterator it = jsonFiles.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pairs = (Map.Entry)it.next();
//            System.out.println(pairs.getKey() + " = " + pairs.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//        System.exit(0);
        for (Map.Entry<String, String> entry : jsonFiles.entrySet()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(entry.getValue()));
                switch(entry.getKey()){
                    case "imdb.Actor":
                        System.out.println("Parsing " + entry.getValue() + " to " + entry.getKey() + " POJO's and adding to Actor array...");
                        Actor[] a = gson.fromJson(br, Actor[].class);
                        System.out.println("Iterating array and putting to " + entry.getKey().toLowerCase() + " hashMap...");
                        for ( Actor actor : a) {
//                            if (actor.getName().equals("Cynthia Dane")) {
//                                System.out.println(actor.getName() + " ID: " + actor.getId());
//                                System.out.println("Exiting...");
//                                System.exit(0);
//                            }
                            actors.put(actor.getId(), actor);
                            actorNames.put(actor.getName(), actor.getId());
                        }
                        System.out.println("Finished...");
                        System.out.println("Size of actors hashMap: " + actors.size());
                        break;
                    case "imdb.Movie":
                        System.out.println("");
                        System.out.println("Parsing " + entry.getValue() + " to " + entry.getKey() + " POJO's and adding to Movie array...");
                        Movie[] m = gson.fromJson(br, Movie[].class);
                        System.out.println("Iterating array and putting to movies hashMap...");
                        for ( Movie movie : m) {
//                                System.out.println(movie.getName() + " ID: " + movie.getId());
//                                System.out.println("Exiting...");
//                                System.exit(0);
                            movies.put(movie.getId(), movie);
                        }
                        System.out.println("Finished...");
                        System.out.println("Size of movies hashMap: " + movies.size());
                        break;
                    case "imdb.Role":
                        System.out.println("");
                        System.out.println("Parsing " + entry.getValue() + " to " + entry.getKey() + " POJO's and adding to Role array...");
                        Role[] r = gson.fromJson(br, Role[].class);
                        System.out.println("Iterating array and putting to actors and movies hashMaps...");
                        for ( Role role : r) {
                            if (actors.containsKey(role.getActor_id())) {
                                //actors.get(role.getActor_id()).add(movies.get(role.getMovie_id()).getName());
                                actor = actors.get(role.getActor_id());
                                movie = movies.get(role.getMovie_id());
                                if (!actor.getMovies().contains(movie))
                                    actor.addMovies(movie);
                                if (!movie.getActors().contains(actor))
                                    movie.addActor(actor);
                            }
                        }
                        System.out.println("Finished...");
                        // System.out.println("Size of roles hashMap: " + roles.size());
                        break;
                    case "imdb.Director":
                        break;
                }
            } catch (Exception e) {
                System.err.println("Caught IO exception: " + e.getMessage());
            }
        }
        System.out.println("");
        System.out.println("Finished parsing JSON. :-)");
        System.out.println("");
    }

    /**
     * Handle non array non object tokens
     *
     * @param reader JsonReader
     * @param token JsonToken
     * @throws IOException
     */
    public static void handleNonArrayToken(JsonReader reader, JsonToken token) throws IOException {
//        if (token.equals(JsonToken.NAME))
//            System.out.println(reader.nextName());
//        else if (token.equals(JsonToken.STRING))
//            System.out.println(reader.nextString());
//        else if (token.equals(JsonToken.NUMBER))
//            System.out.println(reader.nextDouble());
//        else
//            reader.skipValue();
    }

    private FileReader readFile(String path) throws IOException {
        FileReader input = null;
        try {
            input = new FileReader(path);
        } catch (IOException e) {
            System.err.println("Caught IO exception: " + e.getMessage());
        }
        return input;
    }

    /**
     *
     * @param folder File
     * @throws FileNotFoundException
     */
    private void listFilesForFolder(final File folder) throws FileNotFoundException {
        jsonFiles.put("imdb.Actor", folder.getPath() + "/actors.json");
        jsonFiles.put("imdb.Movie", folder.getPath() + "/movies.json");
        jsonFiles.put("imdb.Role", folder.getPath() + "/roles.json");
    }

    public Map<Integer,Actor> getActors() {
        return actors;
    }

    public Map<Integer,Movie> getMovies() {
        return movies;
    }

    public Map<String, Integer> getActorNames() {
        return actorNames;
    }
}