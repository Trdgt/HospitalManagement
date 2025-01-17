import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PatientManagementApp {
    private JFrame frame;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField nationalCodeField;
    private JTextField diseaseField;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchNationalCodeField;
    private ReportGenerationApp reportGenerationApp; // مرجع به کلاس ReportGenerationApp

    public PatientManagementApp(JFrame parentFrame, ReportGenerationApp reportApp) {
        frame = new JFrame("مدیریت بیماران");
        this.reportGenerationApp = reportApp; // دریافت و ذخیره مرجع به ReportGenerationApp
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // پنل ورودی اطلاعات
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

        JLabel diseaseLabel = new JLabel("بیماری:");
        diseaseField = new JTextField(15);

        // دکمه‌های عملیات
        JButton addButton = new JButton("افزودن");
        JButton editButton = new JButton("ویرایش");
        JButton deleteButton = new JButton("حذف");

        // دکمه بازگشت
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
                                .addComponent(diseaseLabel)
                                .addComponent(searchLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(firstNameField)
                                .addComponent(lastNameField)
                                .addComponent(nationalCodeField)
                                .addComponent(diseaseField)
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
                                .addComponent(diseaseLabel)
                                .addComponent(diseaseField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(addButton)
                                .addComponent(editButton)
                                .addComponent(deleteButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(searchLabel)
                                .addComponent(searchNationalCodeField))
                        .addComponent(searchButton)
                        .addComponent(backButton) // اضافه کردن دکمه بازگشت
        );

        frame.add(inputPanel, BorderLayout.NORTH);

        // جدول نمایش اطلاعات بیماران
        tableModel = new DefaultTableModel(new Object[] { "نام", "نام خانوادگی", "کد ملی", "بیماری" }, 0);
        patientTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(patientTable);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // بارگذاری اطلاعات اولیه جدول
        loadPatientData();

        // افزودن اکشن برای دکمه‌ها
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPatient();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePatient();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPatientByNationalCode();
            }
        });

        // اکشن برای دکمه بازگشت
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // بستن پنجره فعلی
                parentFrame.setVisible(true); // نمایش مجدد پنجره اصلی
            }
        });

        frame.setVisible(true);
    }

    private void addPatient() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String nationalCode = nationalCodeField.getText();
        String disease = diseaseField.getText();

        if (DatabaseOperations.addPatient(firstName, lastName, nationalCode, disease)) {
            JOptionPane.showMessageDialog(frame, "بیمار با موفقیت اضافه شد.");
            clearFields();
            loadPatientData();
            // ارسال داده‌های جدید به ReportGenerationApp
            reportGenerationApp.addPatientToReport(new Patient(firstName, lastName, nationalCode, disease));
        } else {
            JOptionPane.showMessageDialog(frame, "خطا در افزودن بیمار.");
        }
    }

    private void editPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow != -1) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String nationalCode = nationalCodeField.getText();
            String disease = diseaseField.getText();

            if (DatabaseOperations.updatePatient(firstName, lastName, nationalCode, disease)) {
                JOptionPane.showMessageDialog(frame, "بیمار با موفقیت ویرایش شد.");
                clearFields();
                loadPatientData();
                // ارسال داده‌های جدید به ReportGenerationApp
                reportGenerationApp.addPatientToReport(new Patient(firstName, lastName, nationalCode, disease));
            } else {
                JOptionPane.showMessageDialog(frame, "برای ویرایش لظفا روی قسمت مربوطه دوبار پشت سر هم کلیک کنید.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "لطفاً یک ردیف برای ویرایش انتخاب کنید.");
        }
    }

    private void deletePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow != -1) {
            String nationalCode = (String) patientTable.getValueAt(selectedRow, 2);
            if (DatabaseOperations.deletePatient(nationalCode)) {
                JOptionPane.showMessageDialog(frame, "بیمار با موفقیت حذف شد.");
                loadPatientData();
                // ارسال داده‌های جدید به ReportGenerationApp
                reportGenerationApp.removePatientFromReport(nationalCode);
            } else {
                JOptionPane.showMessageDialog(frame, "خطا در حذف بیمار.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "لطفاً یک ردیف برای حذف انتخاب کنید.");
        }
    }

    private void searchPatientByNationalCode() {
        String nationalCode = searchNationalCodeField.getText();
        if (nationalCode.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "لطفاً کد ملی را وارد کنید.");
            return;
        }
        Patient patient = DatabaseOperations.getPatientByNationalCode(nationalCode);
        if (patient != null) {
            firstNameField.setText(patient.getFirstName());
            lastNameField.setText(patient.getLastName());
            nationalCodeField.setText(patient.getNationalCode());
            diseaseField.setText(patient.getDisease());
        } else {
            JOptionPane.showMessageDialog(frame, "بیماری با این کد ملی یافت نشد.");
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        nationalCodeField.setText("");
        diseaseField.setText("");
    }

    private void loadPatientData() {
        tableModel.setRowCount(0);
        List<Object[]> patientList = DatabaseOperations.getAllPatients();
        for (Object[] patient : patientList) {
            tableModel.addRow(patient);
        }
    }
}
