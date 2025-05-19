package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserRentalInfo extends JFrame {
    public UserRentalInfo(Connection conn, String userId) {
        setTitle("내 대여 정보 조회");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String license = null;

        // 먼저 고객의 운전면허증번호 찾기
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT 운전면허증번호 FROM 고객 WHERE 고객ID = ?"
            );
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                license = rs.getString(1);
            } else {
                JOptionPane.showMessageDialog(this, "고객 정보를 찾을 수 없습니다.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "운전면허증 조회 실패");
            return;
        }

        // 대여 정보 조회
        String[] columns = {"대여번호", "캠핑카ID", "대여회사ID", "대여시작일", "대여기간", "청구요금", "납입기한", "기타내역", "기타요금"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM 캠핑카대여 WHERE 운전면허증번호 = ?"
            );
            pstmt.setString(1, license);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[9];
                for (int i = 0; i < 9; i++) {
                    row[i] = rs.getObject(i + 1); // 1부터 시작
                }
                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "대여 정보 조회 실패");
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }
}