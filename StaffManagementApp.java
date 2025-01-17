import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StaffManagementApp {
    private JFrame frame;
    private JTextField firstNameField, lastNameField, nationalCodeField, jobTitleField, salaryField;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchNationalCodeField; // فیلد جستجو برای کد ملی

    public StaffManagementApp(JFrame parentFrame) {
        frame = new JFrame("مدیریت کارکنان");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        frame.setSize(800, 600);

        JPanel inputPanel = new JPanel();
        GroupLayout layout = new GroupLayout(inputPanel);
        inputPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // ایجاد و تنظیم فیلدها و برچسب‌ها
        JLabel firstNameLabel = new JLabel("نام:");
        firstNameField = new JTextField(15);

        JLabel lastNameLabel = new JLabel("نام خانوادگی:");
        lastNameField = new JTextField(15);

        JLabel nationalCodeLabel = new JLabel("کد ملی:");
        nationalCodeField = new JTextField(15);

        JLabel jobTitleLabel = new JLabel("عنوان شغلی:");
        jobTitleField = new JTextField(15);

        JLabel salaryLabel = new JLabel("حقوق:");
        salaryField = new JTextField(15);

        // دکمه‌های عملیات
        JButton addButton = new JButton("افزودن");
        JButton editButton = new JButton("ویرایش");
        JButton deleteButton = new JButton("حذف");

        // دکمه بازگشت به منوی قبلی
        JButton backButton = new JButton("بازگشت");

        // جستجو بر اساس کد ملی
        JLabel searchLabel = new JLabel("جستجو بر اساس کد ملی:");
        searchNationalCodeField = new JTextField(15);
        JButton searchButton = new JButton("جستجو");

        // تنظیم چیدمان فیلدها و دکمه‌ها
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(firstNameLabel)
                                .addComponent(lastNameLabel)
                                .addComponent(nationalCodeLabel)
                                .addComponent(jobTitleLabel)
                                .addComponent(salaryLabel)
                                .addComponent(searchLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(firstNameField)
                                .addComponent(lastNameField)
                                .addComponent(nationalCodeField)
                                .addComponent(jobTitleField)
                                .addComponent(salaryField)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(addButton)
                                        .addComponent(editButton)
                                        .addComponent(deleteButton))
                                .addComponent(searchNationalCodeField)
                                .addComponent(searchButton)
                                .addComponent(backButton) // اضافه کردن دکمه بازگشت
                        ));

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(firstNameLabel)
                                .addComponent(firstNameField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lastNameLabel)
                                .addComponent(lastNameField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(nationalCodeLabel)
                                .addComponent(nationalCodeField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jobTitleLabel)
                                .addComponent(jobTitleField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(salaryLabel)
                                .addComponent(salaryField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(addButton)
                                .addComponent(editButton)
                                .addComponent(deleteButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(searchLabel)
                                .addComponent(searchNationalCodeField))
                        .addComponent(searchButton)
                        .addComponent(backButton) // دکمه بازگشت
        );

        frame.add(inputPanel, BorderLayout.NORTH);

        // جدول نمایش اطلاعات کارکنان
        tableModel = new DefaultTableModel(new Object[] { "نام", "نام خانوادگی", "کد ملی", "عنوان شغلی", "حقوق" }, 0);
        staffTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(staffTable);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // بارگذاری اطلاعات اولیه جدول
        loadStaffData();

        // افزودن اکشن برای دکمه‌ها
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStaff(); 
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editStaff();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStaff(); 
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchStaffByNationalCode();
            }
        });

        // عملگر دکمه بازگشت
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                parentFrame.setVisible(true); 
            }
        });

        frame.setVisible(true);
    }

    // متد افزودن کارمند جدید
    private void addStaff() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String nationalCode = nationalCodeField.getText();
        String jobTitle = jobTitleField.getText();
        String salaryText = salaryField.getText();

        try {
            double salary = Double.parseDouble(salaryText);
            if (DatabaseOperations.addStaff(firstName, lastName, nationalCode, jobTitle, salary)) {
                JOptionPane.showMessageDialog(frame, "کارمند با موفقیت اضافه شد.");
                clearFields();
                loadStaffData(); 
            } else {
                JOptionPane.showMessageDialog(frame, "خطا در افزودن کارمند.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "لطفاً یک مقدار معتبر برای حقوق وارد کنید.");
        }
    }

    
    private void editStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow != -1) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String nationalCode = nationalCodeField.getText();
            String jobTitle = jobTitleField.getText();
            String salaryText = salaryField.getText();

            try {
                double salary = Double.parseDouble(salaryText);
                if (DatabaseOperations.updateStaff(firstName, lastName, nationalCode, jobTitle, salary)) {
                    JOptionPane.showMessageDialog(frame, "کارمند با موفقیت ویرایش شد.");
                    clearFields();
                    loadStaffData(); 
                } else {
                    JOptionPane.showMessageDialog(frame, "خطا در ویرایش کارمند.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "برای ویرایش لظفا روی قسمت مربوطه دوبار پشت سر هم کلیک کنید.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "لطفاً یک ردیف برای ویرایش انتخاب کنید.");
        }
    }

    
    private void deleteStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow != -1) {
            String nationalCode = (String) staffTable.getValueAt(selectedRow, 2);
            if (DatabaseOperations.deleteStaff(nationalCode)) {
                JOptionPane.showMessageDialog(frame, "کارمند با موفقیت حذف شد.");
                loadStaffData(); 
            } else {
                JOptionPane.showMessageDialog(frame, "خطا در حذف کارمند.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "لطفاً یک ردیف برای حذف انتخاب کنید.");
        }
    }

    // متد جستجو کارمند بر اساس کد ملی
    private void searchStaffByNationalCode() {
        String nationalCode = searchNationalCodeField.getText();
        if (nationalCode.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "لطفاً کد ملی را وارد کنید.");
            return;
        }
        Object[] staffData = DatabaseOperations.getStaffByNationalCode(nationalCode);
        if (staffData != null) {
            firstNameField.setText((String) staffData[0]);
            lastNameField.setText((String) staffData[1]);
            nationalCodeField.setText((String) staffData[2]);
            jobTitleField.setText((String) staffData[3]);
            salaryField.setText(String.valueOf(staffData[4]));
        } else {
            JOptionPane.showMessageDialog(frame, "کارمند با این کد ملی یافت نشد.");
        }
    }

    
    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        nationalCodeField.setText("");
        jobTitleField.setText("");
        salaryField.setText("");
    }

    // متد بارگذاری داده‌ها در جدول
    private void loadStaffData() {
        List<Object[]> staffList = DatabaseOperations.getAllStaff();
        tableModel.setRowCount(0); 
        for (Object[] staff : staffList) {
            tableModel.addRow(staff); 
        }
    }
}
