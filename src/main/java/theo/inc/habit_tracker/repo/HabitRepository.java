package theo.inc.habit_tracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import theo.inc.habit_tracker.model.Habit;
import theo.inc.habit_tracker.model.User;

import java.util.List;


@org.springframework.stereotype.Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
}
