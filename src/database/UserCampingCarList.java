package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

public class UserCampingCarList extends JFrame {
    private JPanel camperPanel;
    private Connection conn;
    private String userId;

    public UserCampingCarList(Connection conn, String userId) {
        this.conn = conn;
        this.userId = userId;
        setTitle("캠핑카 목록");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        camperPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        camperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(camperPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadCampingCars();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadCampingCars() {
        String sql = "SELECT 캠핑카등록ID, 캠핑카이름, 차량번호, 승차인원수, 캠핑카대여비용, 캠핑카이미지 FROM 캠핑카";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("캠핑카등록ID");
                String name = rs.getString("캠핑카이름");
                String number = rs.getString("차량번호");
                int people = rs.getInt("승차인원수");
                int price = rs.getInt("캠핑카대여비용");
                String imageFile = rs.getString("캠핑카이미지");

                JPanel camperCard = createCamperCard(id, name, number, people, price, imageFile);
                camperPanel.add(camperCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "캠핑카 정보를 불러오지 못했습니다.");
        }
    }

    private JPanel createCamperCard(String id, String name, String number, int people, int price, String imageFile) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String imagePath = "images/" + imageFile;
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("이름: " + name);
        JLabel numberLabel = new JLabel("차량번호: " + number);
        JLabel peopleLabel = new JLabel("인원수: " + people + "명");
        JLabel priceLabel = new JLabel("대여비: " + price + "원");

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        peopleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton viewDateButton = new JButton("대여 가능일 보기");
        viewDateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewDateButton.addActionListener(e -> new UserCanRentDay(conn, id));

        JButton rentButton = new JButton("이 캠핑카 대여하기");
        rentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rentButton.addActionListener(e -> new UserRentCampingCar(conn, userId, id));

        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(nameLabel);
        panel.add(numberLabel);
        panel.add(peopleLabel);
        panel.add(priceLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewDateButton);
        panel.add(rentButton);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        return panel;
    }
}

class UserCanRentDay extends JFrame {
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private YearMonth currentMonth;
    private Connection conn;
    private String camperId;

    public UserCanRentDay(Connection conn, String camperId) {
        this.conn = conn;
        this.camperId = camperId;
        this.currentMonth = YearMonth.now();

        setTitle("대여 가능일자 조회");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);

        prevButton.addActionListener(this::showPreviousMonth);
        nextButton.addActionListener(this::showNextMonth);

        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        calendarPanel = new JPanel(new GridLayout(0, 7));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(calendarPanel, BorderLayout.CENTER);

