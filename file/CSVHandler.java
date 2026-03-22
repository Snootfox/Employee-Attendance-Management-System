package file;

import model.AttendanceRecord;
import model.AttendanceRecord.AttendanceStatus;
import model.Employee;
import model.Holiday;
import model.LeaveRecord;
import model.LeaveRecord.LeaveStatus;
import model.LeaveRecord.LeaveType;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    private static final DateTimeFormatter DT_FORMAT   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT  = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String DATA_DIR        = "data";
    public static final String EMPLOYEES_FILE  = DATA_DIR + "/employees.csv";
    public static final String ATTENDANCE_FILE = DATA_DIR + "/attendance.csv";
    public static final String LEAVES_FILE     = DATA_DIR + "/leaves.csv";
    public static final String HOLIDAYS_FILE   = DATA_DIR + "/holidays.csv";

    private static final String EMPLOYEE_HEADER   = "id,name,department,scheduleStart,scheduleEnd";
    private static final String ATTENDANCE_HEADER = "employeeId,clockIn,clockOut,status";
    private static final String LEAVES_HEADER     = "employeeId,date,type,reason,status";
    private static final String HOLIDAYS_HEADER   = "date,name";

    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            initFile(EMPLOYEES_FILE,  EMPLOYEE_HEADER);
            initFile(ATTENDANCE_FILE, ATTENDANCE_HEADER);
            initFile(LEAVES_FILE,     LEAVES_HEADER);
            initFile(HOLIDAYS_FILE,   HOLIDAYS_HEADER);
        } catch (IOException e) {
            System.err.println("Could not initialize data directory: " + e.getMessage());
        }
    }

    private static void initFile(String path, String header) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println(header);
            }
        }
    }

    // Employee CSV

    public static List<Employee> loadEmployees() {
        List<Employee> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(EMPLOYEES_FILE)))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split(",", -1);
                    if (p.length < 5) continue;
                    list.add(new Employee(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim()));
                } catch (Exception e) {
                    System.err.println("Skipping bad employee row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading employees.csv: " + e.getMessage());
        }
        return list;
    }

    public static void saveEmployees(List<Employee> employees) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEES_FILE))) {
            pw.println(EMPLOYEE_HEADER);
            for (Employee e : employees) {
                pw.println(String.join(",", e.getId(), e.getName(), e.getDepartment(),
                        e.getScheduleStart(), e.getScheduleEnd()));
            }
        } catch (IOException e) {
            System.err.println("Error saving employees.csv: " + e.getMessage());
        }
    }

    // Attendance CSV

    public static List<AttendanceRecord> loadAttendanceRecords() {
        List<AttendanceRecord> list = new ArrayList<>();
        File file = new File(ATTENDANCE_FILE);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split(",", -1);
                    if (p.length < 4) continue;
                    String employeeId = p[0].trim();
                    String clockInStr = p[1].trim();
                    String clockOutStr = p[2].trim();
                    String statusStr  = p[3].trim();
                    if (employeeId.isEmpty() || clockInStr.isEmpty() || statusStr.isEmpty()) continue;
                    LocalDateTime clockIn     = LocalDateTime.parse(clockInStr, DT_FORMAT);
                    AttendanceStatus status   = AttendanceStatus.valueOf(statusStr);
                    AttendanceRecord record   = new AttendanceRecord(employeeId, clockIn, status);
                    if (!clockOutStr.isEmpty() && !clockOutStr.equals("-")) {
                        record.setClockOut(LocalDateTime.parse(clockOutStr, DT_FORMAT));
                    }
                    list.add(record);
                } catch (Exception e) {
                    System.err.println("Skipping bad attendance row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attendance.csv: " + e.getMessage());
        }
        return list;
    }

    public static void saveAttendanceRecords(List<AttendanceRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE))) {
            pw.println(ATTENDANCE_HEADER);
            for (AttendanceRecord r : records) {
                pw.println(String.join(",", r.getEmployeeId(),
                        r.getClockIn().format(DT_FORMAT),
                        r.getClockOut() != null ? r.getClockOut().format(DT_FORMAT) : "-",
                        r.getStatus().name()));
            }
        } catch (IOException e) {
            System.err.println("Error saving attendance.csv: " + e.getMessage());
        }
    }

    public static void appendAttendanceRecord(AttendanceRecord record) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE, true))) {
            pw.println(String.join(",", record.getEmployeeId(),
                    record.getClockIn().format(DT_FORMAT),
                    record.getClockOut() != null ? record.getClockOut().format(DT_FORMAT) : "-",
                    record.getStatus().name()));
        } catch (IOException e) {
            System.err.println("Error appending attendance record: " + e.getMessage());
        }
    }

    // Leaves CSV

    public static List<LeaveRecord> loadLeaves() {
        List<LeaveRecord> list = new ArrayList<>();
        File file = new File(LEAVES_FILE);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split(",", -1);
                    if (p.length < 5) continue;
                    String     employeeId = p[0].trim();
                    LocalDate  date       = LocalDate.parse(p[1].trim(), DATE_FORMAT);
                    LeaveType  type       = LeaveType.valueOf(p[2].trim());
                    String     reason     = p[3].trim();
                    LeaveStatus status    = LeaveStatus.valueOf(p[4].trim());
                    list.add(new LeaveRecord(employeeId, date, type, reason, status));
                } catch (Exception e) {
                    System.err.println("Skipping bad leave row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading leaves.csv: " + e.getMessage());
        }
        return list;
    }

    public static void saveLeaves(List<LeaveRecord> leaves) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LEAVES_FILE))) {
            pw.println(LEAVES_HEADER);
            for (LeaveRecord l : leaves) {
                pw.println(String.join(",",
                        l.getEmployeeId(),
                        l.getDate().format(DATE_FORMAT),
                        l.getType().name(),
                        l.getReason(),
                        l.getStatus().name()));
            }
        } catch (IOException e) {
            System.err.println("Error saving leaves.csv: " + e.getMessage());
        }
    }

    //Holidays CSV

    public static List<Holiday> loadHolidays() {
        List<Holiday> list = new ArrayList<>();
        File file = new File(HOLIDAYS_FILE);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] p = line.split(",", 2);
                    if (p.length < 2) continue;
                    LocalDate date = LocalDate.parse(p[0].trim(), DATE_FORMAT);
                    String    name = p[1].trim();
                    list.add(new Holiday(date, name));
                } catch (Exception e) {
                    System.err.println("Skipping bad holiday row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading holidays.csv: " + e.getMessage());
        }
        return list;
    }

    public static void saveHolidays(List<Holiday> holidays) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HOLIDAYS_FILE))) {
            pw.println(HOLIDAYS_HEADER);
            for (Holiday h : holidays) {
                pw.println(h.getDate().format(DATE_FORMAT) + "," + h.getName());
            }
        } catch (IOException e) {
            System.err.println("Error saving holidays.csv: " + e.getMessage());
        }
    }
}