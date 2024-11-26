import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Main{
    public static void main(String[] args){
        File f = new File("tmdb_data.csv");
        Scanner scanner = null;
        try{
            scanner = new Scanner(f);
        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }
        scanner.nextLine();
        ArrayList<String> brokenShows = new ArrayList<>();
        ArrayList<TVShow> cleanedShows = new ArrayList<>();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] parts = parseCsvLine(line);
            if(parts.length==10 && !parts[3].equals("0")){
                String glasovi = parts[3].replaceAll("\"", "").replaceAll(",", "");
                String glasoviAvg = parts[4].replaceAll("\"", "").replace(",", ".");
                String popularnost = parts[8].replaceAll("\"", "").replace(",", ".");
                try{
                    cleanedShows.add(new TVShow(Integer.parseInt(parts[0]), parts[1], parts[2], Integer.parseInt(glasovi), Double.parseDouble(glasoviAvg),
                            parts[5], parts[6], Boolean.parseBoolean(parts[7]), Double.parseDouble(popularnost), parts[9] ));
                }catch (Exception e){
                    brokenShows.add(line);
                }
            } else {
                brokenShows.add(line);
            }
        }
        cleanedCsv(cleanedShows);
        brokenCsv(brokenShows);

        //prvi zadatak
        cleanedShows.sort((v1,v2)->{return v2.getVoteAverage().compareTo(v1.getVoteAverage());});
        try{
            FileWriter fw = new FileWriter("task1.csv");
            ArrayList<TVShow> filteredShows = new ArrayList<>();
            for(TVShow show : cleanedShows){
                if(show.getVoteCount()>99){
                    filteredShows.add(show);
                }
            }
            int limit = Math.min(50, filteredShows.size());
            for(int i = 0; i < limit; i++){
                fw.write(filteredShows.get(i).toString()+"\n");
            }
            fw.close();
        } catch(Exception e){
            System.out.println("Error writing task1.csv: "+e.getMessage());
        }

        //drugi zadatak
        Map<String, ArrayList<TVShow>> byLang = new HashMap<>();
        for(TVShow show : cleanedShows){
            byLang.computeIfAbsent(show.getOglang(), k -> new ArrayList<>()).add(show);
        }
        for(Map.Entry<String, ArrayList<TVShow>> entry : byLang.entrySet()){
            String lang = entry.getKey();
            ArrayList<TVShow> shows = entry.getValue();
            shows.sort((v1,v2)-> v2.getVoteAverage().compareTo(v1.getVoteAverage()));
            if(shows.size()>50){
                shows.subList(50, shows.size()).clear();
            }
            File task2 = new File("task2." + lang + ".csv");
            try{
                FileWriter fw = new FileWriter(task2);
                for(TVShow show : shows){
                    fw.write(show.toString()+"\n");
                }
                fw.close();
            } catch(Exception e){
                System.out.println("Error writing task2.csv: "+e.getMessage());
            }
        }

        //treci zadatak
        try{
            long count = cleanedShows.stream()
                    .filter(show -> show.getVoteCount() > 15000)
                    .count();
            FileWriter fw = new FileWriter("task3.csv");
            fw.write("Number of shows with vote count over 15000: " + count +"\n");
            fw.close();
        }catch(Exception e){
            System.out.println("Error writing task3.csv: " + e.getMessage());
        }

        //cetvrti zad
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse("2010-01-01");
            Date endDate = sdf.parse("2019-12-31");

            ArrayList<TVShow> filteredShows = new ArrayList<>();
            for(TVShow show : cleanedShows) {
                String firstAirStr = show.getFirstAir();
                String lastAirStr = show.getLastAir();
                if (firstAirStr == null || firstAirStr.isEmpty() || lastAirStr == null || lastAirStr.isEmpty()) {
                    continue; // Skip this entry
                }
                try {
                    Date firstAirDate = sdf.parse(firstAirStr);
                    Date lastAirDate = sdf.parse(lastAirStr);
                    if (!firstAirDate.before(startDate) && !lastAirDate.after(endDate)) {
                        filteredShows.add(show);
                    }
                } catch (java.text.ParseException e) {
                    // Log and skip rows with invalid date formats
                    System.out.println("Skipping invalid date for show: " + show);
                }
            }

            filteredShows.sort((v1,v2)->v2.getVoteAverage().compareTo(v1.getVoteAverage()));
            int limit = Math.min(50, filteredShows.size());
            FileWriter fw = new FileWriter("task4.csv");
            for(int i = 0; i < limit; i++){
                fw.write(filteredShows.get(i).toString()+"\n");
            }
            fw.close();
        }catch(ParseException e){
            System.out.println("Error parsing date: "+e.getMessage());
        } catch (Exception e){
            System.out.println("Error writing task4.csv: "+e.getMessage());
        }

        //peti
        try{
            ArrayList<TVShow> inProductionShows = new ArrayList<>();
            for(TVShow show : cleanedShows){
                if(show.getInProduction())inProductionShows.add(show);
            }
            inProductionShows.sort((v1,v2)-> v2.getVoteAverage().compareTo(v1.getVoteAverage()));
            int limit = Math.min(50, inProductionShows.size());
            FileWriter fw = new FileWriter("task5.csv");
            for(int i = 0; i < limit; i++){
                fw.write(inProductionShows.get(i).toString()+"\n");
            }
            fw.close();
        }catch(Exception e){
            System.out.println("Error in task5: "+e.getMessage());
        }
        //sesti
        try{
            ArrayList<TVShow> singleWord = new ArrayList<>();
            for(TVShow show : cleanedShows){
                if(!show.getName().contains(" ")){
                    singleWord.add(show);
                }
            }
            singleWord.sort((v1,v2)-> v2.getPopularity().compareTo(v1.getPopularity()));
            int limit = Math.min(50, singleWord.size());
            FileWriter fw = new FileWriter("task6.csv");
            for(int i = 0; i < limit; i++){
                fw.write(singleWord.get(i).toString()+"\n");
            }
            fw.close();
        }catch(Exception e){
            System.out.println("Error in task6: "+e.getMessage());
        }

        //sedmi
        try{
            Map<String, ArrayList<TVShow>> byGenre = new HashMap<>();
            for(TVShow show : cleanedShows){
                ArrayList<String> genres = show.getGenres();
                for(String genre : genres){
                    genre=genre.trim();
                    byGenre.computeIfAbsent(genre.trim(), k->new ArrayList<>()).add(show);
                }
            }
            for(Map.Entry<String, ArrayList<TVShow>> entry : byGenre.entrySet()){
                String genre = entry.getKey();
                ArrayList<TVShow> genreShows = entry.getValue();
                genreShows.sort((v1,v2)-> v2.getVoteAverage().compareTo(v1.getVoteAverage()));
                int limit = Math.min(50, genreShows.size());
                ArrayList<TVShow> topShows = new ArrayList<>(genreShows.subList(0,limit));
                String sanitizedGenre = genre.replaceAll("[\\\\/:*?\"<>|]", "_");
                File genreFile = new File("task7_" + sanitizedGenre + ".csv");
                try(FileWriter fw = new FileWriter(genreFile)){
                    for(TVShow show : topShows){
                        fw.write(show.toString() + "\n");
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Error in task7: "+e.getMessage());
        }
    }

    private static String[] parseCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?=,|$)");
        Matcher matcher = pattern.matcher(line);
        while(matcher.find()){
            if(matcher.group(1)!=null) fields.add(matcher.group(1));
            else fields.add(matcher.group(2));
        }
        return fields.toArray(new String[0]);
    }


    private static void brokenCsv(ArrayList<String> brokenShows) {
        try{
            FileWriter fw = new FileWriter("brokenShows.csv");
            for(String s : brokenShows){
                fw.write(s + "\n");
            }
            fw.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private static void cleanedCsv(ArrayList<TVShow> cleanedShows) {
        try{
            FileWriter fw = new FileWriter("cleanShows.csv");
            for(TVShow sh : cleanedShows){
                fw.write(sh.toString() + "\n");
            }
            fw.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
