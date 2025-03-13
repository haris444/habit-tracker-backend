package theo.inc.habit_tracker.service;

import org.springframework.stereotype.Service;
import theo.inc.habit_tracker.clock.ClockService;
import theo.inc.habit_tracker.model.Habit;
import theo.inc.habit_tracker.model.User;
import theo.inc.habit_tracker.repo.HabitRepository;
import theo.inc.habit_tracker.repo.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HabitService {
    private final HabitRepository habitRepo;
    private final UserRepository userRepo;
    private final ClockService clockService;

    public HabitService(HabitRepository habitRepo, UserRepository userRepo, ClockService clockService) {
        this.habitRepo = habitRepo;
        this.userRepo = userRepo;
        this.clockService = clockService;
    }

    public Habit completeHabit(Long id, Long userId) {
        Habit habit = habitRepo.findById(id).orElseThrow(() -> new RuntimeException("Habit not found"));
        if (!habit.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        checkAndResetStreak(habit);
        LocalDate today = LocalDate.now(clockService.getClock());
        System.out.println("Completing habit " + id + " on " + today);
        System.out.println("Current completion dates: " + habit.getCompletionDates());
        if (!habit.getCompletionDates().contains(today)) {
            habit.setStreak(habit.getStreak() + 1);
            habit.setXp(habit.getXp() + 10);
            int newLevel = habit.getXp() / 100 + 1;
            if (newLevel > habit.getLevel()) habit.setLevel(newLevel);
            habit.getCompletionDates().add(today);
            System.out.println("Updated streak: " + habit.getStreak() + ", XP: " + habit.getXp());
            habitRepo.saveAndFlush(habit); // Flush habit first
        } else {
            System.out.println("Habit already completed today: " + today);
        }
        Habit savedHabit = habitRepo.save(habit); // Final save
        System.out.println("Saved habit: " + savedHabit);
        return savedHabit;
    }

    public List<Habit> getAllHabits(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return habitRepo.findByUser(user);
    }

    public Habit createHabit(Habit habit, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        habit.setUser(user);
        return habitRepo.save(habit);
    }

    public void deleteHabit(Long id, Long userId) {
        // Use the helper method to get habit and verify ownership
        Habit habit = getHabitAndVerifyOwner(id, userId);
        habitRepo.delete(habit);
    }

    private void checkAndResetStreak(Habit habit) {
        LocalDate today = LocalDate.now(clockService.getClock());
        LocalDate yesterday = today.minusDays(1);
        List<LocalDate> dates = habit.getCompletionDates();
        System.out.println("Checking streak for habit " + habit.getId() + ": Today=" + today + ", Yesterday=" + yesterday + ", Dates=" + dates);

        if (dates.isEmpty()) return;

        LocalDate lastCompletion = dates.stream()
                .max(LocalDate::compareTo)
                .orElse(null);
        if (lastCompletion != null && !lastCompletion.equals(yesterday) && !lastCompletion.equals(today)) {
            habit.setStreak(0);
            System.out.println("Streak reset for habit " + habit.getId() + " - Last completion: " + lastCompletion);
        }
    }

    public Map<LocalDate, Integer> getCompletionCounts(Long userId, LocalDate start, LocalDate end) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Habit> habits = habitRepo.findByUser(user);
        Map<LocalDate, Integer> counts = new HashMap<>();

        for (Habit habit : habits) {
            for (LocalDate date : habit.getCompletionDates()) {
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    counts.merge(date, 1, Integer::sum);
                }
            }
        }
        return counts;
    }

    // New method to get habit by ID with JWT auth
    public Habit getHabitById(Long id, Long userId) {
        return getHabitAndVerifyOwner(id, userId);
    }

    // Helper method to get a habit and verify the user owns it
    private Habit getHabitAndVerifyOwner(Long habitId, Long userId) {
        Habit habit = habitRepo.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to access this habit");
        }

        return habit;
    }
}