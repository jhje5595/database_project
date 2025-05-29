package database;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class AdminDeleteRecord extends JFrame {
    private Connection conn;
    private JComboBox<String> tableComboBox;
    private JTextField conditionField;

    public AdminDeleteRecord(Connection conn) {
        this.conn = conn;

        setTitle("조건 기반 삭제");
        setSize(500, 200);
        setLayout(new GridLayout(3, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 메인 프로그램은 종료 안 됨

        // 콤보박스 값 고정 (DB에서 불러오지 않음)
        tableComboBox = new JComboBox<>(new String[]{
            "캠핑카대여회사", "캠핑카", "고객", "캠핑카대여", "부품재고",
            "자체정비기록", "외부캠핑카정비소", "외부정비정보", "직원"
        });

        conditionField = new JTextField();

        add(new JLabel("테이블 선택:"));
        add(tableComboBox);
        add(new JLabel("WHERE 조건 (예: 캠핑카대여회사ID='C001')"));
        add(conditionField);

        JButton deleteBtn = new JButton("삭제");
        JButton cancelBtn = new JButton("취소");

        add(deleteBtn);
        add(cancelBtn);

        // 버튼 이벤트: DB 작업
        deleteBtn.addActionListener(e -> deleteRecord());
        cancelBtn.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void deleteRecord() {
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "DB 연결이 필요합니다.", "에러", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String table = (String) tableComboBox.getSelectedItem();
        String condition = conditionField.getText().trim();

        if (condition.isEmpty()) {
            JOptionPane.showMessageDialog(this, "WHERE 조건을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM `" + table + "` WHERE " + condition;

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, result + "개의 행이 삭제되었습니다.");
                dispose(); // 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "조건에 맞는 데이터가 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }
}
