package service;
import file.CSVHandler;
import model.LeaveRecord;
import model.LeaveRecord.LeaveStatus;
import model.LeaveRecord.LeaveType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeaveService {
    private final List<LeaveRecord> leaves;

    public LeaveService() {
        this.leaves = new ArrayList<>(CSVHandler.loadLeaves());
        System.out.println("Loaded " + leaves.size() + " leave record(s) from file.");
    }

    public boolean fileLeave(String employeeId, LocalDate date, LeaveType type, String reason) {
        boolean duplicate = leaves.stream()
                .anyMatch(l -> l.getEmployeeId().equals(employeeId) && l.getDate().equals(date));
        if (duplicate) return false;

        LeaveRecord record = new LeaveRecord(employeeId, date, type, reason, LeaveStatus.PENDING);
        leaves.add(record);
        CSVHandler.saveLeaves(leaves);
        return true;
    }

    public boolean updateStatus(String employeeId, LocalDate date, LeaveStatus newStatus) {
        for (LeaveRecord l : leaves) {
            if (l.getEmployeeId().equals(employeeId) && l.getDate().equals(date)) {
                l.setStatus(newStatus);
                CSVHandler.saveLeaves(leaves);
                return true;
            }
        }
        return false;
    }
    public boolean isOnApprovedLeave(String employeeId, LocalDate date) {
        return leaves.stream()
                .anyMatch(l -> l.getEmployeeId().equals(employeeId) && l.coversDate(date));
    }

    public long countApprovedLeavesInMonth(String employeeId, int year, int month) {
        return leaves.stream()
                .filter(l -> l.getEmployeeId().equals(employeeId))
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED)
                .filter(l -> l.getDate().getYear() == year && l.getDate().getMonthValue() == month)
                .count();
    }

    public List<LeaveRecord> getAllLeaves()          { return new ArrayList<>(leaves); }

    public List<LeaveRecord> getLeavesForEmployee(String employeeId) {
        return leaves.stream()
                .filter(l -> l.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    public List<LeaveRecord> getPendingLeaves() {
        return leaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.PENDING)
                .collect(Collectors.toList());
    }
}
