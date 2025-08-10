import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ReservationForm extends JFrame {
    JTextField nameField, ageField, contactField, trainNoField, trainNameField, dateField, fromField, toField, pnrCancelField;
    JComboBox<String> classTypeCombo;

    String loggedInUser;

    public ReservationForm(String username) {
        this.loggedInUser = username;

        setTitle("Online Reservation System - Reservation");
        setLayout(null);
        setSize(600, 400); // Increased width for right-side panel
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final int CANCEL_OFFSET_X = 310;  // Shift cancel fields to the right

        JLabel lTitle = new JLabel("Make a Reservation");
        lTitle.setBounds(20,10,250,25);
        add(lTitle);

        addField("Name:", 40, nameField = new JTextField());
        addField("Age:", 70, ageField = new JTextField());
        addField("Contact:", 100, contactField = new JTextField());
        addField("Train No:", 130, trainNoField = new JTextField());
        addField("Train Name:", 160, trainNameField = new JTextField());
        addField("Class:", 190, classTypeCombo = new JComboBox<>(new String[] {"Sleeper", "3AC", "2AC", "First Class"}));
        addField("Date (YYYY-MM-DD):", 220, dateField = new JTextField());
        addField("From:", 250, fromField = new JTextField());
        addField("To:", 280, toField = new JTextField());

        JButton bookBtn = new JButton("Book");
        bookBtn.setBounds(80, 320, 100, 30);
        add(bookBtn);

        // Cancellation (Right side)
        JLabel lCancel = new JLabel("Cancel Reservation (PNR):");
        lCancel.setBounds(CANCEL_OFFSET_X,40,200,25);
        add(lCancel);
        pnrCancelField = new JTextField();
        pnrCancelField.setBounds(CANCEL_OFFSET_X,70,170,25);
        add(pnrCancelField);
        JButton cancelBtn = new JButton("Cancel Ticket");
        cancelBtn.setBounds(CANCEL_OFFSET_X,110,170,30);
        add(cancelBtn);

        bookBtn.addActionListener(e -> bookTicket());
        cancelBtn.addActionListener(e -> cancelTicket());

        setVisible(true);
    }

    private void addField(String label, int y, JTextField field) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 120, 25);
        field.setBounds(150, y, 130, 25);
        add(l); add(field);
    }

    private void addField(String label, int y, JComboBox<String> combo) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 120, 25);
        combo.setBounds(150, y, 130, 25);
        add(l); add(combo);
    }

    private void bookTicket() {
        // Basic form validation:
        if (nameField.getText().trim().isEmpty() ||
            ageField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty() ||
            trainNoField.getText().trim().isEmpty() ||
            trainNameField.getText().trim().isEmpty() ||
            dateField.getText().trim().isEmpty() ||
            fromField.getText().trim().isEmpty() ||
            toField.getText().trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Please fill all fields before booking.");
            return;
        }
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            // Optionally, more validation can be added
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age. Please enter a valid number.");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO reservations(name, age, contact, trainno, trainname, classtype, doj, origin, dest, userid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nameField.getText().trim());
            ps.setInt(2, Integer.parseInt(ageField.getText().trim()));
            ps.setString(3, contactField.getText().trim());
            ps.setString(4, trainNoField.getText().trim());
            ps.setString(5, trainNameField.getText().trim());
            ps.setString(6, (String)classTypeCombo.getSelectedItem());
            ps.setString(7, dateField.getText().trim());
            ps.setString(8, fromField.getText().trim());
            ps.setString(9, toField.getText().trim());
            ps.setString(10, loggedInUser);
            int result = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int pnr = 0;
            if (rs.next()) {
                pnr = rs.getInt(1);
            }
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Reservation Successful!\nYour PNR: " + pnr);
                // Optionally, clear or reset fields after success.
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book the ticket!");
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void cancelTicket() {
        String pnrTxt = pnrCancelField.getText().trim();
        if (pnrTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR to cancel.");
            return;
        }
        int pnr = 0;
        try {
            pnr = Integer.parseInt(pnrTxt);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid PNR format.");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM reservations WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, pnr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Reservation Details:\n"
                        + "Name: " + rs.getString("name") + "\n"
                        + "Train: " + rs.getString("trainname") + "\n"
                        + "From: " + rs.getString("origin") + "  To: " + rs.getString("dest") + "\n"
                        + "Date: " + rs.getString("doj") + "\n\n"
                        + "Confirm cancellation?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    PreparedStatement ps2 = con.prepareStatement("DELETE FROM reservations WHERE id=?");
                    ps2.setInt(1, pnr);
                    int deleted = ps2.executeUpdate();
                    if (deleted > 0)
                        JOptionPane.showMessageDialog(this, "Reservation cancelled.");
                    else
                        JOptionPane.showMessageDialog(this, "Cancellation failed.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "PNR not found.");
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
