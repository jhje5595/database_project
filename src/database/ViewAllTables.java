package database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewAllTables extends JFrame {
    private Connection conn;
    private JComboBox<String> tableComboBox;
    private JTextArea resultArea;

    public ViewAllTables(Connection conn) {
        this.conn = conn;
        initGUI();
    }

    private void initGUI() {
        setTitle("전체 테이블 보기");
        setSize(600, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableComboBox = new JComboBox<>(new String[]{
            "캠핑카대여회사", "캠핑카", "고객", "캠핑카대여", "부품재고",
            "자체정비기록", "외부캠핑카정비소", "외부정비정보", "직원"
        });

        JButton viewBtn = new JButton("조회");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("테이블 선택:"));
        topPanel.add(tableComboBox);
        topPanel.add(viewBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        viewBtn.addActionListener(e -> showTableData());

        setVisible(true);
    }

    private void showTableData() {
        String table = (String) tableComboBox.getSelectedItem();
        String sql = "SELECT * FROM `" + table + "`";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            StringBuilder sb = new StringBuilder();

            // 컬럼 이름 출력
            for (int i = 1; i <= columnCount; i++) {
                sb.append(meta.getColumnName(i)).append("\t");
            }
            sb.append("\n");

            // 구분선
            for (int i = 1; i <= columnCount; i++) {
                sb.append("--------\t");
            }
            sb.append("\n");

            // 데이터 출력
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    sb.append(rs.getString(i)).append("\t");
                }
                sb.append("\n");
            }

            resultArea.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }
}
