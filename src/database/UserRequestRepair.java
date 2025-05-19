package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class UserRequestRepair extends JFrame {
    private Connection conn;
    private String userId;
    private String license;

    public UserRequestRepair(Connection conn, String userId) {
        this.conn = conn;
        this.userId = userId;

        setTitle("외부 정비소 정비 의뢰");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 5, 5));

        JTextField camperIdField = new JTextField();
        JTextField repairShopIdField = new JTextField();
        JTextField detailsField = new JTextField();
        JTextField costField = new JTextField("0");
        JTextField extraDetailField = new JTextField();

        add(new JLabel("캠핑카 등록ID:"));
        add(camperIdField);
        add(new JLabel("정비소 ID:"));
        add(repairShopIdField);
        add(new JLabel("정비 내역:"));
        add(detailsField);
        add(new JLabel("수리 비용:"));
        add(costField);
        add(new JLabel("기타 정비 내역:"));
        add(extraDetailField);

        JButton submitBtn = new JButton("정비 의뢰");
        add(new JLabel()); // placeholder
        add(submitBtn);

        fetchLicense();

        submitBtn.addActionListener(e -> {
            try {
                String camperId = camperIdField.getText();
                String repairShopId = repairShopIdField.getText();
                String details = detailsField.getText();
                int cost = Integer.parseInt(costField.getText());
                String extra = extraDetailField.getText();

                // 캠핑카 회사 ID 가져오기
                PreparedStatement p = conn.prepareStatement("SELECT 대여한회사ID FROM 캠핑카 WHERE 캠핑카등록ID = ?");
                p.setString(1, camperId);
                ResultSet rs = p.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "캠핑카 ID가 존재하지 않습니다.");
                    return;
                }
                String companyId = rs.getString(1);

                // 정비번호 자동 증가
                Statement s = conn.createStatement();
                ResultSet max = s.executeQuery("SELECT MAX(정비번호) FROM 외부정비정보");
                int nextId = 1;
                if (max.next() && max.getInt(1) > 0) nextId = max.getInt(1) + 1;

                LocalDate today = LocalDate.now();
                LocalDate due = today.plusDays(7);

                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO 외부정비정보 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                insert.setInt(1, nextId);
                insert.setString(2, camperId);
                insert.setString(3, companyId);
                insert.setString(4, repairShopId);
                insert.setString(5, license);
                insert.setString(6, details);
                insert.setDate(7, Date.valueOf(today));
                insert.setInt(8, cost);
                insert.setDate(9, Date.valueOf(due));
                insert.setString(10, extra);

                insert.executeUpdate();
                JOptionPane.showMessageDialog(this, "정비 의뢰 완료!");
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "정비 의뢰 실패");
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchLicense() {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT 운전면허증번호 FROM 고객 WHERE 고객ID = ?");
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                license = rs.getString(1);
            } else {
                JOptionPane.showMessageDialog(this, "고객 정보를 찾을 수 없습니다.");
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}