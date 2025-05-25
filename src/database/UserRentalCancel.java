package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserRentalCancel extends JFrame { // 대여한 캠핑카 예약 취소하기
    private JTable table;
    private DefaultTableModel model;
    private String license;
    private Connection conn;

    public UserRentalCancel(Connection conn, String userId) {
        this.conn = conn;
        setTitle("내 대여 정보 취소");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"대여번호", "캠핑카ID", "대여회사ID", "대여시작일", "대여기간", "청구요금", "납입기한", "기타내역", "기타요금"}, 0
        );
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 버튼 패널
        JPanel btnPanel = new JPanel();
        JButton deleteBtn = new JButton("선택한 대여 취소");
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> deleteSelectedRental());

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
                        rs.getInt("청구요금"),
                        rs.getDate("납입기한"),
                        rs.getString("기타청구내역"),
                        rs.getInt("기타청구요금")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedRental() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 대여를 선택하세요.");
            return;
        }

        int rentalId = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "정말로 대여를 취소하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM 캠핑카대여 WHERE 대여번호 = ?");
            stmt.setInt(1, rentalId);
            int result = stmt.executeUpdate();

            if (result > 0) {
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "대여 취소 완료!");
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패. 다시 시도해주세요.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB 오류 발생");
        }
    }
}