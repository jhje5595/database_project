package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserMenu extends JFrame {
    private Connection conn;
    private String userId; // 로그인한 고객 ID

    public UserMenu(Connection conn, String userId) {
        this.conn = conn;
        this.userId = userId;

        setTitle("회원 메뉴 - " + userId);
        setSize(400, 500);
        setLayout(new GridLayout(3,1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton viewCampersBtn = new JButton("캠핑카 조회");
        JButton viewMyRentalsBtn = new JButton("내 대여정보 조회");
        JButton requestRepairBtn = new JButton("외부정비소 정비 의뢰");

        add(viewCampersBtn);
//        add(viewAvailableBtn);
//        add(rentCamperBtn);
        add(viewMyRentalsBtn);
        add(requestRepairBtn);

        viewCampersBtn.addActionListener(e -> viewCampers());
        viewMyRentalsBtn.addActionListener(e -> viewMyRentals());
        requestRepairBtn.addActionListener(e -> requestExternalRepair());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void viewCampers() {
    	new UserCampingCarList(conn,userId);
    }

    private void viewMyRentals() {
        new UserRentalInfo(conn,userId);
    }

    private void requestExternalRepair() {
        new UserRequestRepair(conn,userId);
    }
}
