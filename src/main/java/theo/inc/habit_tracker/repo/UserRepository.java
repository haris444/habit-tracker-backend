package theo.inc.habit_tracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import theo.inc.habit_tracker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}