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
        setLayout(new GridLayout(3,3));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton viewCampersBtn = new JButton("캠핑카 조회");
        JButton viewAvailableBtn = new JButton("대여 가능일자 조회");
        JButton rentCamperBtn = new JButton("캠핑카 대여 등록");
        JButton viewMyRentalsBtn = new JButton("내 대여정보 조회");
        JButton cancelRentalBtn = new JButton("대여 취소");
        JButton changeCamperBtn = new JButton("대여 캠핑카 변경");
        JButton changeScheduleBtn = new JButton("대여 일정 변경");
        JButton requestRepairBtn = new JButton("외부정비소 정비 의뢰");

        add(viewCampersBtn);
//        add(viewAvailableBtn);
//        add(rentCamperBtn);
        add(viewMyRentalsBtn);
        add(cancelRentalBtn);
        add(changeCamperBtn);
        add(changeScheduleBtn);
        add(requestRepairBtn);

        viewCampersBtn.addActionListener(e -> viewCampers());
//        viewAvailableBtn.addActionListener(e -> viewAvailableDates());
//        rentCamperBtn.addActionListener(e -> rentCamper());
        viewMyRentalsBtn.addActionListener(e -> viewMyRentals());
        cancelRentalBtn.addActionListener(e -> cancelRental());
        changeCamperBtn.addActionListener(e -> changeRentedCamper());
        changeScheduleBtn.addActionListener(e -> changeRentalSchedule());
        requestRepairBtn.addActionListener(e -> requestExternalRepair());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void viewCampers() {
    	new UserCampingCarList(conn,userId);
    }

//    private void viewAvailableDates() {
//    	new UserCanRentDay(conn);
//    }
//
//    private void rentCamper() {
//    	new UserRentCampingCar(conn,userId);
//    }

    private void viewMyRentals() {
        new UserRentalInfo(conn,userId);
    }

    private void cancelRental() {
        new UserRentalCancel(conn,userId);
    }

    private void changeRentedCamper() {
        new UserChangeCampingCar(conn,userId);
    }

    private void changeRentalSchedule() {
        new UserChangeSchedule(conn,userId);
    }

    private void requestExternalRepair() {
        new UserRequestRepair(conn,userId);
    }
}
