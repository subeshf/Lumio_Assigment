package exam2;

import java.util.ArrayList;
import java.util.List;

public class MetricsCollectorsSlider {
	private int totalMoviesOnFrontPage = 0;
    private int totalMoviesInSlider = 0;
    private int watchOnValidated = 0;
    private int watchOnFailed = 0;
    private int trailerValidated = 0;
    private int trailerFailed = 0;
    private int descriptionsFound = 0;
    static int duplicateMovies = 0;
    private int descriptionsMissing = 0;
    private List<String> moviesValidated = new ArrayList<>();
    private List<String> moviesWithDescriptionIssues = new ArrayList<>();
    private List<String> providerPagesOpened = new ArrayList<>();
    public static List<String> issuesFound = new ArrayList<>();

    public void setTotalMoviesOnFrontPage(int count) {
        this.totalMoviesOnFrontPage = count;
    }

    public void setTotalMoviesInSlider(int count) {
        this.totalMoviesInSlider = count;
    }

    public void incrementWatchOnValidated() {
        this.watchOnValidated++;
    }

    public void incrementWatchOnFailed() {
        this.watchOnFailed++;
    }

    public void incrementTrailerValidated() {
        this.trailerValidated++;
    }

    public void incrementTrailerFailed() {
        this.trailerFailed++;
    }

    public void addDescriptionFound(String movieName) {
        this.descriptionsFound++;
    }

    public void addDescriptionMissing(String movieName) {
        this.descriptionsMissing++;
        this.moviesWithDescriptionIssues.add(movieName);
    }

    public void addMovieValidated(String movieName) {
        this.moviesValidated.add(movieName);
    }

    public void addProviderPage(String title, String url) {
        this.providerPagesOpened.add(title + " | " + url);
    }

    public void addIssue(String issue) {
        this.issuesFound.add(issue);
    }

    public void printMetrics() {
        System.out.println("");
        System.out.println("======= FINAL METRICS for Watch and Tailer Button ========");
     
        System.out.println("");
        
        System.out.println("🎬 MOVIE COUNTS:");
        System.out.println("   ➤ Total movies on front page: " + totalMoviesOnFrontPage);
        System.out.println("   ➤ Total movies in slider: " + totalMoviesInSlider);
        System.out.println("   ➤ Movies validated: " + moviesValidated.size());
        System.out.println("");
        
        System.out.println("📺 'WATCH ON' BUTTON VALIDATION:");
        System.out.println("   ✅ Validated successfully: " + watchOnValidated);
        System.out.println("   ❌ Failed: " + watchOnFailed);
        System.out.println("   📊 Success rate: " + getSuccessRate(watchOnValidated, watchOnFailed) + "%");
        System.out.println("");
        
        System.out.println("🎥 'PLAY TRAILER' BUTTON VALIDATION:");
        System.out.println("   ✅ Validated successfully: " + trailerValidated);
        System.out.println("   ❌ Failed: " + trailerFailed);
        System.out.println("   📊 Success rate: " + getSuccessRate(trailerValidated, trailerFailed) + "%");
        System.out.println("");
        
        System.out.println("📝 DESCRIPTION VALIDATION:");
        System.out.println("   ✅ Descriptions found: " + descriptionsFound);
        System.out.println("   ❌ Descriptions missing: " + descriptionsMissing);
        System.out.println("");
        
        System.out.println("🌐 PROVIDER PAGES:");
        System.out.println("   ➤ Total provider pages opened: " + providerPagesOpened.size());
        System.out.println("");
        
        if (!moviesWithDescriptionIssues.isEmpty()) {
            System.out.println("⚠️ MOVIES WITH DESCRIPTION ISSUES:");
            moviesWithDescriptionIssues.forEach(m -> System.out.println("   - " + m));
            System.out.println("");
        }
        
        if (!issuesFound.isEmpty()) {
            System.out.println("❌ ISSUES FOUND (" + issuesFound.size() + "):");
            issuesFound.forEach(issue -> System.out.println("   - " + issue));
            System.out.println("");
        }
        
        System.out.println("========================================");
        System.out.println("📈 OVERALL SUCCESS RATE: " + getOverallSuccessRate() + "%");
        System.out.println("========================================");
    }

    private String getSuccessRate(int success, int failed) {
        int total = success + failed;
        if (total == 0) return "N/A";
        return String.format("%.2f", (success * 100.0 / total));
    }

    
   
    private String getOverallSuccessRate() {
        int totalValidations = 	MetricsCollectorsSlider.duplicateMovies+MetricsCollector.issuesFound+ watchOnValidated + watchOnFailed + trailerValidated + trailerFailed+descriptionsFound+descriptionsMissing;
        int totalSuccess = watchOnValidated + trailerValidated+descriptionsFound;
        if (totalValidations == 0) return "N/A";
        return String.format("%.2f", (totalSuccess * 100.0 / totalValidations));
    }
    
    // Getters for external access
    public int getTotalMoviesValidated() { return moviesValidated.size(); }
    public int getWatchOnValidated() { return watchOnValidated; }
    public int getTrailerValidated() { return trailerValidated; }
    public int getTotalIssues() { return issuesFound.size(); }
}
