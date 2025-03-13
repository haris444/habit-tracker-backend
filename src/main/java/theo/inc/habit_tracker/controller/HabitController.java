package theo.inc.habit_tracker.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import theo.inc.habit_tracker.clock.ClockService;
import theo.inc.habit_tracker.model.Habit;
import theo.inc.habit_tracker.repo.HabitRepository;
import theo.inc.habit_tracker.service.HabitService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
public class HabitController {
    @Autowired
    private HabitRepository habitRepo;

    @Autowired
    private HabitService habitService;

    @Autowired
    private ClockService clockService;

    @GetMapping("/current-date")
    public String getCurrentDate() {
        return LocalDate.now(clockService.getClock()).toString();
    }

    @GetMapping
    public List<Habit> getAllHabits(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return habitService.getAllHabits(userId);
    }

    @PostMapping
    public Habit createHabit(@RequestBody Habit habit, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return habitService.createHabit(habit, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteHabit(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        habitService.deleteHabit(id, userId);
    }

    @GetMapping("/{id}")
    public Habit getHabit(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return habitService.getHabitById(id, userId);
    }

    @PostMapping("/complete/{id}")
    public Habit completeHabit(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return habitService.completeHabit(id, userId);
    }

    @PostMapping("/set-date")
    public String setDate(@RequestParam String date) {
        LocalDate newDate = LocalDate.parse(date);
        Clock newClock = Clock.fixed(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        clockService.setClock(newClock);

        // After setting the date, check and reset streaks for all habits
        List<Habit> allHabits = habitRepo.findAll();
        int resetCount = 0;

        for (Habit habit : allHabits) {
            boolean wasReset = checkAndResetStreakForDateChange(habit, newDate);
            if (wasReset) {
                resetCount++;
                habitRepo.save(habit);
            }
        }

        return "Date set to " + newDate + ". Reset " + resetCount + " habit streaks.";
    }

    // Method to check and reset streak when date changes
    private boolean checkAndResetStreakForDateChange(Habit habit, LocalDate newDate) {
        if (habit.getCompletionDates().isEmpty()) return false;

        LocalDate lastCompletion = habit.getCompletionDates().stream()
                .max(LocalDate::compareTo)
                .orElse(null);

        if (lastCompletion == null) return false;

        // If the last completion is more than 1 day before the new date,
        // and the streak is not already 0, reset the streak
        if (lastCompletion.isBefore(newDate.minusDays(1)) && habit.getStreak() > 0) {
            habit.setStreak(0);
            System.out.println("Reset streak for habit: " + habit.getId() +
                    " - Last completion: " + lastCompletion +
                    ", New date: " + newDate);
            return true;
        }

        return false;
    }

    @GetMapping("/completions")
    public Map<LocalDate, Integer> getCompletionCounts(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return habitService.getCompletionCounts(userId, start, end);
    }
}