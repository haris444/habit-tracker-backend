package theo.inc.habit_tracker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users") // Capitalized table name
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Habit> habits = new ArrayList<>();

    // Constructors, getters, setters unchanged...
    public User() {}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Habit> getHabits() { return habits; }
    public void setHabits(List<Habit> habits) { this.habits = habits; }
}