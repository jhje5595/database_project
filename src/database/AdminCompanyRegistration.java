//package database;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//
//public class AdminCompanyRegistration extends JFrame {
//    private Connection conn;
//    private JTextField companyIdField, companyNameField, addressField, phoneField, managerNameField, managerEmailField;
//
//    public AdminCompanyRegistration(Connection conn) {
//        this.conn = conn;
//        
//
//        setTitle("캠핑카 대여회사 등록");
//        setSize(400, 400);
//        setLayout(new GridLayout(7, 2, 10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫아도 메인 프로그램은 살아있음
//
//        companyIdField = new JTextField();
//        companyNameField = new JTextField();
//        addressField = new JTextField();
//        phoneField = new JTextField();
//        managerNameField = new JTextField();
//        managerEmailField = new JTextField();
//
//        add(new JLabel("캠핑카대여회사 ID:"));
//        add(companyIdField);
//        add(new JLabel("회사명:"));
//        add(companyNameField);
//        add(new JLabel("주소:"));
//        add(addressField);
//        add(new JLabel("전화번호:"));
//        add(phoneField);
//        add(new JLabel("담당자 이름:"));
//        add(managerNameField);
//        add(new JLabel("담당자 이메일:"));
//        add(managerEmailField);
//
//        JButton saveBtn = new JButton("저장");
//        JButton cancelBtn = new JButton("취소");
//
//        add(saveBtn);
//        add(cancelBtn);
//
//        saveBtn.addActionListener(e -> insertCompany());
//        cancelBtn.addActionListener(e -> dispose());
//
//        setLocationRelativeTo(null);
//        setVisible(true);
//    }
//
//    private void insertCompany() {
//        String sql = "INSERT INTO `캠핑카대여회사` (`캠핑카대여회사ID`, `회사명`, `주소`, `전화번호`, `담당자이름`, `담당자이메일`) VALUES (?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, companyIdField.getText().trim());
//            pstmt.setString(2, companyNameField.getText().trim());
//            pstmt.setString(3, addressField.getText().trim());
//            pstmt.setString(4, phoneField.getText().trim());
//            pstmt.setString(5, managerNameField.getText().trim());
//            pstmt.setString(6, managerEmailField.getText().trim());
//
//            int result = pstmt.executeUpdate();
//            if (result > 0) {
//                JOptionPane.showMessageDialog(this, "등록 성공!");
//                dispose(); // 창 닫기
//            } else {
//                JOptionPane.showMessageDialog(this, "등록 실패", "에러", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//}



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
        StringBuilder sql = new StringBuilder("INSERT INTO `` (");
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

        sql.insert(13, table + "` (");
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
