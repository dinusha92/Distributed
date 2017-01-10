import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MovieHandler {
    private List<String> moviesList = new ArrayList<String>();

    public MovieHandler(String file_name){
        this.moviesList = getSelectedMovies(file_name);
    }

    private List<String> getSelectedMovies(String fileName) {
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

    public List<String> searchMovies(String query){
        List<String> list = new ArrayList<String>();

        if (query != null && !query.trim().equals("")) {
            query = query.toLowerCase();
            for (String movie : moviesList) {
                if (movie.toLowerCase().contains(query)) {
                    // Remove the spaces
                    list.add(movie.replaceAll(" ", "_"));
                }
            }
        }
        return list;
    }
}
