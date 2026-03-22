package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AttendanceRecord {
    private final String employeeId;
    private final LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private final AttendanceStatus status;

    public enum AttendanceStatus {
        ON_TIME, LATE, ABSENT
    }

    public AttendanceRecord(String employeeId, LocalDateTime clockIn, AttendanceStatus status) {
        this.employeeId = employeeId;
        this.clockIn    = clockIn;
        this.status     = status;
    }

    public void clockOut() {
        this.clockOut = LocalDateTime.now();
    }

    // Used by CSVHandler to restore a clock-out time loaded from file.
    public void setClockOut(LocalDateTime clockOut) {
        this.clockOut = clockOut;
    }

    public boolean isClockedOut() {
        return clockOut != null;
    }

    public String getFormattedClockIn() {
        return clockIn.format(DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a"));
    }

    public String getFormattedClockOut() {
        if (clockOut == null) return "—";
        return clockOut.format(DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a"));
    }

    public String getEmployeeId()       { return employeeId; }
    public LocalDateTime getClockIn()   { return clockIn; }
    public LocalDateTime getClockOut()  { return clockOut; }
    public AttendanceStatus getStatus() { return status; }
}