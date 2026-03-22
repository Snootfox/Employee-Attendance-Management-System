package model;

public class Employee {
    private String id;
    private String name;
    private String department;
    private String scheduleStart; // e.g. "08:00"
    private String scheduleEnd;   // e.g. "17:00"

    public Employee(String id, String name, String department,
                    String scheduleStart, String scheduleEnd) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
    }

    public String getId()            { return id; }
    public String getName()          { return name; }
    public String getDepartment()    { return department; }
    public String getScheduleStart() { return scheduleStart; }
    public String getScheduleEnd()   { return scheduleEnd; }

    public void setName(String name)                   { this.name = name; }
    public void setDepartment(String department)       { this.department = department; }
    public void setScheduleStart(String scheduleStart) { this.scheduleStart = scheduleStart; }
    public void setScheduleEnd(String scheduleEnd)     { this.scheduleEnd = scheduleEnd; }

    @Override
    public String toString() {
        return String.format("Employee[id=%s, name=%s, dept=%s]", id, name, department);
    }
}
