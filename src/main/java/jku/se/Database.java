package jku.se;

import javafx.application.Platform;
import jku.se.controller.SubmitBillController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database:
 * Methods for the communication with Supabase
 */
public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    /**
     * URL for JDBC
     */
    private static final String JDBC_URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";

    /**
     * User for postgres
     */
    private static final String USER = "postgres.pwltfjlqripcrhenhgnk";

    /**
     * Password
     */
    private static final String PASSWORD = "ujCpo7WdTPUzWpss";

    /**
     * Supabase-Bucket
     * for uploading images of the invoices
     */
    public static final String SUPABASE_BUCKET = "invoices";

    /**
     * Supabase-API-Key
     */
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB3bHRmamxxcmlwY3JoZW5oZ25rIiwi" +
            "cm9sZSI6ImFub24iLCJpYXQiOjE3NDIzOTY0NTAsImV4cCI6MjA1Nzk3MjQ1MH0.VSMfiNzyXxzSjXyiwhkomUk_kd5WYbuuLXLBVIgfo_I";

    /**
     * Supabase-URL
     */
    public static final String SUPABASE_URL = "https://pwltfjlqripcrhenhgnk.supabase.co";

    /**
     * Creates a connection to the database
     *
     * @throws SQLException Falls ein Fehler bei der Verbindung zur Datenbank auftritt.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * uploads the image/pdf of the invoice to the supabase-storage and generates a link to it(AI)
     * @param imageFile
     */
    public static String uploadImage(File imageFile) {
        String image =  null;
        try {
            // Generate unique file name
            String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            // Determine the content type correctly
            String contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";  //if detection fails, set a default value
            }


            // establish connection to Supabase and configures it for a PUT request with authorization
            HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setDoOutput(true);

            // reads a file in blocks and sends it to the server via the HTTP connection
            try (OutputStream outputStream = connection.getOutputStream();
                 FileInputStream fis = new FileInputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                } // End of while loop
            } // End of try block

            //checks answer
            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) { //is uploaded successfully
                image = getPublicUrl(fileName);
            }
        } catch (Exception exception) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Exeption:", exception);
            }
        }
        return image;
    }

    /**
     * Method for generating the public URL for the image (AI)
     * @param filePath
     * @return Url for picture
     */
    private static String getPublicUrl(String filePath) {
        return SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + filePath;
    }


    /**
     * get username of the invoice
     * @param identifier
     * @return username
     */
    public static String getInvoiceUsername(int identifier) {
        String username = null;

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT username FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, identifier);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        username = resultSet.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Fehler beim Abrufen des Rechnungsstatus mit ID: " + identifier, e);
            }
        }
        return username;
    }


    /**
     * get the date of the invoice
     * @param identifier
     * @return invoice date
     */
    public static LocalDate getInvoiceDate(int identifier) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT datum FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, identifier);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getDate("datum").toLocalDate();
                    }
                }
            }
        } catch (SQLException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Fehler beim Abrufen des Rechnungsstatus mit ID: " + identifier, e);
            }
        }
        return null;
    }

    /**
     * get status of the Invoice
     * @param identifier
     * @return status
     */
    public static String getInvoiceStatus(int identifier) {
        String status = null;
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT status FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, identifier);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                         status = resultSet.getString("status");
                    }
                }
            }
        } catch (SQLException exception) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Exeption:", exception);
            }
        }
        return status;
    }


    /**
     * get image of the invoice
     * @param identifier
     * @return image of the invoice
     */
    public static String getInvoiceImage(int identifier) {
        String image = null;
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT image FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, identifier);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        image =  resultSet.getString("image");
                    }
                }
            }
        } catch (SQLException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Fehler beim Abrufen des Fotos mit ID: " + identifier, e);
            }
        }
        return image;
    }


    /**
     * get refund
     * @param identifier
     * @return refund of the invoice
     */
    public static double getInvoiceRefund(int identifier) {
        double refund = 0.0;
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT refund FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, identifier);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        refund = resultSet.getDouble("refund");
                    }
                }
            }
        } catch (SQLException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Fehler: " + identifier, e);
            }
        }
        return refund;
    }


    /**
     * Updates invoice
     * @param betrag
     * @param datum
     * @param typ
     * @param username
     * @param status
     * @param image
     * @param refund
     * @param identifier
     * @return if the update was successful
     */
    public static boolean updateInvoice(double betrag, Date datum, InvoiceType typ, String username, InvoiceStatus status, String image, double refund, int identifier){
        boolean userFound = false;
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT username FROM accounts";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    String users = resultSet.getString("username");
                        if(username.equals(users)) {
                        userFound = true; //wenn user gefunden wird
                    }
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            if (!userFound) return false; //wenn user nicht in abfrage gefunden wird

            if (typ != InvoiceType.SUPERMARKET && typ != InvoiceType.RESTAURANT){
                return false;
            }

            if (betrag < 0) {
                 return false;
            }

            if(refund != 3 && refund != 2.5) {//falls beim test eine andere zahl eingegeben wird, soll false zurückgegeben werden
                return false;
            }

            String updateQuery = "UPDATE rechnungen SET betrag = ?, datum = ?, typ = ?, username = ?, status = ?, image = ?, refund = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                stmt.setDouble(1, betrag);
                stmt.setDate(2, datum);
                stmt.setObject(3, typ, Types.OTHER);
                stmt.setString(4, username);
                stmt.setObject(5, status, Types.OTHER);
                stmt.setString(6, image);
                stmt.setDouble(7, refund);
                stmt.setInt(8, identifier);
                int rows = stmt.executeUpdate();
                return rows == 1;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * uploads the invoice data to the table rechnungen (AI)
     * @param connection
     * @param username
     * @param betrag
     * @param datum
     * @param typ
     * @param status
     * @param imageFile
     * @param refund
     * @param controller
     */
    public static void uploadInvoice(Connection connection, String username, double betrag, LocalDate datum, InvoiceType typ, InvoiceStatus status, File imageFile, Double refund, SubmitBillController controller) {
        String sqlInsert = "INSERT INTO rechnungen (username, betrag, datum, typ, status, image,refund) VALUES (?, ?, ?, ?, ?, ?,?)";

        //transaction
        try {
            connection.setAutoCommit(false);

            //Check if the entered date is within the current month
            if(!InvoiceScan.isWithinCurrentMonth(datum)) {
                controller.displayMessage("Datum muss innerhalb des aktuellen Monats liegen.", "red");
                connection.rollback();  // rollback on error
                return;
            }

            //Check if an invoice already exists for the date
            if (invoiceExists(connection, username, datum)) {
                controller.displayMessage("Rechnung für dieses Datum existiert bereits!", "red");
                connection.rollback();  // rollback on error
                return;
            }

            //Upload image
            String imageUrl = uploadImage(imageFile);
            if (imageUrl == null) { //checks if upload was successfully
                connection.rollback();  // rollback on error
                return;
            }

            // Inserting the invoice into the database
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, username);
                pstmt.setDouble(2, betrag);
                pstmt.setDate(3, Date.valueOf(datum));
                pstmt.setObject(4, typ, Types.OTHER);
                pstmt.setObject(5, status, Types.OTHER);
                pstmt.setString(6, imageUrl);
                pstmt.setDouble(7, refund);

                //check if the insert was successfully
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    controller.displayMessage("Rechnung erfolgreich eingefügt!", "green");
                    connection.commit();  // complete transaction successfully
                } else {
                    connection.rollback();  // rollback on error
                }
            } catch (SQLException e) {
                connection.rollback();  // rollback on error
            }

        } catch (SQLException e) {
            try {
                connection.rollback();  // rollback on error
            } catch (SQLException rollbackEx) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Exeption:", rollbackEx);
                }
            }
        } finally {
            try {
                // reset Auto-Commit
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * checks if the user has already uploaded an invoice for that day
     * @param connection
     * @param username
     * @param datum
     * @return
     */
    public static boolean invoiceExists(Connection connection, String username, LocalDate datum) {
        boolean exists = false;
        String sql = "SELECT 1 FROM rechnungen WHERE username = ? AND datum = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, Date.valueOf(datum));
            try (ResultSet resultSet = pstmt.executeQuery()) {
                exists = resultSet.next(); // Returns true if an entry exists
            }
        } catch (SQLException exception) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Exeption:", exception);
            }
        }
        return exists;
    }

    /**
     * OCR+data upload (AI)
     * @param path
     * @param controller
     */
    public static void invoiceScanUpload(String path, SubmitBillController controller) {

        //Perform OCR processing and database operations in a background thread
        new Thread(() -> {
            Invoice invoice; //generate invoice instance
            try {
                InvoiceScan invoiceScan = new InvoiceScan(controller);
                invoice = invoiceScan.scanInvoice(path); //Specify the path to the image file
            } catch (Exception e) {
                Platform.runLater(() -> controller.displayMessage("Fehler beim Scannen der Rechnung: " + e.getMessage(), "red"));
                return; //if an error occurs, abort the method
            }

            // Perform database operations
            try (Connection connection = Database.getConnection()) {
                File imageFile = new File(path); // Pfad zur Datei

                double sum = invoice.getSum();
                LocalDate date = invoice.getDate();
                InvoiceType invoiceType = invoice.getTyp();
                InvoiceStatus invoiceStatus = invoice.getStatus();
                double refund = invoice.getRefund();

                // Insert invoice into the database
                Database.uploadInvoice(connection, Login.getCurrentUsername(), sum, date, invoiceType, invoiceStatus, imageFile, refund,controller);

            } catch (SQLException e) {
                Platform.runLater(() -> controller.displayMessage("Fehler bei der Verbindung zur Datenbank: " + e.getMessage(), "red"));
            }
        }).start(); //starts the background thread
    }

    /**
     * deletes an image from the supabase-storage (AI)
     * @param imageUrl
     * @return if the image deletion was successful
     */
    public static boolean deleteImage(String imageUrl) {
        boolean result = false;
        try {
            // Extracts file-name from url
            URI uri = new URI(imageUrl);
            String fileName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
            String deleteUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            //Connect to the database
            HttpURLConnection conn = (HttpURLConnection) new URL(deleteUrl).openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

            // checks response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) { // Erfolgreich gelöscht
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /**
     * method to delete a specific invoice from the database and deletes the image from the storage (AI)
     * @param connection
     * @param username
     * @param date
     * @return if the invoice deletion was successful
     */
    public static boolean deleteInvoice(Connection connection, String username, LocalDate date) {
        boolean result = false;

        // First, fetch the image URL associated with the invoice record
        String imageUrl = null;
        try {
            // Query to get the image URL for the invoice
            String query = "SELECT image FROM rechnungen WHERE username = ? AND datum = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setDate(2, Date.valueOf(date));

                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        imageUrl = resultSet.getString("image");
                    }
                }
            }

            // If an image URL is found, delete the image from Supabase Storage
            if (imageUrl != null && deleteImage(imageUrl)) {
                // Image deletion was successful, now delete the invoice record from the database
                String deleteQuery = "DELETE FROM rechnungen WHERE username = ? AND datum = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setString(1, username);
                    deleteStmt.setDate(2, Date.valueOf(date));

                    int rowsAffected = deleteStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        result = true; // Successful deletion
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            result = false; // Error occurred during deletion process
        }
        return result;
    }


}

