import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    JTextField userField;
    JPasswordField passField;

    public LoginForm() {
        setTitle("Online Reservation System - Login");
        setLayout(null);
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel l1 = new JLabel("User ID:");
        JLabel l2 = new JLabel("Password:");
        userField = new JTextField();
        passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        l1.setBounds(30,30,80,25);
        userField.setBounds(120,30,150,25);
        l2.setBounds(30,70,80,25);
        passField.setBounds(120,70,150,25);
        loginBtn.setBounds(120,110,100,30);

        add(l1); add(userField);
        add(l2); add(passField); add(loginBtn);

        loginBtn.addActionListener(e -> authenticate());

        setVisible(true);
    }

    private void authenticate() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE userid=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, userField.getText());
            ps.setString(2, new String(passField.getPassword()));
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                new ReservationForm(userField.getText());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error.");
        }
    }
    public static void main(String[] args) {
        new LoginForm();
    }
}
