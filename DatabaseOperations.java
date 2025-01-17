import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    // اتصال به دیتابیس
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hospital_management", "root", "2130909991@Gt");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // عملیات افزودن داده کارمند
    public static boolean addStaff(String firstName, String lastName, String nationalCode, String jobTitle,
            double salary) {
        String query = "INSERT INTO staff (first_name, last_name, national_code, job_title, salary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, nationalCode);
            stmt.setString(4, jobTitle);
            stmt.setDouble(5, salary);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // عملیات جستجوی کارمند با کد ملی
    public static Object[] getStaffByNationalCode(String nationalCode) {
        String query = "SELECT * FROM staff WHERE national_code = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nationalCode);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Object[] {
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("national_code"),
                        rs.getString("job_title"),
                        rs.getDouble("salary")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // عملیات افزودن داده بیمار
    public static boolean addPatient(String firstName, String lastName, String nationalCode, String disease) {

        // حذف فضای اضافی از کد ملی و تبدیل به حروف کوچک
        nationalCode = nationalCode.trim();

        // ابتدا بررسی می‌کنیم که آیا بیمار با این کد ملی وجود دارد یا خیر
        if (getPatientByNationalCode(nationalCode) != null) {
            return false;
        }
        String query = "INSERT INTO patients (firstName, lastName, nationalCode, disease) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, nationalCode);
            stmt.setString(4, disease);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // عملیات جستجوی بیمار با کد ملی
    public static Patient getPatientByNationalCode(String nationalCode) {
        String query = "SELECT * FROM patients WHERE nationalCode = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nationalCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String disease = rs.getString("disease");
                // ایجاد شیء Patient با داده‌های موجود در پایگاه داده
                return new Patient(firstName, lastName, nationalCode, disease);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // اگر بیمار پیدا نشد
    }

    // عملیات ویرایش داده کارمند
    public static boolean updateStaff(String firstName, String lastName, String nationalCode, String jobTitle,
            double salary) {
        String query = "UPDATE staff SET first_name = ?, last_name = ?, job_title = ?, salary = ? WHERE national_code = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, jobTitle);
            stmt.setDouble(4, salary);
            stmt.setString(5, nationalCode);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // عملیات ویرایش داده بیمار
    public static boolean updatePatient(String firstName, String lastName, String nationalCode, String disease) {
        String query = "UPDATE patients SET firstName = ?, lastName = ?, disease = ? WHERE nationalCode = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, disease);
            stmt.setString(4, nationalCode);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // عملیات حذف داده کارمند
    public static boolean deleteStaff(String nationalCode) {
        String query = "DELETE FROM staff WHERE national_code = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nationalCode);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // عملیات حذف داده بیمار
    public static boolean deletePatient(String nationalCode) {
        String query = "DELETE FROM patients WHERE nationalCode = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nationalCode);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // دریافت همه بیماران
    public static List<Object[]> getAllPatients() {
        String query = "SELECT * FROM patients";
        List<Object[]> patientList = new ArrayList<>();

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                patientList.add(new Object[] {
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("nationalCode"),
                        rs.getString("disease")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientList;
    }

    // دریافت همه کارمندان
    public static List<Object[]> getAllStaff() {
        String query = "SELECT * FROM staff";
        List<Object[]> staffList = new ArrayList<>();

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                staffList.add(new Object[] {
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("national_code"),
                        rs.getString("job_title"),
                        rs.getDouble("salary")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

}
