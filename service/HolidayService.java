package service;
import file.CSVHandler;
import model.Holiday;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class HolidayService {
    private final List<Holiday> holidays;

    public HolidayService() {
        this.holidays = new ArrayList<>(CSVHandler.loadHolidays());
        if (holidays.isEmpty()) {
            seedDefaultHolidays();
        }
        System.out.println("Loaded " + holidays.size() + " holiday(s).");
    }

    public boolean isHoliday(LocalDate date) {
        return holidays.stream().anyMatch(h -> h.getDate().equals(date));
    }

    public String getHolidayName(LocalDate date) {
        return holidays.stream()
                .filter(h -> h.getDate().equals(date))
                .map(Holiday::getName)
                .findFirst()
                .orElse(null);
    }

    public boolean addHoliday(LocalDate date, String name) {
        if (isHoliday(date)) return false;
        holidays.add(new Holiday(date, name));
        holidays.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        CSVHandler.saveHolidays(holidays);
        return true;
    }

    public boolean removeHoliday(LocalDate date) {
        boolean removed = holidays.removeIf(h -> h.getDate().equals(date));
        if (removed) CSVHandler.saveHolidays(holidays);
        return removed;
    }

    public List<Holiday> getAllHolidays() { return new ArrayList<>(holidays); }

    private void seedDefaultHolidays() {
        int year = LocalDate.now().getYear();
        holidays.add(new Holiday(LocalDate.of(year, Month.JANUARY,  1),  "New Year's Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.APRIL,    9),  "Araw ng Kagitingan"));
        holidays.add(new Holiday(LocalDate.of(year, Month.MAY,      1),  "Labor Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.JUNE,     12), "Independence Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.AUGUST,   26), "National Heroes Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.NOVEMBER, 1),  "All Saints' Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.NOVEMBER, 30), "Bonifacio Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.DECEMBER, 25), "Christmas Day"));
        holidays.add(new Holiday(LocalDate.of(year, Month.DECEMBER, 30), "Rizal Day"));
        CSVHandler.saveHolidays(holidays);
        System.out.println("Seeded default Philippine holidays.");
    }
}
