import jku.se.Controller.SubmitBillController;
import jku.se.Database;
import jku.se.InvoiceScan;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;

import static jku.se.Database.uploadImage;

public class TestMethoden {

    public static int uploadInvoice(Connection connection, String username, double betrag, LocalDate datum) throws SQLException {
        int invoiceId = 0;

        String sqlInsert = "INSERT INTO rechnungen (username, betrag, datum,typ,status, image) VALUES (?, ?, ?,?,?,?)";

        //transaction
        try {
            connection.setAutoCommit(false);
            // Inserting the invoice into the database
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, username);
                pstmt.setDouble(2, betrag);
                pstmt.setDate(3, Date.valueOf(datum));
                InvoiceType typ = InvoiceType.SUPERMARKET;
                InvoiceStatus status = InvoiceStatus.ACCEPTED;
                pstmt.setObject(4, typ, Types.OTHER);
                pstmt.setObject(5, status, Types.OTHER);
                File imageFile = new File("src/test/resources/testfile.jpg");
                String imageUrl = uploadImage(imageFile);
                if (imageUrl == null) { //checks if upload was successfully
                    System.out.println("Bild-Upload fehlgeschlagen. Abbruch.");
                    connection.rollback();  // rollback on error
                    return 0;
                }
                pstmt.setString(6, imageUrl);
                //check if the insert was successfully
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rechnung erfolgreich eingefügt!");
                    connection.commit();
                    // complete transaction successfully




                } else {
                    System.out.println("Rechnung konnte nicht eingefügt werden.");
                    connection.rollback();  // rollback on error
                }
            } catch (SQLException e) {
                connection.rollback();  // rollback on error
                System.out.println("Datenbankfehler: " + e.getMessage());
            }

        } catch (SQLException e) {
            try {
                connection.rollback();  // rollback on error
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback fehlgeschlagen: " + rollbackEx.getMessage());
            }
            System.out.println("Datenbankfehler: " + e.getMessage());
        } finally {
            try {
                // reset Auto-Commit
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        PreparedStatement stmt = Database.getConnection().prepareStatement(
                "SELECT id FROM rechnungen WHERE username = ? AND betrag = ? AND datum = ? ");
        stmt.setString(1, username);
        stmt.setDouble(2, betrag);
        stmt.setDate(3, Date.valueOf(datum));



        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
             invoiceId = rs.getInt("id");
            System.out.println("Neue Rechnung-ID: " + invoiceId);
        }

        return invoiceId;
    }

    public static int uploadInvoiceWithoutImage(Connection connection, String username, double betrag, LocalDate datum) throws SQLException {
        int invoiceId = 0;

        String sqlInsert = "INSERT INTO rechnungen (username, betrag, datum,typ,status) VALUES (?, ?, ?,?,?)";

        //transaction
        try {
            connection.setAutoCommit(false);
            // Inserting the invoice into the database
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, username);
                pstmt.setDouble(2, betrag);
                pstmt.setDate(3, Date.valueOf(datum));
                InvoiceType typ = InvoiceType.SUPERMARKET;
                InvoiceStatus status = InvoiceStatus.ACCEPTED;
                pstmt.setObject(4, typ, Types.OTHER);
                pstmt.setObject(5, status, Types.OTHER);
                //check if the insert was successfully
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rechnung erfolgreich eingefügt!");
                    connection.commit();
                    // complete transaction successfully




                } else {
                    System.out.println("Rechnung konnte nicht eingefügt werden.");
                    connection.rollback();  // rollback on error
                }
            } catch (SQLException e) {
                connection.rollback();  // rollback on error
                System.out.println("Datenbankfehler: " + e.getMessage());
            }

        } catch (SQLException e) {
            try {
                connection.rollback();  // rollback on error
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback fehlgeschlagen: " + rollbackEx.getMessage());
            }
            System.out.println("Datenbankfehler: " + e.getMessage());
        } finally {
            try {
                // reset Auto-Commit
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        PreparedStatement stmt = Database.getConnection().prepareStatement(
                "SELECT id FROM rechnungen WHERE username = ? AND betrag = ? AND datum = ? ");
        stmt.setString(1, username);
        stmt.setDouble(2, betrag);
        stmt.setDate(3, Date.valueOf(datum));



        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            invoiceId = rs.getInt("id");
            System.out.println("Neue Rechnung-ID: " + invoiceId);
        }

        return invoiceId;
    }
}
