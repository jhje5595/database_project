package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;


public class AdminMenu extends JFrame {
    private Connection conn;
    
    public AdminMenu(Connection conn) {
        this.conn = conn;
        
        setTitle("캠핑카 예약 시스템 - 관리자 메뉴");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));
        
        JButton initDbBtn = new JButton("데이터베이스 초기화");
        JButton insertBtn = new JButton("테이블 삽입");
        JButton deleteBtn = new JButton("테이블 삭제");
        JButton updateBtn = new JButton("테이블 변경");
        JButton viewAllBtn = new JButton("전체 테이블 보기");
        JButton viewRepairBtn = new JButton("캠핑카 정비 내역 보기");
        JButton sqlQueryBtn = new JButton("임의 SQL 질의 실행");

        add(initDbBtn);
        add(insertBtn);
        add(deleteBtn);
        add(updateBtn);
        add(viewAllBtn);
        add(viewRepairBtn);
        add(sqlQueryBtn);
        
        initDbBtn.addActionListener(e -> initializeDatabase());
        insertBtn.addActionListener(e -> insertData());
        deleteBtn.addActionListener(e -> deleteData());
        updateBtn.addActionListener(e -> updateData());
        viewAllBtn.addActionListener(e -> viewAllTables());
        viewRepairBtn.addActionListener(e -> viewRepairInfo());
        sqlQueryBtn.addActionListener(e -> executeCustomSQL());
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeDatabase() {
        AdminDatabaseInitializer initializer = new AdminDatabaseInitializer(conn);
        initializer.initialize();
    }
    
    private void insertData() {
        new AdminUniversalInsert(conn);
    }
    
    private void deleteData() {
        // 삭제조건 입력받고 삭제
        new AdminDeleteRecord(conn);
    }
    
    private void updateData() {
        // 변경할 테이블/조건 입력받고 변경
        new AdminUpdateRecord(conn);
    }
    
    private void viewAllTables() {
        // 전체 테이블 조회
        new AdminViewAllTables(conn);
    }
    
    private void viewRepairInfo() {
        new AdminRepairHistoryList(conn);
    }
    
    private void executeCustomSQL() {
        new AdminSqlQueryExecutor(conn);
    }
}