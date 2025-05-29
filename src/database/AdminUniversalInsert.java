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
	            return new String[]{"캠핑카대여회사ID (예 : C001)", "회사명 (예 : 캠핑이좋아)", "주소 (예 : 서울시 강남구)", "전화번호 (예 : 010-1111-1111)", "담당자이름 (예 : 홍길동)", "담당자이메일 (예 : hong@example.com)"};
	        case "캠핑카":
	            return new String[]{"캠핑카등록ID (예 : RV001)", "캠핑카이름 (예 : 럭셔리 캠핑카)", "차량번호 (예 : 11가1234)", "승차인원수 (예 : 4)", "캠핑카이미지 (예 : image1.jpg)", "캠핑카상세정보 (예 : 풀옵션)", "캠핑카대여비용 (예 : 150000)", "대여한회사ID (예 : C001)", "등록일자 (예 : 2025-05-01)"};
	        case "부품재고":
	            return new String[]{"부품등록ID (예 : P001)", "부품명 (예 : 타이어)", "부품단가 (예 : 80000)", "재고수량 (예 : 20)", "입고날짜 (예 : 2025-05-01)", "공급회사이름 (예 : 한국타이어)"};
	        case "직원":
	            return new String[]{"직원ID (예 : E001)", "직원이름 (예 : 박민수)", "휴대폰번호 (예 : 010-5555-6666)", "집주소 (예 : 서울 송파구)", "월급여 (예 : 3200000)", "부양가족수 (예 : 2)", "부서명 (예 : 정비부)", "담당업무 (예 : 정비)"};
	        case "고객":
	            return new String[]{"고객ID (예 : U001)", "비밀번호 (예 : pass123)", "운전면허증번호 (예 : DL1234567)", "고객명 (예 : 서강준)", "고객주소 (예 : 서울 강북구)", "고객전화번호 (예 : 010-7777-8888)", "고객 이메일 (예 : seo@example.com)", "이전캠핑카사용날짜 (예 : 2025-05-01)", "이전캠핑카종류 (예 : 럭셔리 캠핑카)"};
	        case "캠핑카대여":
	            return new String[]{"대여번호 (예 : 1)", "캠핑카등록ID (예 : RV001)", "운전면허증번호 (예 : DL1234567)", "캠핑카대여회사ID (예 : C001)", "대여시작일 (예 : 2025-05-01)", "대여기간 (예 : 3)", "청구요금 (예 : 450000)", "납입기한 (예 : 2025-05-04)", "기타청구내역 (예 : 침낭 추가)", "기타청구요금 (예 : 30000)"};
	        case "외부캠핑카정비소":
	            return new String[]{"캠핑카정비소ID (예 : RS001)", "정비소명 (예 : 정비마스터)", "정비소주소 (예 : 서울 은평구)", "정비소전화번호 (예 : 02-111-2222)", "담당자이름 (예 : 조세호)", "담당자이메일 (예 : cho@example.com)"};
	        case "자체정비기록":
	            return new String[]{"자체정비등록ID (예 : SR001)", "캠핑카등록ID (예 : RV001)", "부품등록ID (예 : P001)", "정비일자 (예 : 2025-05-01)", "정비시간 (예 : 90)", "정비담당자ID (예 : E001)"};
	        case "외부정비정보":
	            return new String[]{"정비번호 (예 : 1)", "캠핑카등록ID (예 : RV001)", "캠핑카대여회사ID (예 : C001)", "캠핑카정비소ID (예 : RS001)", "고객운전면허증번호 (예 : DL1234567)", "정비내역 (예 : 타이어 교체)", "수리날짜 (예 : 2025-05-01)", "수리비용 (예 : 150000)", "납입기한 (예 : 2025-05-04)", "기타정비내역 (예 : 추가 점검 포함)"};
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
