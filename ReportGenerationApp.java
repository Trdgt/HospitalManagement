import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReportGenerationApp {
    private JFrame frame;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private List<Patient> patientList; // لیست مشترک بیماران
    private JButton generateReportButton;
    private JPanel panel;
    private JScrollPane tableScrollPane;
    private Connection connection; // متغیر اتصال به دیتابیس
    
    
    public ReportGenerationApp() {
        patientList = new ArrayList<>(); // این لیست حاوی بیماران اضافه شده است
        initializeDatabase(); 
        loadPatientsFromDatabase(); 
        frame = new JFrame("گزارش گیری تمامی بیماران");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        
        generateReportButton = new JButton("تولید گزارش تمامی بیماران");
        panel.add(generateReportButton, BorderLayout.NORTH);

        // جدول برای نمایش گزارش
        tableModel = new DefaultTableModel(new Object[]{"نام", "نام خانوادگی", "کد ملی", "بیماری"}, 0);
        reportTable = new JTable(tableModel);
        tableScrollPane = new JScrollPane(reportTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // افزودن اکشن برای دکمه تولید گزارش
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        
        frame.add(panel);
        frame.setVisible(true);
    }

   
    private void initializeDatabase() {
        try {
            // اتصال به دیتابیس MySQL
            String url = "jdbc:mysql://localhost:3306/hospital_management"; 
            String username = "root"; 
            String password = "2130909991@Gt"; 
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "خطا در اتصال به دیتابیس.");
        }
    }

    // متد برای بارگذاری بیماران از دیتابیس
    private void loadPatientsFromDatabase() {
        try {
            String query = "SELECT firstName, lastName, nationalCode, disease FROM patients";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String nationalCode = resultSet.getString("nationalCode");
                String disease = resultSet.getString("disease");
                
                Patient patient = new Patient(firstName, lastName, nationalCode, disease);
                patientList.add(patient);
            }
            generateReport();  // پس از بارگذاری بیماران از دیتابیس، گزارش را تولید می‌کنیم
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "خطا در بارگذاری داده‌ها از دیتابیس.");
        }
    }

    // متد برای بررسی تکراری بودن کد ملی
    private boolean isNationalCodeExists(String nationalCode) {
        try {
            String query = "SELECT COUNT(*) FROM patients WHERE nationalCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nationalCode);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // اگر بیشتر از 0 بیمار با این کد ملی وجود داشت
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    


public void addPatientToReport(Patient patient) {
    
    if (isNationalCodeExists(patient.getNationalCode())) {
        JOptionPane.showMessageDialog(frame, "کد ملی وارد شده قبلاً در سیستم موجود است.");
        return; 
    }

    // اگر کد ملی تکراری نبود، بیمار را به دیتابیس اضافه می‌کنیم
    try {
        String query = "INSERT INTO patients (firstName, lastName, nationalCode, disease) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, patient.getFirstName());
        statement.setString(2, patient.getLastName());
        statement.setString(3, patient.getNationalCode());
        statement.setString(4, patient.getDisease());
        statement.executeUpdate();

        
        patientList.add(patient);
        generateReport();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "خطا در افزودن بیمار به دیتابیس.");
    }
}
    
    public void removePatientFromReport(String nationalCode) {
        try {
            // حذف بیمار از دیتابیس
            String query = "DELETE FROM patients WHERE nationalCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nationalCode);
            statement.executeUpdate();

            // حذف بیمار از لیست و بروزرسانی گزارش
            patientList.removeIf(patient -> patient.getNationalCode().equals(nationalCode));
            generateReport();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "خطا در حذف بیمار از دیتابیس.");
        }
    }

    

    // متدی برای به‌روزرسانی گزارش
    public void updateReport() {
        System.out.println("گزارش به‌روز شد:");
        for (Patient patient : patientList) {
            System.out.println("نام: " + patient.getFirstName() + "، نام خانوادگی: " + patient.getLastName());
        }
    }

    private void generateReport() {
        try {
            if (tableModel == null) {
                tableModel = new DefaultTableModel(new Object[]{"نام", "نام خانوادگی", "کد ملی", "بیماری"}, 0);
            }
            tableModel.setRowCount(0);  // پاک کردن داده‌های قبلی
            for (Patient patient : patientList) {
                Object[] row = {patient.getFirstName(), patient.getLastName(), patient.getNationalCode(), patient.getDisease()};
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "خطا در تولید گزارش.");
        }
    }

    public void showWindow() {
        frame.setVisible(true); 
}
}
