package theo.inc.habit_tracker.clock;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class ClockService {
    private Clock clock = Clock.systemDefaultZone();

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock newClock) {
        this.clock = newClock;
        System.out.println("ClockService set to: " + LocalDate.now(clock));
    }
}