package panels.admin;

import navigation.NavigationManager;
import panels.BasePanel;
import service.AttendanceService;
import service.HolidayService;
import ui.StyledComponents;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DashboardPanel extends BasePanel {
    private final AttendanceService attendanceService;
    private final HolidayService    holidayService;

    private JLabel onTimeValue, lateValue, absentValue, totalValue;
    private JLabel clockLabel, dateLabel, monthYearLabel;
    private JPanel calendarGrid;
    private YearMonth displayedMonth;

    private static final Color PAGE_BG       = new Color(235, 238, 243);
    private static final Color TEXT_DARK     = new Color(15,  35,  65);
    private static final Color TEXT_GRAY     = new Color(130, 140, 155);
    private static final Color ACCENT_BLUE   = new Color(59,  100, 200);
    private static final Color ACCENT_AMBER  = new Color(210, 130,  30);
    private static final Color ACCENT_PURPLE = new Color(120,  80, 200);
    private static final Color ACCENT_TEAL   = new Color(30,  160, 130);

    public DashboardPanel(NavigationManager navManager,
                          AttendanceService attendanceService,
                          HolidayService holidayService) {
        super(navManager);
        this.attendanceService = attendanceService;
        this.holidayService    = holidayService;
        this.displayedMonth    = YearMonth.now();
        setBackground(PAGE_BG);
        setLayout(new BorderLayout());
        buildUI();
        startClock();
    }

    public DashboardPanel(NavigationManager navManager, AttendanceService attendanceService) {
        this(navManager, attendanceService, null);
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(PAGE_BG);
        root.setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.weighty = 0;
        g.insets  = new Insets(0, 0, 0, 0);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 1;
        root.add(buildHeader(), g);

        g.gridy = 1; g.insets = new Insets(18, 0, 0, 0);
        root.add(Box.createVerticalStrut(1), g);

        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        root.add(buildStatRow(), g);

        g.gridy = 3; g.insets = new Insets(16, 0, 0, 0);
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1.0;
        root.add(buildBottomRow(), g);

        add(root, BorderLayout.CENTER);
    }
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Inter", Font.BOLD, 30));
        title.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Administrator");
        sub.setFont(new Font("Inter", Font.PLAIN, 13));
        sub.setForeground(TEXT_GRAY);

        p.add(title, BorderLayout.WEST);
        p.add(sub,   BorderLayout.EAST);
        return p;
    }

    private JPanel buildStatRow() {
        onTimeValue = bigNum("0", ACCENT_BLUE);
        lateValue   = bigNum("0", ACCENT_AMBER);
        absentValue = bigNum("0", ACCENT_PURPLE);
        totalValue  = bigNum("5", ACCENT_TEAL);

        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);

        row.add(statCard("ON TIME TODAY",   onTimeValue, ACCENT_BLUE,   "✓"));
        row.add(statCard("LATE TODAY",      lateValue,   ACCENT_AMBER,  "⏱"));
        row.add(statCard("ABSENT",          absentValue, ACCENT_PURPLE, "✗"));
        row.add(statCard("TOTAL EMPLOYEES", totalValue,  ACCENT_TEAL,   "👥"));
        return row;
    }

    private JPanel statCard(String title, JLabel numLabel, Color accent, String icon) {
        JPanel card = whiteCard(accent);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(0, 110));

        // top: label left, icon right
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Inter", Font.BOLD, 10));
        lbl.setForeground(TEXT_GRAY);

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Inter", Font.PLAIN, 22));
        ico.setForeground(accent);

        top.add(lbl, BorderLayout.WEST);
        top.add(ico, BorderLayout.EAST);

        card.add(top,      BorderLayout.NORTH);
        card.add(numLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel bigNum(String val, Color color) {
        JLabel l = new JLabel(val);
        l.setFont(new Font("Inter", Font.BOLD, 42));
        l.setForeground(color);
        return l;
    }

    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.add(buildClockCard());
        row.add(buildCalendarCard());
        return row;
    }

    // Clock card

    private JPanel buildClockCard() {
        JPanel card = whiteCard(null);
        card.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        clockLabel = new JLabel("--:--:-- --", SwingConstants.CENTER);
        clockLabel.setFont(new Font("Inter", Font.BOLD, 46));
        clockLabel.setForeground(TEXT_DARK);
        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_GRAY);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 225, 235));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        badges.setOpaque(false);
        badges.add(shiftBadge("Shift Start", "8:00 AM", ACCENT_TEAL));
        badges.add(shiftBadge("Shift End",   "5:00 PM", ACCENT_PURPLE));

        inner.add(clockLabel);
        inner.add(Box.createVerticalStrut(4));
        inner.add(dateLabel);
        inner.add(Box.createVerticalStrut(14));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(14));
        inner.add(badges);

        card.add(inner);
        return card;
    }

    private JPanel shiftBadge(String label, String value, Color color) {
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        JLabel valLbl = new JLabel(value, SwingConstants.CENTER);
        valLbl.setFont(new Font("Inter", Font.BOLD, 16));
        valLbl.setForeground(color);
        valLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLbl = new JLabel(label, SwingConstants.CENTER);
        lblLbl.setFont(new Font("Inter", Font.PLAIN, 11));
        lblLbl.setForeground(TEXT_GRAY);
        lblLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        badge.add(valLbl);
        badge.add(lblLbl);
        return badge;
    }

    // Calendar card

    private JPanel buildCalendarCard() {
        JPanel card = whiteCard(null);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        JButton prev = calBtn("‹");
        JButton next = calBtn("›");
        prev.addActionListener(e -> { displayedMonth = displayedMonth.minusMonths(1); refreshCalendar(); });
        next.addActionListener(e -> { displayedMonth = displayedMonth.plusMonths(1);  refreshCalendar(); });

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Inter", Font.BOLD, 14));
        monthYearLabel.setForeground(TEXT_DARK);

        nav.add(prev,          BorderLayout.WEST);
        nav.add(monthYearLabel,BorderLayout.CENTER);
        nav.add(next,          BorderLayout.EAST);

        JPanel dayHeaders = new JPanel(new GridLayout(1, 7, 4, 0));
        dayHeaders.setOpaque(false);
        dayHeaders.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (int i = 0; i < 7; i++) {
            JLabel dl = new JLabel(days[i], SwingConstants.CENTER);
            dl.setFont(new Font("Inter", Font.BOLD, 11));
            dl.setForeground(i == 0 || i == 6 ? new Color(180, 60, 60) : TEXT_GRAY);
            dayHeaders.add(dl);
        }

        calendarGrid = new JPanel(new GridLayout(6, 7, 4, 6));
        calendarGrid.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(nav,        BorderLayout.NORTH);
        top.add(dayHeaders, BorderLayout.SOUTH);

        card.add(top,          BorderLayout.NORTH);
        card.add(calendarGrid, BorderLayout.CENTER);

        refreshCalendar();
        return card;
    }

    private void refreshCalendar() {
        if (calendarGrid == null) return;
        calendarGrid.removeAll();

        monthYearLabel.setText(
                displayedMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        + " " + displayedMonth.getYear());

        LocalDate today        = LocalDate.now();
        LocalDate firstOfMonth = displayedMonth.atDay(1);
        int startDow           = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth        = displayedMonth.lengthOfMonth();

        for (int i = 0; i < startDow; i++) calendarGrid.add(new JLabel(""));

        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate cellDate = displayedMonth.atDay(d);
            boolean isToday    = cellDate.equals(today);
            boolean isHoliday  = holidayService != null && holidayService.isHoliday(cellDate);
            boolean isWeekend  = cellDate.getDayOfWeek().getValue() >= 6;

            JLabel cell = new JLabel(String.valueOf(d), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isToday) {
                        g2.setColor(TEXT_DARK);
                        int s = Math.min(getWidth(), getHeight()) - 4;
                        g2.fillOval((getWidth()-s)/2, (getHeight()-s)/2, s, s);
                    } else if (isHoliday) {
                        g2.setColor(new Color(255, 224, 130));
                        int s = Math.min(getWidth(), getHeight()) - 4;
                        g2.fillOval((getWidth()-s)/2, (getHeight()-s)/2, s, s);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cell.setFont(new Font("Inter", isToday ? Font.BOLD : Font.PLAIN, 12));
            if      (isToday)    cell.setForeground(Color.WHITE);
            else if (isHoliday)  cell.setForeground(new Color(140, 80, 0));
            else if (isWeekend)  cell.setForeground(new Color(180, 60, 60));
            else                 cell.setForeground(new Color(50, 55, 70));

            if (isHoliday && holidayService != null)
                cell.setToolTipText(holidayService.getHolidayName(cellDate));

            calendarGrid.add(cell);
        }

        int total     = startDow + daysInMonth;
        int remainder = total % 7 == 0 ? 0 : 7 - (total % 7);
        for (int i = 0; i < remainder; i++) calendarGrid.add(new JLabel(""));

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }


    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            if (clockLabel != null)
                clockLabel.setText(now.format(DateTimeFormatter.ofPattern("h:mm:ss a")));
            if (dateLabel != null)
                dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        });
        timer.start();
    }

    private JPanel whiteCard(Color accentTop) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                if (accentTop != null) {
                    g2.setColor(accentTop);
                    g2.fillRect(0, 0, getWidth(), 4);
                }
                g2.setColor(new Color(215, 220, 232));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
    }

    private JButton calBtn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Inter", Font.BOLD, 20));
        b.setForeground(TEXT_GRAY);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(28, 28));
        return b;
    }

    @Override
    public void onShow() {
        onTimeValue.setText(String.valueOf(attendanceService.countOnTimeToday()));
        lateValue.setText(String.valueOf(attendanceService.countLateToday()));
        absentValue.setText(String.valueOf(attendanceService.countAbsentToday()));
        refreshCalendar();
    }
}