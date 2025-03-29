package jku.se;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;

public class Database {

    // Konstanten für die Datenbankverbindung
    private static final String JDBC_URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.pwltfjlqripcrhenhgnk";
    private static final String PASSWORD = "ujCpo7WdTPUzWpss";
    private static final String SUPABASE_BUCKET = "invoices";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB3bHRmamxxcmlwY3JoZW5oZ25rIiwi" +
            "cm9sZSI6ImFub24iLCJpYXQiOjE3NDIzOTY0NTAsImV4cCI6MjA1Nzk3MjQ1MH0.VSMfiNzyXxzSjXyiwhkomUk_kd5WYbuuLXLBVIgfo_I";
    private static final String SUPABASE_URL = "https://pwltfjlqripcrhenhgnk.supabase.co";

    // Methode, um eine Verbindung zur Datenbank zu erhalten
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    // Methode zum Schließen der Datenbankverbindung
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();  // Verbindung schließen
                System.out.println("Datenbankverbindung geschlossen.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        }
    }

    public static boolean validateLogin(String username, String password, StringBuilder userRole) {
        String query = "SELECT * FROM accounts WHERE email = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        userRole.append(rs.getString("role"));

                        if (password.equals(storedPassword)) {
                            conn.commit();
                            return true;
                        }
                    }
                }

            } catch (SQLException e) {
                conn.rollback();  // Rollback im Fehlerfall
                System.out.println("Fehler bei der Abfrage: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung: " + e.getMessage());
        }
        return false;
    }

    public static String uploadImage(File imageFile) {
        try {
            // 🔹 1. Eindeutigen Dateinamen generieren
            String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            // 🔹 2. Content-Type korrekt bestimmen
            String contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) contentType = "application/octet-stream"; // Fallback

            // 🔹 3. Verbindung zu Supabase aufbauen
            HttpURLConnection conn = (HttpURLConnection) new URL(uploadUrl).openConnection();
            conn.setRequestMethod("PUT"); // Supabase erwartet PUT für direkten Upload
            conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);

            // 🔹 4. Datei hochladen
            try (OutputStream os = conn.getOutputStream();
                 FileInputStream fis = new FileInputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            // 🔹 5. Antwort prüfen
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                return getPublicUrl(fileName);
            } else {
                System.out.println("Upload fehlgeschlagen: HTTP " + responseCode);
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

    // Methode zur Generierung der öffentlichen URL
    private static String getPublicUrl(String filePath) {
        return SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + filePath;
    }

    public static boolean insertInvoice(Connection connection, String username, double betrag, String datum, invoice_typ typ, boolean proved, File imageFile) {
        String sqlInsert = "INSERT INTO rechnungen (username, betrag, datum, typ, proved, image) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Deaktiviere Auto-Commit, damit wir manuell transaktionale Kontrolle übernehmen können
            connection.setAutoCommit(false);

            // Prüfen, ob bereits eine Rechnung für das Datum existiert
            if (invoiceExists(connection, username, datum)) {
                System.out.println("Rechnung für dieses Datum existiert bereits!");
                connection.rollback();  // Rollback bei Fehler
                return false;
            }

            // Bild hochladen
            String imageUrl = uploadImage(imageFile);
            if (imageUrl == null) {
                System.out.println("Bild-Upload fehlgeschlagen. Abbruch.");
                connection.rollback();  // Rollback bei Fehler
                return false;
            }

            // Einfügen der Rechnung in die Datenbank
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, username);
                pstmt.setDouble(2, betrag);
                pstmt.setDate(3, java.sql.Date.valueOf(datum));
                pstmt.setObject(4, typ, java.sql.Types.OTHER);
                pstmt.setBoolean(5, proved);
                pstmt.setString(6, imageUrl);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rechnung erfolgreich eingefügt!");
                    connection.commit();  // Transaktion erfolgreich abschließen
                    return true;
                } else {
                    System.out.println("Rechnung konnte nicht eingefügt werden.");
                    connection.rollback();  // Rollback bei Fehler
                    return false;
                }
            } catch (SQLException e) {
                connection.rollback();  // Rollback im Fehlerfall
                System.out.println("Datenbankfehler: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            try {
                connection.rollback();  // Rollback im Fehlerfall
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback fehlgeschlagen: " + rollbackEx.getMessage());
            }
            System.out.println("Datenbankfehler: " + e.getMessage());
        } finally {
            try {
                // Auto-Commit zurücksetzen
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    public static boolean invoiceExists(Connection connection, String username, String datum) {
        String sql = "SELECT 1 FROM rechnungen WHERE username = ? AND datum = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(datum));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Gibt true zurück, wenn ein Eintrag existiert
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei Existenzprüfung: " + e.getMessage());
        }
        return false;
    }

}
