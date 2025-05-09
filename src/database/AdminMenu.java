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
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1, 10, 10));
        
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
        
        setVisible(true);
    }
    
    private void initializeDatabase() {
        JOptionPane.showMessageDialog(this, "데이터베이스 초기화 기능 실행 예정");
        DatabaseInitializer initializer = new DatabaseInitializer(conn);
        initializer.initialize();
    }
    
    private void insertData() {
        JOptionPane.showMessageDialog(this, "삽입 기능 선택");
        // 테이블 선택해서 삽입화면으로
        System.out.println("conn null? " + (conn == null));
        new CompanyRegistration(conn);
    }
    
    private void deleteData() {
        JOptionPane.showMessageDialog(this, "삭제 기능 선택");
        // 삭제조건 입력받고 삭제
        new DeleteRecord(conn);
    }
    
    private void updateData() {
        JOptionPane.showMessageDialog(this, "변경 기능 선택");
        // 변경할 테이블/조건 입력받고 변경
        new UpdateRecord(conn);
        
    }
    
    private void viewAllTables() {
        JOptionPane.showMessageDialog(this, "전체 테이블 보기 선택");
        // 전체 테이블 조회
        new ViewAllTables(conn);
    }
    
    private void viewRepairInfo() {
        JOptionPane.showMessageDialog(this, "캠핑카 정비 내역 조회 선택");
        // 캠핑카 선택 후 정비 내역 조회
    }
    
    private void executeCustomSQL() {
        JOptionPane.showMessageDialog(this, "임의 SQL 실행 기능 선택");
        // 직접 SQL 입력받아 실행
    }
}