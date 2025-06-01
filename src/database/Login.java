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

        adminLoginBtn.addActionListener(e -> adminLogin()); // 관리자 로그인 클릭시 adminLogin()호출
        userLoginBtn.addActionListener(e -> userLogin()); // 회원 로그인 클릭시 userLogin()호출

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void adminLogin() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 예제 소스파일에 있는 DB연동 코드
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DBTEST", "root", "1234"
            ); //관리자 계정 root/1234
            JOptionPane.showMessageDialog(this, "관리자 로그인 성공");
            new AdminMenu(conn); // 관리자 메뉴 화면 띄우기
            dispose(); // 로그인 창 닫기
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "관리자 로그인 실패 \nDBTEST 데이터베이스가 존재하는지 확인해주세요. 만약 존재하지 않다면 DBTEST 데이터베이스를 생성한 뒤 다시 시도해주세요"
            		+ "\ncreate database DBTEST; 한 문장만 실행해도 관리자 로그인이 가능합니다.(DBTEST 내에 테이블이 없어도 로그인 가능합니다. 데이터 베이스 자체가 없어서 오류가 뜰 가능성이 높습니다.)"
            		+ "\n 테이블 생성 및 초기화는 관리자 로그인 후 \"초기화\"버튼을 누르거나 외부 sql 초기화 스크립트로 가능합니다.", "에러", JOptionPane.ERROR_MESSAGE);
            System.out.println("관리가 로그인 자체가 DBTEST라는 데이터베이스가 존재해야합니다. 껍데기만 있는 DBTEST여도 괜찮습니다. (교수님이 올려주신 DB접속 코드 그대로 사용했습니다. 그 예제파일에서 사용한 데이터베이스 이름이 DBTEST여서 그대로 사용했습니다.)");
            System.out.println("혹시 이 메세지가 뜬다면 mySQL workbench에서 create database DBTEST; 한 번 해주시면 감사하겠습니다.");
        }
    }

    private void userLogin() {
        UserLoginDialog dialog = new UserLoginDialog(this);
        dialog.setVisible(true);

        if (dialog.isLoginSuccess()) {
            Connection conn = dialog.getConnection();
            String userId = dialog.getUserId();
            new UserMenu(conn, userId); // 회원 전용 메뉴로 이동
            dispose(); // 로그인 창 닫기
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}
