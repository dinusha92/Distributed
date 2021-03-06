import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MovieHandler {
    private static MovieHandler instance;

    private List<String> movies = new ArrayList<String>();

    public static MovieHandler getInstance(String path) {
        if (instance == null) {
            synchronized (MovieHandler.class) {
                if (instance == null) {
                    instance = new MovieHandler(path);
                }
            }
        }

        return instance;
    }

    private MovieHandler(String fileName) {
        this.movies = getMoviesList(fileName);
    }

    private List<String> getMoviesList(String fileName) {
        List<String> moviesList = new ArrayList<String>();
        List<String> movies = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                moviesList.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        Collections.shuffle(moviesList);
        Random rand = new Random();
        int num = rand.nextInt(3) + 3;
        for (int i = 0; i < num; i++){
            movies.add(moviesList.get(i));
        }
        return movies;
    }

    public List<String> searchMoviesList(String query){
        List<String> list = new ArrayList<String>();
        String temp = null;
        String tempQuery = null;

        tempQuery = "_" + query.toLowerCase().replaceAll(" ", "_")+"_";
        if (query != null && !query.trim().equals("")) {

            for (String movie : movies) {
                temp = "_"+movie.toLowerCase().replaceAll(" ","_")+"_";
                if (temp.contains(tempQuery)) {
                    // Remove the spaces
                    list.add(movie.replaceAll(" ","_"));
                }
            }
        }
        return list;
    }

    public List<String> getSelectedMovies() {
        return this.movies;
    }
}