        renderCalendar();
        setVisible(true);
    }

    private void renderCalendar() {
        calendarPanel.removeAll();
        monthLabel.setText(currentMonth.getYear() + "년 " + currentMonth.getMonthValue() + "월");

        Set<LocalDate> unavailableDates = getUnavailableDatesFromDB(conn, camperId);
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();

        for (int i = 0; i < firstDay.getDayOfWeek().getValue() % 7; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (LocalDate day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
            JLabel label = new JLabel(String.valueOf(day.getDayOfMonth()), SwingConstants.CENTER);
            label.setOpaque(true);
            if (unavailableDates.contains(day)) {
                label.setBackground(Color.GRAY);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            calendarPanel.add(label);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showPreviousMonth(ActionEvent e) {
        currentMonth = currentMonth.minusMonths(1);
        renderCalendar();
    }

    private void showNextMonth(ActionEvent e) {
        currentMonth = currentMonth.plusMonths(1);
        renderCalendar();
    }

    private Set<LocalDate> getUnavailableDatesFromDB(Connection conn, String camperId) {
        Set<LocalDate> dates = new HashSet<>();
        String sql = "SELECT 대여시작일, 대여기간 FROM 캠핑카대여 WHERE 캠핑카등록ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, camperId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate start = rs.getDate("대여시작일").toLocalDate();
                int days = rs.getInt("대여기간");
                for (int i = 0; i < days; i++) {
                    dates.add(start.plusDays(i));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "날짜 조회 실패: " + e.getMessage());
        }

        return dates;
    }
}

class UserRentCampingCar extends JFrame {
    public UserRentCampingCar(Connection conn, String userId, String defaultCamperId) {
        setTitle("캠핑카 대여 등록");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField camperIdField = new JTextField(defaultCamperId);
        JTextField startDateField = new JTextField("2025-06-01");
        JTextField periodField = new JTextField();
        JTextField 기타청구Field = new JTextField("없음");

        add(new JLabel("캠핑카 등록ID:"));
        add(camperIdField);
        add(new JLabel("대여 시작일 (yyyy-MM-dd):"));
        add(startDateField);
        add(new JLabel("대여 기간 (일):"));
        add(periodField);
        add(new JLabel("기타 청구 내역:"));
        add(기타청구Field);

        JButton saveBtn = new JButton("대여 등록");
        add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String camperId = camperIdField.getText();
                String startDate = startDateField.getText();
                int period = Integer.parseInt(periodField.getText());
                String 기타내역 = 기타청구Field.getText();
                LocalDate start = LocalDate.parse(startDate);

                // 대여 중복 체크
                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT 대여시작일, 대여기간 FROM 캠핑카대여 WHERE 캠핑카등록ID = ?"
                );
                checkStmt.setString(1, camperId);
                ResultSet checkRs = checkStmt.executeQuery();
                while (checkRs.next()) {
                    LocalDate existingStart = checkRs.getDate(1).toLocalDate();
                    int existingPeriod = checkRs.getInt(2);
                    LocalDate existingEnd = existingStart.plusDays(existingPeriod);
                    if (!start.plusDays(period).isBefore(existingStart) && !start.isAfter(existingEnd)) {
                        JOptionPane.showMessageDialog(this, "해당 날짜에는 대여가 불가능합니다.");
                        return;
                    }
                }

                String license = null;
                PreparedStatement find = conn.prepareStatement(
                    "SELECT 운전면허증번호 FROM 고객 WHERE 고객ID = ?"
                );
                find.setString(1, userId);
                ResultSet rs = find.executeQuery();
                if (rs.next()) {
                    license = rs.getString(1);
                } else {
                    JOptionPane.showMessageDialog(this, "고객 정보가 없습니다.");
                    return;
                }

                PreparedStatement feeQuery = conn.prepareStatement(
                    "SELECT 캠핑카대여비용, 대여한회사ID FROM 캠핑카 WHERE 캠핑카등록ID = ?"
                );
                feeQuery.setString(1, camperId);
                ResultSet feeRs = feeQuery.executeQuery();
                if (!feeRs.next()) {
                    JOptionPane.showMessageDialog(this, "해당 캠핑카를 찾을 수 없습니다.");
                    return;
                }
                int 일당 = feeRs.getInt("캠핑카대여비용");
                String 회사ID = feeRs.getString("대여한회사ID");
                int 총비용 = 일당 * period;

                Statement stmt = conn.createStatement();
                ResultSet maxIdRs = stmt.executeQuery("SELECT MAX(대여번호) FROM 캠핑카대여");
                int nextId = 1;
                if (maxIdRs.next() && maxIdRs.getInt(1) > 0)
                    nextId = maxIdRs.getInt(1) + 1;

                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO 캠핑카대여 VALUES (?, ?, ?, ?, ?, ?, ?, DATE_ADD(?, INTERVAL ? DAY), ?, ?)"
                );
                insert.setInt(1, nextId);
                insert.setString(2, camperId);
                insert.setString(3, license);
                insert.setString(4, 회사ID);
                insert.setDate(5, Date.valueOf(startDate));
                insert.setInt(6, period);
                insert.setInt(7, 총비용);
                insert.setDate(8, Date.valueOf(startDate));
                insert.setInt(9, period);
                insert.setString(10, 기타내역);
                insert.setInt(11, 0);

                insert.executeUpdate();
                JOptionPane.showMessageDialog(this, "대여 등록 성공!");
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "등록 실패: " + ex.getMessage());
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
