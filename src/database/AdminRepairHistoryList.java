package database;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminRepairHistoryList extends JFrame {
    private Connection conn;
    private JList<String> camperList;
    private JTable selfRepairTable, externalRepairTable;
    private DefaultListModel<String> camperListModel;

    public AdminRepairHistoryList(Connection conn) {
        this.conn = conn;

        setTitle("캠핑카 정비 내역 보기");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 캠핑카 목록
        camperListModel = new DefaultListModel<>();
        camperList = new JList<>(camperListModel);
        loadCampers();
        camperList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        camperList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String camperId = camperList.getSelectedValue();
                if (camperId != null) {
                    loadSelfRepair(camperId);
                    loadExternalRepair(camperId);
                }
            }
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("캠핑카 목록"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(camperList), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // 테이블 생성
        selfRepairTable = new JTable();
        externalRepairTable = new JTable();

        // 자체/외부 정비 내역 각각 라벨 붙이기
        JPanel selfPanel = new JPanel(new BorderLayout());
        selfPanel.add(new JLabel("  자체 정비 내역"), BorderLayout.NORTH);
        selfPanel.add(new JScrollPane(selfRepairTable), BorderLayout.CENTER);

        JPanel externalPanel = new JPanel(new BorderLayout());
        externalPanel.add(new JLabel("  외부 정비 내역"), BorderLayout.NORTH);
        externalPanel.add(new JScrollPane(externalRepairTable), BorderLayout.CENTER);

        JPanel tablePanel = new JPanel(new GridLayout(2, 1));
        tablePanel.add(selfPanel);
        tablePanel.add(externalPanel);

        add(tablePanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadCampers() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 캠핑카등록ID FROM 캠핑카")) {
            camperListModel.clear();
            while (rs.next()) {
                camperListModel.addElement(rs.getString("캠핑카등록ID"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "캠핑카 불러오기 실패: " + e.getMessage());
        }
    }

    private void loadSelfRepair(String camperId) {
        String sql = "SELECT 자체정비등록ID, 부품등록ID, 정비일자, 정비시간, 정비담당자ID FROM 자체정비기록 WHERE 캠핑카등록ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, camperId);
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new Object[]{
                    "정비ID", "부품ID", "정비일자", "정비시간", "담당자ID", "조회"
            }, 0) {
                public boolean isCellEditable(int row, int column) {
                    return column == 5;
                }
            };
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1), rs.getString(2),
                        rs.getDate(3), rs.getInt(4), rs.getString(5), "조회"
                });
            }
            selfRepairTable.setModel(model);
            selfRepairTable.getColumn("조회").setCellRenderer(new ButtonRenderer());
            selfRepairTable.getColumn("조회").setCellEditor(new ButtonEditor(new JCheckBox(), true));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "자체정비 조회 실패: " + e.getMessage());
        }
    }

    private void loadExternalRepair(String camperId) {
        String sql = "SELECT 정비번호, 캠핑카대여회사ID, 캠핑카정비소ID, 고객운전면허증번호, 정비내역, 수리날짜, 수리비용 FROM 외부정비정보 WHERE 캠핑카등록ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, camperId);
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new Object[]{
                    "정비번호", "대여회사ID", "정비소ID", "고객면허", "정비내역", "수리날짜", "수리비용", "조회"
            }, 0) {
                public boolean isCellEditable(int row, int column) {
                    return column == 7;
                }
            };
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5),
                        rs.getDate(6), rs.getInt(7), "조회"
                });
            }
            externalRepairTable.setModel(model);
            externalRepairTable.getColumn("조회").setCellRenderer(new ButtonRenderer());
            externalRepairTable.getColumn("조회").setCellEditor(new ButtonEditor(new JCheckBox(), false));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "외부정비 조회 실패: " + e.getMessage());
        }
    }

    private void showPartDetailPopup(String partId) {
        String sql = "SELECT 부품명, 부품단가, 재고수량, 공급회사이름 FROM 부품재고 WHERE 부품등록ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, partId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JTextArea area = new JTextArea(String.format(
                        "부품명: %s\n단가: %d원\n재고: %d개\n공급사: %s",
                        rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4)
                ));
                area.setEditable(false);
                JDialog dialog = new JDialog(this, "부품 상세정보", true);
                dialog.add(new JScrollPane(area));
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "부품 상세 조회 오류: " + e.getMessage());
        }
    }

    private void showRepairShopDetailPopup(String shopId) {
        String sql = "SELECT 정비소명, 정비소주소, 정비소전화번호, 담당자이름, 담당자이메일 FROM 외부캠핑카정비소 WHERE 캠핑카정비소ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shopId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JTextArea area = new JTextArea(String.format(
                        "정비소명: %s\n주소: %s\n전화번호: %s\n담당자: %s\n이메일: %s",
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)
                ));
                area.setEditable(false);
                JDialog dialog = new JDialog(this, "정비소 상세정보", true);
                dialog.add(new JScrollPane(area));
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "정비소 상세 조회 오류: " + e.getMessage());
        }
    }

    // 버튼 렌더러
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("상세정보조회");
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // 버튼 에디터
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String id;
        private boolean isPart;

        public ButtonEditor(JCheckBox checkBox, boolean isPart) {
            super(checkBox);
            this.isPart = isPart;
            button = new JButton("상세정보조회");
            button.addActionListener(e -> {
                if (isPart) {
                    showPartDetailPopup(id);
                } else {
                    showRepairShopDetailPopup(id);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            id = (String) table.getValueAt(row, isPart ? 1 : 2);
            return button;
        }
    }
}
