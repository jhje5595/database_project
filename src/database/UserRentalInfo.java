package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserRentalInfo extends JFrame {
    private JPanel rentalPanel;
    private Connection conn;
    private String userId;

    public UserRentalInfo(Connection conn, String userId) {
        this.conn = conn;
        this.userId = userId;
        setTitle("내 대여 정보 조회");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        rentalPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        rentalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(rentalPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadUserRentals();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadUserRentals() {
        String license = null;

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

        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT r.대여번호, r.캠핑카등록ID, c.캠핑카이름, r.캠핑카대여회사ID, f.회사명, r.대여시작일, r.대여기간, r.청구요금, r.납입기한, r.기타청구내역, r.기타청구요금, c.캠핑카상세정보, " +
                "c.차량번호, c.승차인원수, c.캠핑카이미지, c.캠핑카대여비용, c.등록일자 " +
                "FROM 캠핑카대여 r " +
                "JOIN 캠핑카 c ON r.캠핑카등록ID = c.캠핑카등록ID " +
                "JOIN 캠핑카대여회사 f ON r.캠핑카대여회사ID = f.캠핑카대여회사ID " +
                "WHERE r.운전면허증번호 = ?"
            );
            pstmt.setString(1, license);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int rentalId = rs.getInt("대여번호");
                String camperId = rs.getString("캠핑카등록ID");
                String camperName = rs.getString("캠핑카이름");
                String companyName = rs.getString("회사명");
                LocalDate startDate = rs.getDate("대여시작일").toLocalDate();
                int period = rs.getInt("대여기간");
                int fee = rs.getInt("청구요금");
                LocalDate due = rs.getDate("납입기한").toLocalDate();
                String etc = rs.getString("기타청구내역");
                int etcFee = rs.getInt("기타청구요금");

                Map<String, String> detailMap = new HashMap<>();
                detailMap.put("캠핑카ID", camperId);
                detailMap.put("캠핑카명", camperName);
                detailMap.put("차량번호", rs.getString("차량번호"));
                detailMap.put("승차인원수", rs.getString("승차인원수"));
                detailMap.put("캠핑카이미지", rs.getString("캠핑카이미지"));
                detailMap.put("상세정보", rs.getString("캠핑카상세정보"));
                detailMap.put("대여비용", rs.getString("캠핑카대여비용"));
                detailMap.put("등록일자", rs.getString("등록일자"));

                JPanel rentalCard = createRentalCard(rentalId, camperName, companyName, startDate, period, fee, due, etc, etcFee, detailMap);
                rentalPanel.add(rentalCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "대여 정보 조회 실패");
        }
    }

    private JPanel createRentalCard(int rentalId, String camperName, String companyName, LocalDate startDate, int period, int fee, LocalDate due, String etc, int etcFee, Map<String, String> detailMap) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel camperNameLabel = new JLabel("캠핑카명: " + camperName);
        JLabel companyLabel = new JLabel("대여회사: " + companyName);
        JLabel startLabel = new JLabel("대여시작일: " + startDate);
        JLabel periodLabel = new JLabel("대여기간: " + period + "일");
        JLabel feeLabel = new JLabel("청구요금: " + fee + "원");
        JLabel dueLabel = new JLabel("납입기한: " + due);
        JLabel etcLabel = new JLabel("기타내역: " + etc);
        JLabel etcFeeLabel = new JLabel("기타요금: " + etcFee + "원");

        JButton detailBtn = new JButton("캠핑카 세부정보 보기");
        detailBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailBtn.addActionListener(e -> showDetailPopup(detailMap));

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "정말로 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM 캠핑카대여 WHERE 대여번호 = ?");
                    pstmt.setInt(1, rentalId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "삭제 완료");
                    dispose();
                    new UserRentalInfo(conn, userId);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "삭제 실패: " + ex.getMessage());
                }
            }
        });

        JPanel modifyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton changeCamperBtn = new JButton("캠핑카 변경");
        JButton changeDateBtn = new JButton("일정 변경");
//        JButton requestRepairBtn = new JButton("외부정비소 정비 의뢰");
//        requestRepairBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
//        requestRepairBtn.addActionListener(e -> {
//            new UserRequestRepair(conn, userId);
//        });


        changeCamperBtn.addActionListener(e -> 
        new UserChangeCampingCar(conn, userId, rentalId, startDate, period)
    );
     // 일정 변경
        changeDateBtn.addActionListener(e -> 
            new UserChangeSchedule(conn, userId, rentalId, detailMap.get("캠핑카ID"))
        );


        modifyPanel.add(changeCamperBtn);
        modifyPanel.add(changeDateBtn);

        panel.add(camperNameLabel);
        panel.add(companyLabel);
        panel.add(startLabel);
        panel.add(periodLabel);
        panel.add(feeLabel);
        panel.add(dueLabel);
        panel.add(etcLabel);
        panel.add(etcFeeLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(detailBtn);
        panel.add(Box.createVerticalStrut(5));
        //panel.add(requestRepairBtn);
        panel.add(deleteBtn);
        panel.add(modifyPanel);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        return panel;
    }

    private void showDetailPopup(Map<String, String> detailMap) {
        JDialog dialog = new JDialog(this, "캠핑카 세부정보", true);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : detailMap.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        area.setText(sb.toString());
        dialog.add(new JScrollPane(area));
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

// CamperChangeDialog, DateChangeDialog 클래스는 별도 구현 필요
