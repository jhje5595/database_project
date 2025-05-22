package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminUniversalInsert extends JFrame {
    private Connection conn;
    private JComboBox<String> tableComboBox;
    private JPanel formPanel;
    private JButton saveBtn, cancelBtn;

    public AdminUniversalInsert(Connection conn) {
        this.conn = conn;

        setTitle("테이블별 데이터 입력");
        setSize(500, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] tables = {
            "캠핑카대여회사", "캠핑카", "부품재고", "직원", "고객",
            "캠핑카대여", "외부캠핑카정비소", "자체정비기록", "외부정비정보"
        };
        tableComboBox = new JComboBox<>(tables);
        formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 5, 5));

        saveBtn = new JButton("저장");
        cancelBtn = new JButton("취소");

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("테이블 선택:"));
        topPanel.add(tableComboBox);
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        tableComboBox.addActionListener(e -> buildForm((String) tableComboBox.getSelectedItem()));
        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> insertData((String) tableComboBox.getSelectedItem()));

        buildForm((String) tableComboBox.getSelectedItem());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildForm(String tableName) {
        formPanel.removeAll();

        String[] fields = getFieldsForTable(tableName);
        for (String field : fields) {
            formPanel.add(new JLabel(field + ":"));
            formPanel.add(new JTextField());
        }
        formPanel.revalidate();
        formPanel.repaint();
    }

    private String[] getFieldsForTable(String table) {
        switch (table) {
            case "캠핑카대여회사":
                return new String[]{"캠핑카대여회사ID", "회사명", "주소", "전화번호", "담당자이름", "담당자이메일"};
            case "캠핑카":
                return new String[]{"캠핑카등록ID", "캠핑카이름", "차량번호", "승차인원수", "캠핑카이미지", "캠핑카상세정보", "캠핑카대여비용", "대여한회사ID", "등록일자"};
            case "부품재고":
                return new String[]{"부품등록ID", "부품명", "부품단가", "재고수량", "입고날짜", "공급회사이름"};
            case "직원":
                return new String[]{"직원ID", "직원이름", "휴대폰번호", "집주소", "월급여", "부양가족수", "부서명", "담당업무"};
            case "고객":
                return new String[]{"고객ID", "비밀번호", "운전면허증번호", "고객명", "고객주소", "고객전화번호", "고객 이메일", "이전캠핑카사용날짜", "이전캠핑카종류"};
            case "캠핑카대여":
                return new String[]{"대여번호", "캠핑카등록ID", "운전면허증번호", "캠핑카대여회사ID", "대여시작일", "대여기간", "청구요금", "납입기한", "기타청구내역", "기타청구요금"};
            case "외부캠핑카정비소":
                return new String[]{"캠핑카정비소ID", "정비소명", "정비소주소", "정비소전화번호", "담당자이름", "담당자이메일"};
            case "자체정비기록":
                return new String[]{"자체정비등록ID", "캠핑카등록ID", "부품등록ID", "정비일자", "정비시간", "정비담당자ID"};
            case "외부정비정보":
                return new String[]{"정비번호", "캠핑카등록ID", "캠핑카대여회사ID", "캠핑카정비소ID", "고객운전면허증번호", "정비내역", "수리날짜", "수리비용", "납입기한", "기타정비내역"};
            default:
                return new String[]{};
        }
    }

    private void insertData(String table) {
        Component[] comps = formPanel.getComponents();
        int fieldCount = comps.length / 2;
        StringBuilder sql = new StringBuilder("INSERT INTO `" + table + "` (");
        StringBuilder placeholders = new StringBuilder("VALUES (");
        String[] values = new String[fieldCount];

        String[] fields = getFieldsForTable(table);
        for (int i = 0; i < fieldCount; i++) {
            String value = ((JTextField) comps[i * 2 + 1]).getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해야 합니다.");
                return;
            }
            values[i] = value;
        }

        for (int i = 0; i < fields.length; i++) {
            sql.append("`").append(fields[i]).append("`");
            placeholders.append("?");
            if (i < fields.length - 1) {
                sql.append(", ");
                placeholders.append(", ");
            }
        }

        sql.append(") ").append(placeholders).append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "저장 성공!");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "저장 실패: " + e.getMessage());
        }
    }
}
