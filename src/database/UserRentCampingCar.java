//package database;
//
//import javax.swing.*;
//import java.awt.*;
//import java.sql.*;
//
//public class UserRentCampingCar extends JFrame {
//    public UserRentCampingCar(Connection conn, String userId) {
//        setTitle("캠핑카 대여 등록");
//        setSize(400, 300);
//        setLayout(new GridLayout(5, 2));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JTextField camperIdField = new JTextField();
//        JTextField startDateField = new JTextField("2025-06-01");  // yyyy-MM-dd
//        JTextField periodField = new JTextField();
//        JTextField 기타청구Field = new JTextField("없음");
//
//        add(new JLabel("캠핑카 등록ID:"));
//        add(camperIdField);
//        add(new JLabel("대여 시작일 (yyyy-MM-dd):"));
//        add(startDateField);
//        add(new JLabel("대여 기간 (일):"));
//        add(periodField);
//        add(new JLabel("기타 청구 내역:"));
//        add(기타청구Field);
//
//        JButton saveBtn = new JButton("대여 등록");
//        add(saveBtn);
//
//        saveBtn.addActionListener(e -> {
//            String camperId = camperIdField.getText();
//            String startDate = startDateField.getText();
//            int period = Integer.parseInt(periodField.getText());
//            String 기타내역 = 기타청구Field.getText();
//
//            try {
//                // 고객의 운전면허증번호 찾기
//                String license = null;
//                PreparedStatement find = conn.prepareStatement(
//                    "SELECT 운전면허증번호 FROM 고객 WHERE 고객ID = ?"
//                );
//                find.setString(1, userId);
//                ResultSet rs = find.executeQuery();
//                if (rs.next()) {
//                    license = rs.getString(1);
//                } else {
//                    JOptionPane.showMessageDialog(this, "고객 정보가 없습니다.");
//                    return;
//                }
//
//                // 청구요금 계산 (간단히 일당 * 기간 처리)
//                PreparedStatement feeQuery = conn.prepareStatement(
//                    "SELECT 캠핑카대여비용, 대여한회사ID FROM 캠핑카 WHERE 캠핑카등록ID = ?"
//                );
//                feeQuery.setString(1, camperId);
//                ResultSet feeRs = feeQuery.executeQuery();
//
//                if (!feeRs.next()) {
//                    JOptionPane.showMessageDialog(this, "해당 캠핑카를 찾을 수 없습니다.");
//                    return;
//                }
//
//                int 일당 = feeRs.getInt("캠핑카대여비용");
//                String 회사ID = feeRs.getString("대여한회사ID");
//                int 총비용 = 일당 * period;
//
//                // 다음 대여번호 자동 증가
//                Statement stmt = conn.createStatement();
//                ResultSet maxIdRs = stmt.executeQuery("SELECT MAX(대여번호) FROM 캠핑카대여");
//                int nextId = 1;
//                if (maxIdRs.next() && maxIdRs.getInt(1) > 0)
//                    nextId = maxIdRs.getInt(1) + 1;
//
//                PreparedStatement insert = conn.prepareStatement(
//                    "INSERT INTO 캠핑카대여 VALUES (?, ?, ?, ?, ?, ?, ?, DATE_ADD(?, INTERVAL ? DAY), ?, ?)"
//                );
//                insert.setInt(1, nextId);
//                insert.setString(2, camperId);
//                insert.setString(3, license);
//                insert.setString(4, 회사ID);
//                insert.setDate(5, Date.valueOf(startDate));
//                insert.setInt(6, period);
//                insert.setInt(7, 총비용);
//                insert.setDate(8, Date.valueOf(startDate));
//                insert.setInt(9, period);
//                insert.setString(10, 기타내역);
//                insert.setInt(11, 0); // 기타청구요금은 0원으로 고정
//
//                insert.executeUpdate();
//                JOptionPane.showMessageDialog(this, "대여 등록 성공!");
//                dispose();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this, "등록 실패: " + ex.getMessage());
//            }
//        });
//
//        setLocationRelativeTo(null);
//        setVisible(true);
//    }
//}