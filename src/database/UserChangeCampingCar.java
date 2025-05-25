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
                    "WHERE NOT (DATE_ADD(대여시작일, INTERVAL 대여기간 DAY) <= ? OR 대여시작일 >= ?)" +
                    ")"
            );
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(startDate.plusDays(period)));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("캠핑카등록ID");
                String name = rs.getString("캠핑카이름");
                camperBox.addItem(name);
                camperMap.put(name, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton updateBtn = new JButton("변경하기");
        JButton cancelBtn = new JButton("변경 취소");

        updateBtn.addActionListener(e -> {
            String selectedName = (String) camperBox.getSelectedItem();
            String newCamperId = camperMap.get(selectedName);
            if (newCamperId != null) {
                try {
                    // 1. 새 캠핑카의 회사 ID와 대여 비용 조회
                    PreparedStatement infoStmt = conn.prepareStatement(
                        "SELECT 대여한회사ID, 캠핑카대여비용 FROM 캠핑카 WHERE 캠핑카등록ID = ?"
                    );
                    infoStmt.setString(1, newCamperId);
                    ResultSet infoRs = infoStmt.executeQuery();
                    if (!infoRs.next()) {
                        JOptionPane.showMessageDialog(this, "캠핑카 정보를 찾을 수 없습니다.");
                        return;
                    }
                    String newCompanyId = infoRs.getString("대여한회사ID");
                    int dailyFee = infoRs.getInt("캠핑카대여비용");
                    int totalFee = dailyFee * period;

                    // 2. 캠핑카, 회사ID, 요금 모두 변경
                    PreparedStatement update = conn.prepareStatement(
                        "UPDATE 캠핑카대여 SET 캠핑카등록ID = ?, 캠핑카대여회사ID = ?, 청구요금 = ? WHERE 대여번호 = ?"
                    );
                    update.setString(1, newCamperId);
                    update.setString(2, newCompanyId);
                    update.setInt(3, totalFee);
                    update.setInt(4, rentalId);
                    update.executeUpdate();

                    JOptionPane.showMessageDialog(this, "캠핑카 및 요금 변경 완료");
                    dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "변경 실패: " + ex.getMessage());
                }
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(new JLabel("변경할 캠핑카 선택(일정이 겹치지 않는 캠핑카만 리스트에 존재합니다.)"), BorderLayout.NORTH);
        formPanel.add(camperBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
