package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserChangeSchedule extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private String license;
    private Connection conn;

    public UserChangeSchedule(Connection conn, String userId) {
        this.conn = conn;
        setTitle("대여 일정 변경");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"대여번호", "캠핑카ID", "대여시작일", "대여기간", "납입기한"}, 0
        );
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton changeBtn = new JButton("일정 변경");
        JPanel btnPanel = new JPanel();
        btnPanel.add(changeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        changeBtn.addActionListener(e -> changeSchedule());

        loadLicense(userId);
        loadRentals();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadLicense(String userId) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT 운전면허증번호 FROM 고객 WHERE 고객ID = ?");
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                license = rs.getString(1);
            } else {
                JOptionPane.showMessageDialog(this, "고객 정보 없음");
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRentals() {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT 대여번호, 캠핑카등록ID, 대여시작일, 대여기간, 납입기한 FROM 캠핑카대여 WHERE 운전면허증번호 = ?"
            );
            stmt.setString(1, license);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("대여번호"),
                        rs.getString("캠핑카등록ID"),
                        rs.getDate("대여시작일"),
                        rs.getInt("대여기간"),
                        rs.getDate("납입기한")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeSchedule() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "변경할 대여를 선택하세요.");
            return;
        }

        int rentalId = (int) model.getValueAt(row, 0);
        String newStartDate = JOptionPane.showInputDialog(this, "새 대여 시작일 (yyyy-MM-dd):");
        String newPeriodStr = JOptionPane.showInputDialog(this, "새 대여 기간 (일):");

        if (newStartDate == null || newPeriodStr == null) {
            JOptionPane.showMessageDialog(this, "입력이 취소되었습니다.");
            return;
        }

        try {
            int newPeriod = Integer.parseInt(newPeriodStr);

            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE 캠핑카대여 SET 대여시작일 = ?, 대여기간 = ?, 납입기한 = DATE_ADD(?, INTERVAL ? DAY) WHERE 대여번호 = ?"
            );
            stmt.setDate(1, Date.valueOf(newStartDate));
            stmt.setInt(2, newPeriod);
            stmt.setDate(3, Date.valueOf(newStartDate));
            stmt.setInt(4, newPeriod);
            stmt.setInt(5, rentalId);

            int result = stmt.executeUpdate();
            if (result > 0) {
                model.setValueAt(Date.valueOf(newStartDate), row, 2);
                model.setValueAt(newPeriod, row, 3);
                model.setValueAt(Date.valueOf(newStartDate).toLocalDate().plusDays(newPeriod), row, 4);
                JOptionPane.showMessageDialog(this, "일정 변경 완료!");
            } else {
                JOptionPane.showMessageDialog(this, "변경 실패");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB 오류 발생");
        }
    }
}