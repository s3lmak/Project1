import java.util.ArrayList;

public class TVShow {
    private Integer id;
    private String name;
    private String oglang;
    private Integer voteCount;
    private Double voteAverage;
    private String firstAir;
    private String lastAir;
    private Boolean inProduction;
    private Double popularity;
    private ArrayList<String> genres;

    TVShow(Integer id, String name, String oglang, Integer voteCount, Double voteAverage, String firstAir, String lastAir, Boolean inProduction, Double popularity, String genres){
        this.id = id;
        this.name = name;
        this.oglang = oglang;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.firstAir = firstAir;
        this.lastAir = lastAir;
        this.inProduction = inProduction;
        this.popularity = popularity;
        this.genres = genresAssign(genres);
    }

    private ArrayList<String> genresAssign(String genres) {
        String [] genreArray = genres.split(",");
        ArrayList<String> genresList = new ArrayList<>();
        for(String genre : genreArray){
            genresList.add(genre);
        }
        return genresList;
    }

    @Override
    public String toString(){
        return id + "," + name + "," + oglang + "," + voteCount + "," + voteAverage + "," + firstAir + "," + lastAir + "," + inProduction + "," + popularity + "," + genres;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOglang() {
        return oglang;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }
    public String getFirstAir() {
        return firstAir;
    }
    public String getLastAir() {
        return lastAir;
    }

    public Boolean getInProduction() {
        return inProduction;
    }
    public Double getPopularity() {
        return popularity;
    }
    public ArrayList<String> getGenres() {
        return genres;
    }
}
