import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
@EnableScheduling
public class HabitTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HabitTrackerApplication.class, args);
    }

    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void keepAlive() {
        try {
            URL url = new URL("https://habit-tracker-backend-0576.onrender.com/api/habits/current-date");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            System.out.println("Keep-alive ping: " + conn.getResponseCode());
            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Keep-alive failed: " + e.getMessage());
        }
    }
}