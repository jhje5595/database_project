package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserLoginDialog extends JDialog { // DB에 저장되어 있는 유저 ID와 비밀번호 기반으로 유저 로그인 진행
    private JTextField idField;
    private JPasswordField pwField;
    private JButton loginBtn, cancelBtn;
    private Connection conn;
    private boolean loginSuccess = false;
    private String userId;

    public UserLoginDialog(JFrame parent) {
        super(parent, "회원 로그인", true);
        setSize(300, 200);
        setLayout(new GridLayout(3, 2, 10, 10));
        setLocationRelativeTo(parent);

        idField = new JTextField();
        pwField = new JPasswordField();

        loginBtn = new JButton("로그인");
        cancelBtn = new JButton("취소");

        add(new JLabel("아이디:"));
        add(idField);
        add(new JLabel("비밀번호:"));
        add(pwField);
        add(loginBtn);
        add(cancelBtn);

        loginBtn.addActionListener(e -> login());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void login() {
        String userIdInput = idField.getText().trim();
        String userPwInput = new String(pwField.getPassword()).trim();

        if (userIdInput.isEmpty() || userPwInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DBTEST", "user1", "user1"
            );

            String sql = "SELECT * FROM 고객 WHERE 고객ID = ? AND 비밀번호 = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userIdInput);
            pstmt.setString(2, userPwInput);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loginSuccess = true;
                userId = userIdInput;
                JOptionPane.showMessageDialog(this, "회원 로그인 성공");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB 연결 오류: " + ex.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public String getUserId() {
        return userId;
    }

    public Connection getConnection() {
        return conn;
    }
}
