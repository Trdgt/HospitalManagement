import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RoomManagement {

    private class Room {
        private int roomNumber;
        private boolean isOccupied;

        public Room(int roomNumber, boolean isOccupied) {
            this.roomNumber = roomNumber;
            this.isOccupied = isOccupied;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        public boolean isOccupied() {
            return isOccupied;
        }

        public void occupyRoom() {
            isOccupied = true;
            updateRoomStatusInDatabase(roomNumber, true);
        }

        public void freeRoom() {
            isOccupied = false;
            updateRoomStatusInDatabase(roomNumber, false);
        }
    }

    private List<Room> rooms;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String USER = "root"; 
    private static final String PASSWORD = "2130909991@Gt"; 

    public RoomManagement(JFrame parentFrame, int numberOfRooms) {
        rooms = new ArrayList<>(); 
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            loadRoomsFromDatabase(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // اگر اتاق‌ها از دیتابیس بارگذاری نشدند، اتاق‌ها را به صورت دستی ایجاد می‌کنیم
        if (rooms.isEmpty()) {
            for (int i = 1; i <= numberOfRooms; i++) {
                rooms.add(new Room(i, false));
            }
        }

        createRoomManagementUI(parentFrame);
    }

    private void createRoomManagementUI(JFrame parentFrame) {
        JFrame frame = new JFrame("مدیریت اتاق‌ها");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // ایجاد پنل برای نمایش اتاق‌ها
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1)); // تعداد سطرها نامحدود و یک ستون برای دکمه‌ها

        // افزودن فیلد جستجو
        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel("جستجو اتاق شماره:");
        JTextField searchField = new JTextField(10);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // افزودن دکمه بازگشت
        JButton backButton = new JButton("بازگشت");
        backButton.addActionListener(e -> {
            frame.dispose(); 
            parentFrame.setVisible(true); 
        });

        
        searchPanel.add(backButton);

        
        frame.add(searchPanel, BorderLayout.NORTH);

        // نمایش دکمه‌های اتاق‌ها
        for (Room room : rooms) {
            JButton roomButton = new JButton(
                    "اتاق شماره " + room.getRoomNumber() + " - " + (room.isOccupied() ? "اشغال" : "خالی"));
            roomButton.addActionListener(e -> {
                if (room.isOccupied()) {
                    room.freeRoom();
                    roomButton.setText("اتاق شماره " + room.getRoomNumber() + " - خالی");
                } else {
                    room.occupyRoom();
                    roomButton.setText("اتاق شماره " + room.getRoomNumber() + " - اشغال");
                }
            });

            panel.add(roomButton);
        }

        
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // افزودن عملکرد جستجو برای پیدا کردن اتاق‌ها بر اساس شماره
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                for (Component component : panel.getComponents()) {
                    if (component instanceof JButton) {
                        JButton button = (JButton) component;
                        String buttonText = button.getText().toLowerCase();
                        if (buttonText.contains(query)) {
                            button.setVisible(true);
                        } else {
                            button.setVisible(false);
                        }
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    private void loadRoomsFromDatabase() {
        try {
            String query = "SELECT * FROM rooms";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int roomNumber = resultSet.getInt("roomNumber");
                boolean isOccupied = resultSet.getBoolean("isOccupied");
                rooms.add(new Room(roomNumber, isOccupied));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRoomStatusInDatabase(int roomNumber, boolean isOccupied) {
        try {
            String query = "UPDATE rooms SET isOccupied = ? WHERE roomNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBoolean(1, isOccupied);
            preparedStatement.setInt(2, roomNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public Room allocateRoom() {
        for (Room room : rooms) {
            if (!room.isOccupied()) {
                room.occupyRoom();
                return room;
            }
        }
        System.out.println("هیچ اتاق خالی موجود نیست!");
        return null;
    }

    public void freeRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                room.freeRoom();
                break;
            }
        }
    }

    public void showRoomStatus() {
        for (Room room : rooms) {
            System.out
                    .println("اتاق شماره " + room.getRoomNumber() + " - " + (room.isOccupied() ? "اشغال شده" : "خالی"));
        }
    }
}
