import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {

    public static void main(String[] args) {
        // ایجاد پنجره اصلی
        JFrame frame = new JFrame("انتخاب بخش مدیریت");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // پنل اصلی
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1)); // تعداد دکمه‌ها به 5 افزایش یافت

        // دکمه‌های انتخاب
        JButton staffButton = new JButton("مدیریت کارکنان");
        JButton patientButton = new JButton("مدیریت بیماران");
        JButton roomButton = new JButton("مدیریت اتاق‌ها");
        JButton reportButton = new JButton("تولید گزارش"); // دکمه جدید برای تولید گزارش
        JButton backButton = new JButton("بازگشت");

        // اضافه کردن دکمه‌ها به پنل
        panel.add(staffButton);
        panel.add(patientButton);
        panel.add(roomButton);
        panel.add(reportButton);
        panel.add(backButton);

        // افزودن پنل به فریم
        frame.add(panel);

        // ایجاد نمونه‌ای از ReportGenerationApp
        ReportGenerationApp reportGenerationApp = new ReportGenerationApp();

        // تعریف عملگرها برای دکمه‌ها
        staffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StaffManagementApp(frame);
            }
        });

        patientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PatientManagementApp(frame, reportGenerationApp); // ارسال instance از ReportGenerationApp
            }
        });

        roomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(frame, "تعداد اتاق‌ها را وارد کنید:");
                if (input != null && !input.isEmpty()) {
                    try {
                        int numberOfRooms = Integer.parseInt(input);
                        new RoomManagement(frame, numberOfRooms); // ایجاد RoomManagement با تعداد اتاق
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "لطفا یک عدد صحیح وارد کنید.");
                    }
                }
            }
        });

        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportGenerationApp.showWindow(); // باز کردن رابط تولید گزارش
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        // نمایش پنجره اصلی
        frame.setVisible(true);
    }
}
