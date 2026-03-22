package model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LeaveRecord {
    public enum LeaveStatus { APPROVED, PENDING, REJECTED }
    public enum LeaveType   { SICK, VACATION, EMERGENCY, OTHER }

    private final String employeeId;
    private final LocalDate date;
    private final LeaveType type;
    private final String reason;
    private LeaveStatus status;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public LeaveRecord(String employeeId, LocalDate date, LeaveType type, String reason, LeaveStatus status) {
        this.employeeId = employeeId;
        this.date       = date;
        this.type       = type;
        this.reason     = reason;
        this.status     = status;
    }

    public void setStatus(LeaveStatus status) { this.status = status; }

    public String    getEmployeeId() { return employeeId; }
    public LocalDate getDate()       { return date; }
    public LeaveType getType()       { return type; }
    public String    getReason()     { return reason; }
    public LeaveStatus getStatus()   { return status; }

    public String getFormattedDate() { return date.format(FMT); }

    public boolean coversDate(LocalDate d) {
        return status == LeaveStatus.APPROVED && date.equals(d);
    }
}
