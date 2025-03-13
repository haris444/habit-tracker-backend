package theo.inc.habit_tracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import theo.inc.habit_tracker.model.User;
@Entity
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int streak;
    @ElementCollection
    private List<LocalDate> completionDates = new ArrayList<>();


    private int xp = 0;
    private int level = 1;

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    @ManyToOne
    @JsonBackReference
    private User user; // New field

    // Getters, setters...
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Habit() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public List<LocalDate> getCompletionDates() { return completionDates; }
    public void setCompletionDates(List<LocalDate> dates) { this.completionDates = dates; }
}