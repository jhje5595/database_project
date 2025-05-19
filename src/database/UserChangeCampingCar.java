package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserChangeCampingCar extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private String license;
    private Connection conn;

    public UserChangeCampingCar(Connection conn, String userId) {
        this.conn = conn;
        setTitle("대여 캠핑카 변경");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"대여번호", "캠핑카ID", "대여회사ID", "대여시작일", "대여기간", "청구요금"}, 0
        );
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton changeBtn = new JButton("캠핑카 변경");
        btnPanel.add(changeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        changeBtn.addActionListener(e -> changeCamper());

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
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM 캠핑카대여 WHERE 운전면허증번호 = ?");
            stmt.setString(1, license);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("대여번호"),
                        rs.getString("캠핑카등록ID"),
                        rs.getString("캠핑카대여회사ID"),
                        rs.getDate("대여시작일"),
                        rs.getInt("대여기간"),
                        rs.getInt("청구요금")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeCamper() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "변경할 대여를 선택하세요.");
            return;
        }

        int rentalId = (int) model.getValueAt(row, 0);
        String newCamperId = JOptionPane.showInputDialog(this, "새 캠핑카 등록 ID 입력:");

        if (newCamperId == null || newCamperId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "입력된 캠핑카 ID가 유효하지 않습니다.");
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE 캠핑카대여 SET 캠핑카등록ID = ? WHERE 대여번호 = ?"
            );
            stmt.setString(1, newCamperId);
            stmt.setInt(2, rentalId);

            int result = stmt.executeUpdate();
            if (result > 0) {
                model.setValueAt(newCamperId, row, 1); // 테이블에도 반영
                JOptionPane.showMessageDialog(this, "캠핑카 변경 완료!");
            } else {
                JOptionPane.showMessageDialog(this, "변경 실패. 다시 시도하세요.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB 오류 발생");
        }
    }
}