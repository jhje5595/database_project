package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JButton adminLoginBtn, userLoginBtn;

    public Login() {
        setTitle("캠핑카 예약 시스템 - 로그인");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));

        adminLoginBtn = new JButton("관리자 로그인");
        userLoginBtn = new JButton("회원 로그인");

        add(adminLoginBtn);
        add(userLoginBtn);

        adminLoginBtn.addActionListener(e -> adminLogin());
        userLoginBtn.addActionListener(e -> userLogin());

        setVisible(true);
    }

    private void adminLogin() {
        try {
            // ★ Db1.java 스타일: 직접 getConnection 호출
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DBTEST", "root", "1234"
            );
            JOptionPane.showMessageDialog(this, "관리자 로그인 성공");
            new AdminMenu(conn); // 관리자 메뉴 화면 띄우기
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "관리자 로그인 실패", "에러", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void userLogin() {
        String userId = JOptionPane.showInputDialog(this, "회원 아이디 입력:");
        String userPw = JOptionPane.showInputDialog(this, "회원 비밀번호 입력:");

        try {
            // ★ Db1.java 스타일: 직접 getConnection 호출
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DBTEST", "root", "1234"
            );

            // 고객 로그인 확인
            String sql = "SELECT * FROM Customer WHERE CustomerID = ? AND Password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userPw);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "회원 로그인 성공");
                new UserMenu(conn, userId); // 회원 메뉴 화면 띄우기
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 잘못되었습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "회원 로그인 실패", "에러", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}
