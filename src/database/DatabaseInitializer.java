package database;

import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    private Connection conn;

    public DatabaseInitializer(Connection conn) {
        this.conn = conn;
    }

    public void initialize() {
        String basePath = System.getProperty("user.dir"); // 프로젝트 루트
        File sqlFile = new File(basePath + "/resource/camping-ini.sql");

        if (!sqlFile.exists()) {
            JOptionPane.showMessageDialog(null, "초기화 파일이 없습니다: " + sqlFile.getAbsolutePath(), "에러", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            executeSqlFile(sqlFile);
            JOptionPane.showMessageDialog(null, "데이터베이스 초기화 완료!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "초기화 실패: " + e.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeSqlFile(File sqlFile) throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new FileReader(sqlFile));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
            if (line.trim().endsWith(";")) {
                executeSingleSql(sb.toString());
                sb.setLength(0); // 명령어 초기화
            } else {
                sb.append("\n");
            }
        }
        br.close();
    }

    private void executeSingleSql(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();
    }
}
