package jku.se;

import javafx.application.Platform;
import jku.se.Controller.SubmitBillController;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;


public class Database {

    // Constants for the database connection
    private static final String JDBC_URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.pwltfjlqripcrhenhgnk";
    private static final String PASSWORD = "ujCpo7WdTPUzWpss";
    public static final String SUPABASE_BUCKET = "invoices";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB3bHRmamxxcmlwY3JoZW5oZ25rIiwi" +
            "cm9sZSI6ImFub24iLCJpYXQiOjE3NDIzOTY0NTAsImV4cCI6MjA1Nzk3MjQ1MH0.VSMfiNzyXxzSjXyiwhkomUk_kd5WYbuuLXLBVIgfo_I";
    public static final String SUPABASE_URL = "https://pwltfjlqripcrhenhgnk.supabase.co";

    // Method to connect to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();  // Verbindung schließen
            } catch (SQLException e) {
                System.out.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        }
    }

    // uploads the image/pdf of the invoice to the supabase-storage and generates a link to it(AI)
    public static String uploadImage(File imageFile) {
        try {
            // Generate unique file name
            String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            // Determine the content type correctly
            String contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) contentType = "application/octet-stream";  //if detection fails, set a default value


            // establish connection to Supabase and configures it for a PUT request with authorization
            HttpURLConnection conn = (HttpURLConnection) new URL(uploadUrl).openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);

            // reads a file in blocks and sends it to the server via the HTTP connection
            try (OutputStream os = conn.getOutputStream();
                 FileInputStream fis = new FileInputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            //checks answer
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) { //is uploaded successfully
                return getPublicUrl(fileName);
            } else {
                System.out.println("Upload fehlgeschlagen: HTTP " + responseCode);

                //to get more information why the upload went wrong
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Fehlerdetails: " + response);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method for generating the public URL for the image (AI)
    private static String getPublicUrl(String filePath) {
        return SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + filePath;
    }

    //uploads the invoice data to the table rechnungen (AI)
    public static void uploadInvoice(Connection connection, String username, double betrag, LocalDate datum, InvoiceType typ, InvoiceStatus status, File imageFile, Double refund, SubmitBillController controller) {
        String sqlInsert = "INSERT INTO rechnungen (username, betrag, datum, typ, status, image,refund) VALUES (?, ?, ?, ?, ?, ?,?)";

        //transaction
        try {
            connection.setAutoCommit(false);

            //Check if the entered date is within the current month
            if(!InvoiceScan.isWithinCurrentMonth(datum)) {
                controller.displayMessage("Datum muss innerhalb des aktuellen Monats liegen.", "red");
                connection.rollback();  // rollback on error
                System.out.println("Datum muss innerhalb des aktuellen Monats liegen.");
                return;
            }

            //Check if an invoice already exists for the date
            if (invoiceExists(connection, username, datum)) {
                controller.displayMessage("Rechnung für dieses Datum existiert bereits!", "red");
                System.out.println("Rechnung für dieses Datum existiert bereits!");
                connection.rollback();  // rollback on error
                return;
            }

            //Upload image
            String imageUrl = uploadImage(imageFile);
            if (imageUrl == null) { //checks if upload was successfully
                System.out.println("Bild-Upload fehlgeschlagen. Abbruch.");
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
                    System.out.println("Rechnung erfolgreich eingefügt!");
                    connection.commit();  // complete transaction successfully
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

    }

    //checks if the user has already uploaded an invoice for that day
    public static boolean invoiceExists(Connection connection, String username, LocalDate datum) {
        String sql = "SELECT 1 FROM rechnungen WHERE username = ? AND datum = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(datum));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if an entry exists
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei Existenzprüfung: " + e.getMessage());
        }
        return false;
    }

    //OCR+data upload (AI)
    public static void invoiceScanUpload(String path, SubmitBillController controller) {

        //Perform OCR processing and database operations in a background thread
        new Thread(() -> {
            Invoice invoice = null; //generate invoice instance
            try {
                InvoiceScan invoiceScan = new InvoiceScan(controller);
                invoice = invoiceScan.scanInvoice(path); //Specify the path to the image file
            } catch (Exception e) {
                System.out.println("Fehler beim Scannen der Rechnung: " + e.getMessage());
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
                System.out.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
                Platform.runLater(() -> controller.displayMessage("Fehler bei der Verbindung zur Datenbank: " + e.getMessage(), "red"));
            }
        }).start(); //starts the background thread
    }

    //deletes an image from the supabase-storage (AI)
    public static boolean deleteImage(String imageUrl) {
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
                System.out.println("Bild erfolgreich gelöscht: " + fileName);
                return true;
            } else {
                System.out.println("Löschen fehlgeschlagen: HTTP " + responseCode);

                //Error-details
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Fehlerdetails: " + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //method to delete a specific invoice from the database and deletes the image from the storage (AI)
    public static boolean deleteInvoice(Connection connection, String username, LocalDate date) {
        // First, fetch the image URL associated with the invoice record
        String imageUrl = null;
        try {
            // Query to get the image URL for the invoice
            String query = "SELECT image FROM rechnungen WHERE username = ? AND datum = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setDate(2, java.sql.Date.valueOf(date));

                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    imageUrl = resultSet.getString("image");
                }
            }

            // If an image URL is found, delete the image from Supabase Storage
            if (imageUrl != null && deleteImage(imageUrl)) {
                // Image deletion was successful, now delete the invoice record from the database
                String deleteQuery = "DELETE FROM rechnungen WHERE username = ? AND datum = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setString(1, username);
                    deleteStmt.setDate(2, java.sql.Date.valueOf(date));

                    int rowsAffected = deleteStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Invoice and image deleted successfully.");
                        return true; // Successful deletion
                    } else {
                        System.out.println("No invoice record found to delete.");
                        return false; // Invoice not found
                    }
                }
            } else {
                System.out.println("Image deletion failed.");
                return false; // Image deletion failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred during deletion process
        }
    }


}

