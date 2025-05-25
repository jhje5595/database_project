package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

public class UserRequestRepair extends JFrame { // 외부 정비 의뢰 클래스
    private Connection conn;
    private String userId;
    private String license;

    public UserRequestRepair(Connection conn, String userId, String camperId) {
        this.conn = conn;
        this.userId = userId;

        setTitle("외부 정비소 정비 의뢰");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField camperIdField = new JTextField(camperId);
        camperIdField.setEditable(false);

        JTextField repairShopNameField = new JTextField();

        JComboBox<String> detailCombo = new JComboBox<>(new String[]{ // 콤보박스 형태로 점검 목록 보여줌
            "타이어교체", "배터리 교체", "브레이크 수리", "전조등 교체",
            "에어컨 점검", "오일 교환", "냉각수 교체", "서스펜션 수리", "기어점검"
        });

        Map<String, Integer> detailCostMap = Map.of( // 점검하는 것에 따라서 그에 맞는 가격으로 Field에 보여주기 위해서 점검-비용 쌍 구성
            "타이어교체", 150000,
            "배터리 교체", 200000,
            "브레이크 수리", 180000,
            "전조등 교체", 90000,
            "에어컨 점검", 100000,
            "오일 교환", 70000,
            "냉각수 교체", 60000,
            "서스펜션 수리", 300000,
            "기어점검", 160000
        );

        JTextField costField = new JTextField(String.valueOf(detailCostMap.get(detailCombo.getSelectedItem())));
        costField.setEditable(false);

        JTextField extraDetailField = new JTextField();

        JButton viewShopBtn = new JButton("현재 등록 가능한 정비소 목록"); // 정비소명을 입력하기 위해서 현재 등록된 정비소들을 보여줌
        viewShopBtn.addActionListener(e -> showRepairShops());

        detailCombo.addActionListener(e -> {
            String selected = (String) detailCombo.getSelectedItem();
            costField.setText(String.valueOf(detailCostMap.get(selected)));
        });

        // ---- 컴포넌트 배치 ----
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("캠핑카 등록ID:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(camperIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        add(new JLabel("정비소 이름:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(repairShopNameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("정비 내역:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(detailCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("수리 비용:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(costField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("기타 정비 내역:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(extraDetailField, gbc);

        // viewShopBtn 을 버튼 위에 배치
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        add(viewShopBtn, gbc);

        // 버튼들
        JButton submitBtn = new JButton("정비 의뢰");
        JButton cancelBtn = new JButton("의뢰 취소하기");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        add(btnPanel, gbc);

        fetchLicense();

        submitBtn.addActionListener(e -> {
            try {
                String repairShopName = repairShopNameField.getText().trim();
                String detail = (String) detailCombo.getSelectedItem();
                int cost = Integer.parseInt(costField.getText());
                String extra = extraDetailField.getText();
                LocalDate today = LocalDate.now();
                LocalDate due = today.plusDays(7);
                
                if (extra.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "기타정비내역이 없는 경우 \"없음\"으로 입력해주세요.");
                    return;
                }

                PreparedStatement shopStmt = conn.prepareStatement(
                    "SELECT 캠핑카정비소ID FROM 외부캠핑카정비소 WHERE 정비소명 = ?");
                shopStmt.setString(1, repairShopName);
                ResultSet shopRs = shopStmt.executeQuery();
                if (!shopRs.next()) {
                    JOptionPane.showMessageDialog(this, "정비소 이름이 존재하지 않습니다.");
                    return;
                }
                String shopId = shopRs.getString(1);

                PreparedStatement companyStmt = conn.prepareStatement(
                    "SELECT 대여한회사ID FROM 캠핑카 WHERE 캠핑카등록ID = ?");
                companyStmt.setString(1, camperId);
                ResultSet companyRs = companyStmt.executeQuery();
                if (!companyRs.next()) {
                    JOptionPane.showMessageDialog(this, "캠핑카 회사 정보 없음");
                    return;
                }
                String companyId = companyRs.getString(1);

                Statement s = conn.createStatement();
                ResultSet maxRs = s.executeQuery("SELECT MAX(정비번호) FROM 외부정비정보");
                int nextId = 1;
                if (maxRs.next()) nextId = maxRs.getInt(1) + 1;

                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO 외부정비정보 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                insert.setInt(1, nextId);
                insert.setString(2, camperId);
                insert.setString(3, companyId);
                insert.setString(4, shopId);
                insert.setString(5, license);
                insert.setString(6, detail);
                insert.setDate(7, Date.valueOf(today));
                insert.setInt(8, cost);
                insert.setDate(9, Date.valueOf(due));
                insert.setString(10, extra);

                insert.executeUpdate();
                JOptionPane.showMessageDialog(this, "정비 의뢰 완료");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "정비 의뢰 실패: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showRepairShops() {
        JDialog dialog = new JDialog(this, "등록된 정비소 목록", true);
        JTextArea area = new JTextArea(10, 30);
        area.setEditable(false);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 정비소명 FROM 외부캠핑카정비소")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("- ").append(rs.getString(1)).append("\n");
            }
            area.setText(sb.toString());
        } catch (SQLException e) {
            area.setText("정비소 목록 조회 실패: " + e.getMessage());
        }

        dialog.add(new JScrollPane(area));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
