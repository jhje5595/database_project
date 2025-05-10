package database;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class UpdateRecord extends JFrame {
    private Connection conn;
    private JComboBox<String> tableComboBox;
    private JTextField setField, conditionField;

    public UpdateRecord(Connection conn) {
        this.conn = conn;

        setTitle("조건 기반 변경");
        setSize(500, 250);
        setLayout(new GridLayout(5, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableComboBox = new JComboBox<>(new String[]{
            "캠핑카대여회사", "캠핑카", "고객", "캠핑카대여", "부품재고", 
            "자체정비기록", "외부캠핑카정비소", "외부정비정보", "직원"
        });
        setField = new JTextField();
        conditionField = new JTextField();

        add(new JLabel("테이블 선택:"));
        add(tableComboBox);
        add(new JLabel("SET 구문 (예: 주소='서울')"));
        add(setField);
        add(new JLabel("WHERE 조건 (예: 캠핑카대여회사ID='C001')"));
        add(conditionField);

        JButton updateBtn = new JButton("변경");
        JButton cancelBtn = new JButton("취소");

        add(updateBtn);
        add(cancelBtn);

        updateBtn.addActionListener(e -> updateRecord());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void updateRecord() {
        String table = (String) tableComboBox.getSelectedItem();
        String setClause = setField.getText().trim();
        String condition = conditionField.getText().trim();

        if (setClause.isEmpty() || condition.isEmpty()) {
            JOptionPane.showMessageDialog(this, "SET 구문과 WHERE 조건을 모두 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE `" + table + "` SET " + setClause + " WHERE " + condition;

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "변경 성공!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "조건에 해당하는 데이터 없음", "정보", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }
}
