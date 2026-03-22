package service;

import file.CSVHandler;
import model.AttendanceRecord;
import model.AttendanceRecord.AttendanceStatus;
import model.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceService {
    private final List<AttendanceRecord> records;

    public AttendanceService() {
        this.records = new ArrayList<>(CSVHandler.loadAttendanceRecords());
        System.out.println("Loaded " + records.size() + " attendance record(s) from file.");
    }

    public AttendanceRecord clockIn(Employee employee) {
        AttendanceStatus status = determineStatus(employee);
        AttendanceRecord record = new AttendanceRecord(employee.getId(), LocalDateTime.now(), status);
        records.add(record);
        CSVHandler.appendAttendanceRecord(record);
        return record;
    }
    public AttendanceRecord clockOut(String employeeId) {
        for (int i = records.size() - 1; i >= 0; i--) {
            AttendanceRecord r = records.get(i);
            if (r.getEmployeeId().equals(employeeId) && !r.isClockedOut()) {
                r.clockOut();
                CSVHandler.saveAttendanceRecords(records);
                return r;
            }
        }
        return null;
    }
    public boolean markAbsent(String employeeId) {
        boolean alreadyHasRecord = records.stream()
                .anyMatch(r -> r.getEmployeeId().equals(employeeId)
                        && r.getClockIn().toLocalDate().equals(LocalDate.now()));
        if (alreadyHasRecord) return false;

        AttendanceRecord record = new AttendanceRecord(employeeId, LocalDateTime.now(), AttendanceStatus.ABSENT);
        records.add(record);
        CSVHandler.appendAttendanceRecord(record);
        return true;
    }

    public boolean hasRecordToday(String employeeId) {
        return records.stream()
                .anyMatch(r -> r.getEmployeeId().equals(employeeId)
                        && r.getClockIn().toLocalDate().equals(LocalDate.now()));
    }

    public boolean isClockedIn(String employeeId) {
        return records.stream()
                .anyMatch(r -> r.getEmployeeId().equals(employeeId)
                        && !r.isClockedOut()
                        && r.getClockIn().toLocalDate().equals(LocalDate.now()));
    }

    public int countOnTimeToday() {
        return (int) records.stream()
                .filter(r -> r.getClockIn().toLocalDate().equals(LocalDate.now()))
                .filter(r -> r.getStatus() == AttendanceStatus.ON_TIME)
                .count();
    }

    public int countLateToday() {
        return (int) records.stream()
                .filter(r -> r.getClockIn().toLocalDate().equals(LocalDate.now()))
                .filter(r -> r.getStatus() == AttendanceStatus.LATE)
                .count();
    }

    public int countAbsentToday() {
        return (int) records.stream()
                .filter(r -> r.getClockIn().toLocalDate().equals(LocalDate.now()))
                .filter(r -> r.getStatus() == AttendanceStatus.ABSENT)
                .count();
    }

    public List<AttendanceRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    public List<AttendanceRecord> getRecordsForEmployee(String employeeId) {
        return records.stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    private AttendanceStatus determineStatus(Employee employee) {
        LocalTime now = LocalTime.now();
        LocalTime scheduleStart = LocalTime.parse(employee.getScheduleStart(),
                DateTimeFormatter.ofPattern("HH:mm"));
        return now.isAfter(scheduleStart.plusMinutes(15))
                ? AttendanceStatus.LATE
                : AttendanceStatus.ON_TIME;
    }
}