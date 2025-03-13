package theo.inc.habit_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import theo.inc.habit_tracker.clock.ClockService;

@Configuration
public class AppConfig {
    @Bean
    public ClockService clockService() {
        return new ClockService();
    }
}