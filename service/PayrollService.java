package service;

import model.AttendanceRecord;
import model.AttendanceRecord.AttendanceStatus;
import model.Employee;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayrollService {
    // Daily rate per employee in PHP
    private final Map<String, Double> dailyRates;

    private static final LocalTime SHIFT_START   = LocalTime.of(8, 0);
    private static final int       WORKING_DAYS  = 22; // avg working days per month

    public PayrollService() {
        dailyRates = new HashMap<>();
        // Default daily rates per employee
        dailyRates.put("EMP001", 800.0);
        dailyRates.put("EMP002", 750.0);
        dailyRates.put("EMP003", 700.0);
        dailyRates.put("EMP004", 800.0);
        dailyRates.put("EMP005", 720.0);
    }

    public double getDailyRate(String employeeId) {
        return dailyRates.getOrDefault(employeeId, 650.0);
    }

    public void setDailyRate(String employeeId, double rate) {
        dailyRates.put(employeeId, rate);
    }

    public PayrollSummary compute(Employee employee, List<AttendanceRecord> records,
                                  long approvedLeaves, int year, int month) {
        double dailyRate  = getDailyRate(employee.getId());
        double hourlyRate = dailyRate / 8.0;

        int daysPresent = 0;
        int daysLate    = 0;
        int daysAbsent  = 0;
        double lateDeductions = 0.0;

        for (AttendanceRecord r : records) {
            LocalDate recDate = r.getClockIn().toLocalDate();
            if (recDate.getYear() != year || recDate.getMonthValue() != month) continue;

            if (r.getStatus() == AttendanceStatus.ABSENT) {
                daysAbsent++;
            } else {
                daysPresent++;
                if (r.getStatus() == AttendanceStatus.LATE) {
                    daysLate++;
                    // Calculate minutes late
                    LocalTime clockInTime = r.getClockIn().toLocalTime();
                    long minutesLate = Duration.between(SHIFT_START, clockInTime).toMinutes();
                    if (minutesLate > 0) {
                        long blocks = minutesLate / 30; // each 30-min block
                        if (minutesLate % 30 > 0) blocks++; // round up
                        lateDeductions += blocks * (hourlyRate / 2.0);
                    }
                }
            }
        }

        double absentDeductions = daysAbsent * dailyRate;
        double grossPay = daysPresent * dailyRate;
        double totalDeductions = lateDeductions + absentDeductions;
        double netPay = Math.max(0, grossPay - totalDeductions);

        return new PayrollSummary(
                employee.getId(), employee.getName(), employee.getDepartment(),
                dailyRate, daysPresent, daysLate, daysAbsent,
                (int) approvedLeaves, grossPay, lateDeductions, absentDeductions, netPay
        );
    }


    public static class PayrollSummary {
        public final String employeeId, name, department;
        public final double dailyRate;
        public final int daysPresent, daysLate, daysAbsent, daysOnLeave;
        public final double grossPay, lateDeduction, absentDeduction, netPay;

        public PayrollSummary(String employeeId, String name, String department,
                              double dailyRate, int daysPresent, int daysLate,
                              int daysAbsent, int daysOnLeave,
                              double grossPay, double lateDeduction,
                              double absentDeduction, double netPay) {
            this.employeeId     = employeeId;
            this.name           = name;
            this.department     = department;
            this.dailyRate      = dailyRate;
            this.daysPresent    = daysPresent;
            this.daysLate       = daysLate;
            this.daysAbsent     = daysAbsent;
            this.daysOnLeave    = daysOnLeave;
            this.grossPay       = grossPay;
            this.lateDeduction  = lateDeduction;
            this.absentDeduction = absentDeduction;
            this.netPay         = netPay;
        }
    }
}