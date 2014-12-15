import imdb.*;
import parse.BaconJsonParser;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bacon {
    // name -> Movies that actor has been in
    // name -> Actor object
    private Map<Integer, Actor> actors;
    private Map<Integer, Movie> movies;
    private Integer startActorId = null; // Kevin Bacon
    private Integer targetActorId = null;
    private Integer startMovieId = null;
    private int linkableActors;
    List<Integer> actorDistribution = null;
    private Queue<Movie> movieQueue = new ArrayDeque<>(); // faster than linkedList
    private Map<String, Integer> actorNames;

    public Bacon() {
        actors = new HashMap<>();
        movies = new HashMap<>();
        actorNames = new HashMap<>();
    }

    /**
     * Given that the imdb and movies maps are properly
     * initialized from readFile. Explore all of the imdb
     * starting from Kevin Bacon in a breadth-first manner.
     * After completing traverse, all Actors connected to
     * Kevin Bacon (by one or more links) should have
     * their Bacon numbers set.
     */
    public void traverseByActors() // breadth-first
    {
        int vis = 1;
        long begin = System.currentTimeMillis();
        Actor start = actors.get(startActorId);
        start.setNumber(0);
        start.setPrevActor(null);

        Queue<Actor> fringe = new LinkedList<>();
        fringe.add(start);
        Set<Actor> visited = new HashSet<>();

        while (!fringe.isEmpty()) {
            Actor curr = fringe.poll();

//			if (vis % 1000 == 0){
//
//				break;
//			}

            Map<Actor, Movie> coStars = curr.coStars();

//			if (!curr.getName().equals("Kevin Bacon")) {
//				for (Map.Entry<Actor, Movie> entry : coStars.entrySet()) {
//					System.out.println(entry.getKey().getName() + " - was in " + entry.getValue().getName());
//				}
////				System.exit(0);
//			}

            // for every actor who was a costar with curr
            for (Actor a : coStars.keySet()) {
                // add to queue
                if (!visited.contains(a)) {
                    if (!fringe.contains(a)) {
                        fringe.add(a);
                        a.setNumber(curr.getNumber() + 1);
                        a.setPrevActor(curr);
                        a.setSharedMovie(coStars.get(a));

                        if (a.getId() == targetActorId) {
                            break;
                        }

                        visited.add(curr);
                        System.out.println(curr.getId() + " " + curr.getName() + " " + curr.getNumber());
                        System.out.println("visited size: " + visited.size());
                        System.out.println("fringe size: " + fringe.size());
                        System.out.println("#");
                    }
                }
            }
            vis += 1;
        }
        System.out.println("Traversal: Time elapsed " +
                (System.currentTimeMillis() - begin) / 1000.0 + "s");
    }


    public void traverseByMovies() {
//        createMovieQueue();

        int loop = 1;
        long startTime = System.currentTimeMillis();
//        Movie startMovie = movieQueue.poll();
//        Actor startActor = startMovie.hasActor(startActorId) ? actors.get(startActorId) : actors.get(targetActorId);
        Actor startActor = actors.get(startActorId);
        startActor.setNumber(0);
        startActor.setPrevActor(null);

        Queue<Actor> fringe = new ArrayDeque<>();
//        Set<Actor> visited = new HashSet<>();
        fringe.add(startActor);

//        Set<Actor> allActorsQueue = new LinkedHashSet<>();
//        while(!movieQueue.isEmpty()){
//            Movie m = movieQueue.poll();
//            for (Actor movieActor : m.getActors()) {
//                allActorsQueue.add(movieActor);
//            }
//        }

        while(!fringe.isEmpty()){
            Actor currActor = fringe.poll();
            Map<Actor, Movie> coStars = currActor.coStars();

            for(Actor a: coStars.keySet()) {
                if (!a.isVisited() && !a.equals(startActor)) {
                    fringe.add(a);
                    a.setNumber(currActor.getNumber() + 1);
                    a.setPrevActor(currActor);
                    a.setSharedMovie(coStars.get(a));
                    a.toggleVisited();
                    //System.out.println("fringe size: " + fringe.size());

                    if(a.getId() == targetActorId){
                        break;
                    }
                }
            }
        }
        System.out.println("Traversal: Time elapsed " +
                (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    public void createMovieQueue() {
        /**
         * TODO: optimize this! probably not necessary to create a third list
         */
        long startTime = System.currentTimeMillis();
        System.out.println("Sorting started -----------");
        Integer sumYears = 0;
        List<Movie> pivotActorsMovies = new ArrayList<>();
        List<Movie> otherMovies = new ArrayList<>();
        try {
            for (int mId : movies.keySet()) {
                Movie m = movies.get(mId);
                if (m.hasActor(startActorId) || m.hasActor(targetActorId)) {
                    pivotActorsMovies.add(m);
                    sumYears += m.getYear();
                } else {
                    otherMovies.add(m);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        final Integer avgPivotMoviesYear = sumYears / pivotActorsMovies.size();

        pivotActorsMovies.sort(new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                return m2.getActors().size() - m1.getActors().size(); // descending
            }
        });
        movieQueue.addAll(pivotActorsMovies);

        otherMovies.sort(new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                // sort by closeness to the year of movies the pivot actors played in
                return Math.abs(avgPivotMoviesYear - m2.getYear()) - Math.abs(avgPivotMoviesYear - m1.getYear());
            }
        });
        movieQueue.addAll(otherMovies);
        System.out.println("Sorting completed. time: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        System.out.println("pivot movies size: " + pivotActorsMovies.size());
        System.out.println("other movies size: " + otherMovies.size());
    }

    /**
     * Print the chain from Kevin Bacon to specified
     * actor or actress. If no such actor or actress. print
     * error message
     * Actor Name has a Bacon number of X
     * Actor Name appeared in Movie Nam with Actor 2 Name
     * ...
     * Actor Z Name appeared in Movie Z Name with Kevin Bacon
     *
     */
    public void printChain() {
        Actor start = actors.get(startActorId);
        Actor dest = actors.get(targetActorId);

        if (dest == null)
            System.out.println("No such actor " + targetActorId);
        if (dest.getNumber() == Integer.MAX_VALUE) {
            System.out.println(dest.getName() + " has a Bacon number of infinity");
            return;
        }
        System.out.println(dest.getName() + " has a Bacon number of " +
                dest.getNumber());

        while (dest != start) {
            System.out.println(dest.getName() + " was in " +
                    dest.getSharedMovie().getName() + " with " +
                    dest.getPrevActor().getName());
            dest = dest.getPrevActor();
        }
    }

    protected List<Integer> initializeArrayList() {
        System.out.println("Initializing ArrayList...");
        List<Integer> a = new ArrayList<>();
        for (int i = 0; i < 11; i ++) {
            a.add(i, 0);
        }
        System.out.println("Array size: " + a.size());
        System.out.println("Finished...");
        return a;
    }

    private void generateTable() {
        linkableActors = 0;
        actorDistribution = initializeArrayList();
        System.out.println("Generating distribution of actors...");
        int numOfActorsByIndex;
        for (Actor actor : actors.values()) {
            int baconNumber = actor.getNumber();
            if (baconNumber <= 10) {
                linkableActors++;
                numOfActorsByIndex = actorDistribution.get(baconNumber) + 1;
                actorDistribution.set(actor.getNumber(), numOfActorsByIndex);
            }
        }
        System.out.println("Finished...");
    }

    private void printTable() {
        generateTable();
        System.out.println("Printing actor distribution...");
        System.out.println("");
        for (int i = 0; i < 11; i ++) {
            System.out.println(i + " - " + actorDistribution.get(i));
        }
    }

    public void printAvgActorNumber() {
        printTable();
        int weightedDistribution = 0;
        String centerName = actors.get(startActorId).getName();
        for (int i =0; i < actorDistribution.size(); i++) {
            weightedDistribution = weightedDistribution + (i * actorDistribution.get(i));
        }
        // Casting to double to preserve precision
        double avgNum = (double)weightedDistribution / linkableActors;
        DecimalFormat df = new DecimalFormat("###.##");
        String rndAvgNum = df.format(avgNum);

        System.out.println("");
        System.out.println("Total number of linkable actors: " + linkableActors);
        System.out.println("Weighted total of linkable actors: " + weightedDistribution);
        System.out.println("Average " + centerName + " number: " + rndAvgNum);
    }

    public void setStartActorId(int id) {
        startActorId = id;
    }

    public void setStartActorName(String name) {
        startActorId = getActorIdFromName(name);
    }

    public void setTargetActorName(String name) {
        targetActorId = getActorIdFromName(name);
    }

    private Integer getActorIdFromName(String name) {
        if (actorNames.containsKey(name))
            return actorNames.get(name);
        else {
            System.out.println(name + " was not found in the database.");
            System.out.println("Please check your spelling or choose another actor");
            System.exit(0);
        }
        return null;
    }

    public void setTargetActorIdId(int id) {
        targetActorId = id;
    }

    public Integer getTargetActorId() {
        return targetActorId;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Bacon bfs = new Bacon();
        try {
            BaconJsonParser parser = new BaconJsonParser();
            bfs.actors = parser.getActors();
            bfs.movies = parser.getMovies();
            bfs.actorNames = parser.getActorNames();
            //System.out.println("movies count: " + bfs.movies.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bfs.setStartActorName("Kevin Bacon");
        //bfs.setTargetActorName("Cynthia Dane");
        /*
        See https://www.cs.duke.edu/courses/fall07/cps100e/class/10_Bacon/
		and https://www.cs.duke.edu/courses/fall07/cps100e/class/10_Bacon/codev2/
		 */
//		bfs.traverseByActors();
        bfs.traverseByMovies();

        //bfs.printChain(22591); // Kevin Bacon's ID
        //bfs.printChain(); // Cynthia Dane's ID 592813
        bfs.printAvgActorNumber();
    }
}