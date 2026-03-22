package model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Holiday {
    private final LocalDate date;
    private final String name;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public Holiday(LocalDate date, String name) {
        this.date = date;
        this.name = name;
    }

    public LocalDate getDate()          { return date; }
    public String    getName()          { return name; }
    public String    getFormattedDate() { return date.format(FMT); }

    public boolean isToday() {
        return date.equals(LocalDate.now());
    }
}
