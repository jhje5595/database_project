//package database;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.*;
//
//public class UserCanRentDay extends JFrame {
//    private JTable table;
//    private DefaultTableModel model;
//
//    public UserCanRentDay(Connection conn) {
//        setTitle("대여 불가능한 날짜 조회");
//        setSize(600, 300);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        String camperId = JOptionPane.showInputDialog(this, "캠핑카 등록 ID를 입력하세요:");
//        if (camperId == null || camperId.trim().isEmpty()) return;
//
//        model = new DefaultTableModel(new String[]{"대여시작일", "대여기간", "반납일"}, 0);
//        table = new JTable(model);
//        add(new JScrollPane(table), BorderLayout.CENTER);
//
//        loadUnavailableDates(conn, camperId);
//        setLocationRelativeTo(null);
//        setVisible(true);
//    }
//
//    private void loadUnavailableDates(Connection conn, String camperId) {
//        String sql = "SELECT 대여시작일, 대여기간, DATE_ADD(대여시작일, INTERVAL 대여기간 DAY) AS 반납일 " +
//                     "FROM 캠핑카대여 WHERE 캠핑카등록ID = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, camperId);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                model.addRow(new Object[]{
//                        rs.getDate("대여시작일"),
//                        rs.getInt("대여기간"),
//                        rs.getDate("반납일")
//                });
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "조회 실패: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}