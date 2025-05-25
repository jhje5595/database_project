package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class UserChangeSchedule extends JDialog {
    public UserChangeSchedule(Connection conn, String userId, int rentalId, String camperId) {
        setTitle("일정 변경");
        setSize(400, 250);
        setLayout(new BorderLayout());
        setModal(true);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField startDateField = new JTextField();
        JTextField periodField = new JTextField();

        inputPanel.add(new JLabel("새 대여 시작일 (yyyy-MM-dd):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("대여 기간 (일):"));
        inputPanel.add(periodField);

        add(inputPanel, BorderLayout.CENTER);

        JButton updateBtn = new JButton("변경하기");
        JButton cancelBtn = new JButton("변경 취소");

        updateBtn.addActionListener(e -> {
            try {
                String startDateStr = startDateField.getText().trim();
                String periodStr = periodField.getText().trim();

                LocalDate newStart;
                int newPeriod;

                // 날짜 형식 유효성 검사
                try {
                    newStart = LocalDate.parse(startDateStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "날짜 형식이 잘못되었습니다. (예: 2025-06-01)");
                    return;
                }

                // 대여 기간 유효성 검사
                try {
                    newPeriod = Integer.parseInt(periodStr);
                    if (newPeriod <= 0) {
                        JOptionPane.showMessageDialog(this, "대여 기간은 1일 이상이어야 합니다.");
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "대여 기간은 숫자로 입력해주세요.");
                    return;
                }

                // 1. 해당 캠핑카가 이미 예약되어 있는지 확인
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

                // 2. 캠핑카의 일일 대여비용 조회
                PreparedStatement feeQuery = conn.prepareStatement(
                    "SELECT 캠핑카대여비용 FROM 캠핑카 WHERE 캠핑카등록ID = ?"
                );
                feeQuery.setString(1, camperId);
                ResultSet feeRs = feeQuery.executeQuery();
                if (!feeRs.next()) {
                    JOptionPane.showMessageDialog(this, "캠핑카 요금 정보를 찾을 수 없습니다.");
                    return;
                }
                int dailyFee = feeRs.getInt("캠핑카대여비용");
                int totalFee = dailyFee * newPeriod;

                // 3. 일정 및 요금 업데이트
                PreparedStatement update = conn.prepareStatement(
                    "UPDATE 캠핑카대여 SET 대여시작일 = ?, 대여기간 = ?, 납입기한 = DATE_ADD(?, INTERVAL ? DAY), 청구요금 = ? WHERE 대여번호 = ?"
                );
                update.setDate(1, Date.valueOf(newStart));
                update.setInt(2, newPeriod);
                update.setDate(3, Date.valueOf(newStart));
                update.setInt(4, newPeriod);
                update.setInt(5, totalFee);
                update.setInt(6, rentalId);
                update.executeUpdate();

                JOptionPane.showMessageDialog(this, "일정 및 요금 변경 완료");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "변경 실패: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
