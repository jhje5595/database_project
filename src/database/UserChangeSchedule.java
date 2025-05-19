package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class UserChangeSchedule extends JDialog {
    public UserChangeSchedule(Connection conn, String userId, int rentalId, String camperId) {
        setTitle("일정 변경");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2));
        setModal(true);

        JTextField startDateField = new JTextField();
        JTextField periodField = new JTextField();
        JButton updateBtn = new JButton("변경하기");

        add(new JLabel("새 대여 시작일 (yyyy-MM-dd):"));
        add(startDateField);
        add(new JLabel("대여 기간 (일):"));
        add(periodField);
        add(new JLabel());
        add(updateBtn);

        updateBtn.addActionListener(e -> {
            try {
                LocalDate newStart = LocalDate.parse(startDateField.getText());
                int newPeriod = Integer.parseInt(periodField.getText());

                PreparedStatement conflictCheck = conn.prepareStatement(
                    "SELECT * FROM 캠핑카대여 WHERE 캠핑카등록ID = ? AND 대여번호 != ? " +
                    "AND NOT (DATE_ADD(대여시작일, INTERVAL 대여기간 DAY) <= ? OR 대여시작일 >= ?)"
                );
                conflictCheck.setString(1, camperId);
                conflictCheck.setInt(2, rentalId);
                conflictCheck.setDate(3, Date.valueOf(newStart));
                conflictCheck.setDate(4, Date.valueOf(newStart.plusDays(newPeriod)));
                ResultSet rs = conflictCheck.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "해당 일정에 이미 대여가 있습니다.");
                    return;
                }

                PreparedStatement update = conn.prepareStatement(
                    "UPDATE 캠핑카대여 SET 대여시작일 = ?, 대여기간 = ?, 납입기한 = DATE_ADD(?, INTERVAL ? DAY) WHERE 대여번호 = ?"
                );
                update.setDate(1, Date.valueOf(newStart));
                update.setInt(2, newPeriod);
                update.setDate(3, Date.valueOf(newStart));
                update.setInt(4, newPeriod);
                update.setInt(5, rentalId);
                update.executeUpdate();
                JOptionPane.showMessageDialog(this, "일정 변경 완료");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "변경 실패: " + ex.getMessage());
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}