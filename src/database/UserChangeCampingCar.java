package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserChangeCampingCar extends JDialog {
    public UserChangeCampingCar(Connection conn, String userId, int rentalId, LocalDate startDate, int period) {
        setTitle("캠핑카 변경");
        setSize(400, 200);
        setLayout(new BorderLayout());
        setModal(true);

        JComboBox<String> camperBox = new JComboBox<>();
        Map<String, String> camperMap = new HashMap<>();

        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT 캠핑카등록ID, 캠핑카이름 FROM 캠핑카 WHERE 캠핑카등록ID NOT IN (" +
                    "SELECT 캠핑카등록ID FROM 캠핑카대여 " +
                    "WHERE NOT (DATE_ADD(대여시작일, INTERVAL 대여기간 DAY) <= ? OR 대여시작일 >= ?)"
                + ")"
            );
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(startDate.plusDays(period)));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("캠핑카등록ID");
                String name = rs.getString("캠핑카이름");
                camperBox.addItem(name);
                camperMap.put(name, id);  // 이름 → ID 매핑
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton updateBtn = new JButton("변경하기");
        updateBtn.addActionListener(e -> {
            String selectedName = (String) camperBox.getSelectedItem();
            String newCamperId = camperMap.get(selectedName);
            if (newCamperId != null) {
                try {
                    PreparedStatement update = conn.prepareStatement(
                        "UPDATE 캠핑카대여 SET 캠핑카등록ID = ? WHERE 대여번호 = ?"
                    );
                    update.setString(1, newCamperId);
                    update.setInt(2, rentalId);
                    update.executeUpdate();
                    JOptionPane.showMessageDialog(this, "캠핑카 변경 완료");
                    dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "변경 실패: " + ex.getMessage());
                }
            }
        });

        add(new JLabel("변경할 캠핑카 선택:"), BorderLayout.NORTH);
        add(camperBox, BorderLayout.CENTER);
        add(updateBtn, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
