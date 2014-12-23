package parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imdb.Actor;
import imdb.Movie;
import imdb.Role;

import java.io.*;
import java.util.*;

public class JsonSingleRecordsPerLine {

    private final File folder = new File("/Users/philipriley/Documents/ITU/Algorithm Design 2/imdb_json_tables");
    private HashMap<String, String> jsonFiles = new LinkedHashMap<>();

    private imdb.Actor[] actors;
    private imdb.Movie[] movies;
    private imdb.Role[] roles;
    private Gson gson;

    public JsonSingleRecordsPerLine() throws FileNotFoundException {
        listFilesForFolder(folder);
        gson = new Gson();
        for (Map.Entry<String, String> entry : jsonFiles.entrySet()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(entry.getValue()));
                switch(entry.getKey()){
                    case "imdb.Actor":
                        System.out.println("Parsing actors.json...");
                        actors = gson.fromJson(br, Actor[].class);
                        System.out.println("Serialising actors and writing back to JSON...");
                        writeBackToFile(IMDB.Actor);
                        break;
                    case "imdb.Movie":
                        System.out.println("Parsing movies.json...");
                        movies = gson.fromJson(br, Movie[].class);
                        System.out.println("Serialising movies and writing back to JSON...");
                        writeBackToFile(IMDB.Movie);
                        break;
                    case "imdb.Role":
                        System.out.println("Parsing roles.json...");
                        roles = gson.fromJson(br, Role[].class);
                        System.out.println("Serialising roles and writing back to JSON...");
                        writeBackToFile(IMDB.Role);
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

    protected void writeBackToFile(IMDB type) {
        String ext = "_sl.json";
        String lwrPlural = type.toString().toLowerCase() + "s";
        String path = "/" + lwrPlural + ext;
        //Class<?> clz = Class.forName(type.toString());
        FileWriter fWriter;
        BufferedWriter bWriter;
        gson = new GsonBuilder().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
        Object[] array;
        try {
            fWriter = new FileWriter(folder.getPath() + path, true);
            bWriter = new BufferedWriter(fWriter);
            switch (type.toString()) {
                case "Actor":
                    array = actors;
                    break;
                case "Movie":
                    array = movies;
                    break;
                case "Role":
                    array = roles;
                    break;
                default:
                    array = new Object[0];
            }
            for (Object o : array) {
                bWriter.write(gson.toJson(o));
                bWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    enum IMDB {
        Actor, Movie, Role
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
}