package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CompanyRegistration extends JFrame {
    private Connection conn;
    private JTextField companyIdField, companyNameField, addressField, phoneField, managerNameField, managerEmailField;

    public CompanyRegistration(Connection conn) {
        this.conn = conn;

        setTitle("캠핑카 대여회사 등록");
        setSize(400, 400);
        setLayout(new GridLayout(7, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫아도 메인 프로그램은 살아있음

        companyIdField = new JTextField();
        companyNameField = new JTextField();
        addressField = new JTextField();
        phoneField = new JTextField();
        managerNameField = new JTextField();
        managerEmailField = new JTextField();

        add(new JLabel("캠핑카대여회사 ID:"));
        add(companyIdField);
        add(new JLabel("회사명:"));
        add(companyNameField);
        add(new JLabel("주소:"));
        add(addressField);
        add(new JLabel("전화번호:"));
        add(phoneField);
        add(new JLabel("담당자 이름:"));
        add(managerNameField);
        add(new JLabel("담당자 이메일:"));
        add(managerEmailField);

        JButton saveBtn = new JButton("저장");
        JButton cancelBtn = new JButton("취소");

        add(saveBtn);
        add(cancelBtn);

        saveBtn.addActionListener(e -> insertCompany());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void insertCompany() {
        String sql = "INSERT INTO `캠핑카대여회사` (`캠핑카대여회사ID`, `회사명`, `주소`, `전화번호`, `담당자이름`, `담당자이메일`) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, companyIdField.getText().trim());
            pstmt.setString(2, companyNameField.getText().trim());
            pstmt.setString(3, addressField.getText().trim());
            pstmt.setString(4, phoneField.getText().trim());
            pstmt.setString(5, managerNameField.getText().trim());
            pstmt.setString(6, managerEmailField.getText().trim());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "등록 성공!");
                dispose(); // 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "등록 실패", "에러", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }
}