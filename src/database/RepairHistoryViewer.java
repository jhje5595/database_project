package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RepairHistoryViewer extends JFrame {
    private Connection conn;
    private JTextField camperIdField;
    private JTable selfRepairTable, externalRepairTable;

    public RepairHistoryViewer(Connection conn) {
        this.conn = conn;

        setTitle("캠핑카 정비 내역 보기");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel();
        camperIdField = new JTextField(20);
        JButton searchBtn = new JButton("정비 내역 조회");

        topPanel.add(new JLabel("캠핑카등록ID:"));
        topPanel.add(camperIdField);
        topPanel.add(searchBtn);

        selfRepairTable = new JTable();
        externalRepairTable = new JTable();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("자체 정비 내역", new JScrollPane(selfRepairTable));
        tabbedPane.addTab("외부 정비 내역", new JScrollPane(externalRepairTable));

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchRepairHistory());

        setVisible(true);
    }

    private void searchRepairHistory() {
        String camperId = camperIdField.getText().trim();

        if (camperId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "캠핑카등록ID를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchSelfRepair(camperId);
        searchExternalRepair(camperId);
    }

    private void searchSelfRepair(String camperId) {
        String sql = "SELECT 자체정비등록ID, 부품등록ID, 정비일자, 정비시간, 정비담당자ID " +
                     "FROM 자체정비기록 " +
                     "WHERE 캠핑카등록ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, camperId);
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                new String[] {"자체정비등록ID", "부품등록ID", "정비일자", "정비시간", "정비담당자ID"}, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("자체정비등록ID"),
                    rs.getString("부품등록ID"),
                    rs.getDate("정비일자"),
                    rs.getInt("정비시간"),
                    rs.getString("정비담당자ID")
                });
            }

            selfRepairTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "자체 정비 조회 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchExternalRepair(String camperId) {
        String sql = "SELECT 정비번호, 캠핑카대여회사ID, 캠핑카정비소ID, 고객운전면허증번호, 정비내역, 수리날짜, 수리비용 " +
                     "FROM 외부정비정보 " +
                     "WHERE 캠핑카등록ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, camperId);
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                new String[] {"정비번호", "캠핑카대여회사ID", "캠핑카정비소ID", "고객운전면허증번호", "정비내역", "수리날짜", "수리비용"}, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("정비번호"),
                    rs.getString("캠핑카대여회사ID"),
                    rs.getString("캠핑카정비소ID"),
                    rs.getString("고객운전면허증번호"),
                    rs.getString("정비내역"),
                    rs.getDate("수리날짜"),
                    rs.getInt("수리비용")
                });
            }

            externalRepairTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "외부 정비 조회 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
