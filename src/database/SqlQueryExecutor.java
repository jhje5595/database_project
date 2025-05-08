package database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SqlQueryExecutor extends JFrame {
    private Connection conn;
    private JTextArea queryArea;
    private JTable resultTable;

    public SqlQueryExecutor(Connection conn) {
        this.conn = conn;

        setTitle("임의 SQL 질의 실행");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        queryArea = new JTextArea(5, 50);
        JButton executeBtn = new JButton("질의 실행");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
        topPanel.add(executeBtn, BorderLayout.EAST);

        resultTable = new JTable();

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        executeBtn.addActionListener(e -> executeQuery());

        setVisible(true);
    }

    private void executeQuery() {
        String sql = queryArea.getText().trim();

        if (!sql.toLowerCase().startsWith("select")) {
            JOptionPane.showMessageDialog(this, "오직 SELECT 문만 실행할 수 있습니다.", "에러", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            DefaultTableModel model = new DefaultTableModel();

            // 컬럼 이름 추가
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            // 데이터 추가
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            resultTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "SQL 실행 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}