package database;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminViewAllTables extends JFrame {
    private Connection conn;
    private JPanel contentPanel;
    private JScrollPane scrollPane;

    public AdminViewAllTables(Connection conn) {
        this.conn = conn;
        setTitle("전체 테이블 보기");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 보여줄 테이블 목록
        String[] tables = {
            "캠핑카대여회사", "캠핑카", "고객", "캠핑카대여", "부품재고",
            "자체정비기록", "외부캠핑카정비소", "외부정비정보", "직원"
        };

        for (String table : tables) {
            try {
                JTable jTable = createTableFromQuery("SELECT * FROM `" + table + "`");
                jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                jTable.setFillsViewportHeight(true);
                jTable.setRowHeight(15);

                JLabel label = new JLabel("===== " + table + " =====");
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                contentPanel.add(label);
                contentPanel.add(new JScrollPane(jTable));
                contentPanel.add(Box.createVerticalStrut(20));  // 간격
            } catch (SQLException e) {
                JLabel errorLabel = new JLabel("[ERROR] 테이블 " + table + ": " + e.getMessage());
                contentPanel.add(errorLabel);
            }
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTable createTableFromQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        // 컬럼명 배열
        String[] columnNames = new String[colCount];
        for (int i = 1; i <= colCount; i++) {
            columnNames[i - 1] = meta.getColumnName(i);
        }

        // 데이터 추가
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        while (rs.next()) {
            Object[] rowData = new Object[colCount];
            for (int i = 1; i <= colCount; i++) {
                rowData[i - 1] = rs.getString(i);
            }
            model.addRow(rowData);
        }

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true); // 정렬 기능 추가
        return table;
    }
}
