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
        UserLoginDialog dialog = new UserLoginDialog(this);
        dialog.setVisible(true);

        if (dialog.isLoginSuccess()) {
            Connection conn = dialog.getConnection();
            String userId = dialog.getUserId();
            new UserMenu(conn, userId); // 회원 전용 메뉴로 이동
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}
