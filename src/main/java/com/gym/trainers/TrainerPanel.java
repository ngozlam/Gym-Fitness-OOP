package com.gym.trainers;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import com.gym.members.MemberDao;
import com.gym.members.Member;

@Component
public class TrainerPanel extends JPanel{
    private final TrainerService trainerService;
    private final MemberDao memberDao;
    
    //phan thong tin cua trainer
    private final JTextField txtName = new JTextField(20);
    private final JTextField txtSpecialty = new JTextField(20);
    private final JTextField txtPhone = new JTextField();

    // phan nut chuc nang
    private final JButton btnAdd = new JButton("Thêm");
    private final JButton btnUpdate = new JButton("Cập Nhật");
    private final JButton btnDelete = new JButton("Xoá");
    private final JButton btnClear = new JButton("Làm mới");

    //Bảng
    // Model của JTable 
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "ID", "Họ tên", "Chuyên môn", "SĐT" }, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false; // không cho sửa trực tiếp trên bảng
        }
    };    
    private final JTable table = new JTable(tableModel);
    private Long selectedId = null; // lưu id của trainer đang được chọn trong bảng
    

    // Phần gán
    private final JComboBox<Member> cboMember = new JComboBox<>();
    private final JComboBox<Trainer> cboTrainer = new JComboBox<>();
    private final JButton btnAssign = new JButton("Gán");
    private final JLabel lblCurrent = new JLabel(" ");
    private final JButton btnReload = new JButton("Tải lại");

    // Mục "không gán" trong combobox trainer (id = null)
    private static final Trainer NONE = new Trainer("(Không gán)", null, null);

    // chặn listener chạy khi đang nạp lại combobox
    private boolean loading = false;

    public TrainerPanel(TrainerService trainerService, MemberDao memberDao) {
        this.trainerService = trainerService;
        this.memberDao = memberDao;
        buildUI();
        initListeners();
        loadTable();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Form
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Họ tên:"));
        form.add(txtName);
        form.add(new JLabel("Chuyên môn:"));
        form.add(txtSpecialty);
        form.add(new JLabel("SĐT:"));
        form.add(txtPhone);

        //Nút
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        //Phần Thao Tác 
        JPanel action = new JPanel(new BorderLayout(5,5));
        action.add(form,BorderLayout.CENTER);
        action.add(buttons,BorderLayout.SOUTH);
        
        //Bảng
        JScrollPane scorllPane = new JScrollPane(table);

        //trong table chỉ được chọn 1 dòng 
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // làm đẹp combobox cho member và trainer 
        cboMember.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof Member) {
                    Member m = (Member) value;
                    setText(m.getId() + " - " + m.getFullName());
                }
                return this;
            }
        });
        cboTrainer.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof Trainer) {
                    setText(((Trainer) value).getFullName());
                }
                return this;
            }
        });
        //đặt chiều dài hiện thị 2 combobox 
        cboMember.setPrototypeDisplayValue(memberPrototype());
        cboTrainer.setPrototypeDisplayValue(NONE);
    

        JPanel assign = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assign.setBorder(BorderFactory.createTitledBorder("Gán trainer cho member"));
        assign.add(new JLabel("Member:"));
        assign.add(cboMember);
        assign.add(new JLabel("Trainer:"));
        assign.add(cboTrainer);
        assign.add(btnAssign);
        assign.add(btnReload);
        assign.add(lblCurrent);
        

        
        add(action,BorderLayout.NORTH);
        add(scorllPane,BorderLayout.CENTER);
        add(assign, BorderLayout.SOUTH);
    }



    //Nạp dữ liệu 
    private void loadTable() {
        tableModel.setRowCount(0);
        List<Trainer> list = trainerService.findAll();
        for (Trainer t : list) {
            tableModel.addRow(new Object[] {
                    t.getId(), t.getFullName(), t.getSpecialty(), t.getPhone() });
        }
        loadCombos(); // nạp lại combobox trainer mỗi lần chỉnh sửa
    }
    //Tạo 1 member mẫu để lấy chiều dài hiển thị cho combobox member
    private Member memberPrototype() {
        Member m = new Member();
        m.setId(0L);
        m.setFullName("ID Nguyen Van A ");
        return m;
    }

    //Listener 
    private void initListeners() {

        // Chọn dòng đổ lên form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            selectedId = (Long) tableModel.getValueAt(row, 0);
            txtName.setText((String) tableModel.getValueAt(row, 1));
            txtSpecialty.setText((String) tableModel.getValueAt(row, 2));
            txtPhone.setText((String) tableModel.getValueAt(row, 3));
        });

        // Thêm
        btnAdd.addActionListener(e -> {
            try {
                trainerService.save(readForm());
                loadTable();
                clearForm();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Sửa
        btnUpdate.addActionListener(e -> {
            if (selectedId == null) {
                JOptionPane.showMessageDialog(this, "Hãy chọn 1 trainer trong bảng.");
                return;
            }
            try {
                Trainer t = readForm();
                t.setId(selectedId);
                trainerService.update(t);
                loadTable();
                clearForm();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Xóa
        btnDelete.addActionListener(e -> {
            if (selectedId == null) {
                JOptionPane.showMessageDialog(this, "Hãy chọn 1 trainer trong bảng.");
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this,
                    "Xóa trainer này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            try {
                trainerService.delete(selectedId);
                loadTable();
                clearForm();
            }catch(DataIntegrityViolationException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa trainer này vì đang có member gán cho trainer này.");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Làm mới
        btnClear.addActionListener(e -> clearForm());

        //tim showCurrentTrainer
        cboMember.addActionListener(e -> {
        if (!loading) showCurrentTrainer();
        });

        // load lai combobox 
        btnReload.addActionListener(e -> {
            loadCombos();
            loadTable();
        });
         // Gán
        btnAssign.addActionListener(e -> {
            Member selected = (Member) cboMember.getSelectedItem();
            Trainer trainer = (Trainer) cboTrainer.getSelectedItem();
            if (selected == null || trainer == null) {
                JOptionPane.showMessageDialog(this, "Hãy chọn member và trainer.");
                return;
            }
            try {
                Member m = memberDao.findById(selected.getId()).orElseThrow(() -> new IllegalStateException("Không tìm thấy member hãy reload lại danh sách"));
                m.setTrainerId(trainer.getId());   // NONE có id = null -> bỏ gán
                memberDao.update(m);
                showCurrentTrainer();
                JOptionPane.showMessageDialog(this, "Đã lưu.");
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }
    // Nạp lại 2 combobox từ DB
    private void loadCombos() {
        loading = true;

        //tao model de quan lý du lieu

        DefaultComboBoxModel<Member> mm = new DefaultComboBoxModel<>();
        for (Member m : memberDao.findAll()) {
            mm.addElement(m);
        }
        cboMember.setModel(mm);

        DefaultComboBoxModel<Trainer> tm = new DefaultComboBoxModel<>();
        //them khong gan vao dau tien
        tm.addElement(NONE);
        for (Trainer t : trainerService.findAll()) {
            tm.addElement(t); // them một object vào cuối
        }
        cboTrainer.setModel(tm);

        loading = false; //nap xong combobox
        showCurrentTrainer();
    }

    // Đọc lại member từ DB, chọn sẵn trainer hiện tại của member đó
    private void showCurrentTrainer() {
        Member selected = (Member) cboMember.getSelectedItem();
        if (selected == null) {
            lblCurrent.setText(" ");
            return;
        }
        // tìm member từ DB , nếu không có thì dùng member đang chọn trong combobox
        Member m = memberDao.findById(selected.getId()).orElse(selected);

        Trainer current = NONE;
        for (int i = 0; i < cboTrainer.getItemCount(); i++) {
            Trainer t = cboTrainer.getItemAt(i);
            if (t.getId() != null && t.getId().equals(m.getTrainerId())) {
                current = t;
                break;
            }
        }
        loading = true;
        cboTrainer.setSelectedItem(current);
        loading = false;
        lblCurrent.setText("Hiện tại: " + current.getFullName());
    }
    
    
    // Tiện ích
    private Trainer readForm() {
        return new Trainer(txtName.getText().trim(),txtSpecialty.getText().trim(),txtPhone.getText().trim());
    }

    private void clearForm() {
        selectedId = null;
        txtName.setText("");
        txtSpecialty.setText("");
        txtPhone.setText("");
        table.clearSelection();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

}
