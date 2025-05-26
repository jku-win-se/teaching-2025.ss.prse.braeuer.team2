import jku.se.*;
import jku.se.controller.SubmitBillController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OCRTest {

    // Initializes JavaFX once for all tests
    @BeforeAll
    static void initJFX() throws Exception {

    }

    /*
        Database.java
     */

    //trys to connect to supabase
    @Test
    public void testGetConnection() {
        try {
            Connection connection = Database.getConnection();
            assertNotNull(connection, "Connection should not be null.");
            connection.close();
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    //uploads a image
    @Test
    public void testUploadImage_Success() {
        File testFile = new File("src/test/resources/testfile.jpg");
        String imageUrl = Database.uploadImage(testFile);
        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("https://pwltfjlqripcrhenhgnk.supabase.co"));
        Database.deleteImage(imageUrl);
    }



    //checks if there is already an invoice uploaded for that day -> expected that no invoice exist for that day and user
    @Test
    public void testInvoiceExists_False() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            LocalDate date = LocalDate.of(1999, 3, 13); // Beispiel-Datum
            boolean exists = Database.invoiceExists(conn, "Test", date);
            assertFalse(exists);
        }
    }

    @Test
    public void testGetInvoiceDate_throwsSQLException_returnsNull() {
        // Mock Database.getConnection() to throw SQLException
        try (MockedStatic<Database> mockedDatabase = mockStatic(Database.class)) {
            mockedDatabase.when(Database::getConnection)
                    .thenThrow(new SQLException("Verbindung fehlgeschlagen"));

            // Call method
            LocalDate result = Database.getInvoiceDate(42);

            // It should catch the exception and return null
            assertNull(result);
        }
    }

    @Test
    void testGetInvoiceUsername_SQLException() throws Exception {
        // Mock Connection, PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Simuliere Fehler beim prepareStatement
        when(mockConnection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Simulierte SQL-Fehlermeldung"));

        // Verwende Spy oder Factory, um Verbindung zu mocken
        try (MockedStatic<Database> dbMock = Mockito.mockStatic(Database.class)) {
            dbMock.when(Database::getConnection).thenReturn(mockConnection);

            // Aufruf testen
            String username = Database.getInvoiceUsername(1);

            // Ergebnis prüfen
            assertNull(username);  // Methode gibt null bei Fehler zurück
        }
    }










        private Connection mockConn;
        private PreparedStatement mockStmt;
        private ResultSet mockResult;

        @BeforeEach
        public void setup() throws SQLException {
            mockConn = mock(Connection.class);
            mockStmt = mock(PreparedStatement.class);
            mockResult = mock(ResultSet.class);

            when(mockConn.prepareStatement(any(String.class))).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockResult);
        }

        @Test
        public void testGetInvoiceUsername_SQLExceptionHandled() throws Exception {
            Connection realConn = mock(Connection.class);
            when(realConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
            try (MockedStatic<Database> db = Mockito.mockStatic(Database.class)) {
                db.when(Database::getConnection).thenReturn(realConn);
                String result = Database.getInvoiceUsername(1);
                assertNull(result); // should return null on exception
            }
        }

        @Test
        public void testGetInvoiceDate_SQLExceptionHandled() throws Exception {
            Connection realConn = mock(Connection.class);
            when(realConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
            try (MockedStatic<Database> db = mockStatic(Database.class)) {
                db.when(Database::getConnection).thenReturn(realConn);
                LocalDate result = Database.getInvoiceDate(1);
                assertNull(result);
            }
        }

        @Test
        public void testGetInvoiceStatus_SQLExceptionHandled() throws Exception {
            Connection realConn = mock(Connection.class);
            when(realConn.prepareStatement(anyString())).thenThrow(new SQLException("Error"));
            try (MockedStatic<Database> db = mockStatic(Database.class)) {
                db.when(Database::getConnection).thenReturn(realConn);
                assertNull(Database.getInvoiceStatus(1));
            }
        }

        @Test
        public void testGetInvoiceImage_SQLExceptionHandled() throws Exception {
            Connection realConn = mock(Connection.class);
            when(realConn.prepareStatement(anyString())).thenThrow(new SQLException("Fehler"));
            try (MockedStatic<Database> db = mockStatic(Database.class)) {
                db.when(Database::getConnection).thenReturn(realConn);
                assertNull(Database.getInvoiceImage(1));
            }
        }

        @Test
        public void testGetInvoiceRefund_SQLExceptionHandled() throws Exception {
            Connection realConn = mock(Connection.class);
            when(realConn.prepareStatement(anyString())).thenThrow(new SQLException("Fehler"));
            try (MockedStatic<Database> db = mockStatic(Database.class)) {
                db.when(Database::getConnection).thenReturn(realConn);
                assertEquals(0.0, Database.getInvoiceRefund(1));
            }
        }

        @Test
        public void testInvoiceExists_SQLExceptionHandled() throws Exception {
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));
            assertFalse(Database.invoiceExists(mockConn, "testuser", LocalDate.now()));
        }

        @Test
        public void testDeleteInvoice_SQLExceptionHandled() throws Exception {
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Fehler beim Löschen"));
            assertFalse(Database.deleteInvoice(mockConn, "testuser", LocalDate.now()));
        }

    @Test
    public void testUploadInvoice_DateNotInCurrentMonth() throws Exception {
        Connection mockConn = mock(Connection.class);
        SubmitBillController mockController = mock(SubmitBillController.class);
        File mockFile = mock(File.class);

        try (MockedStatic<InvoiceScan> invoiceScanStatic = mockStatic(InvoiceScan.class)) {
            invoiceScanStatic.when(() -> InvoiceScan.isWithinCurrentMonth(any(LocalDate.class))).thenReturn(false);

            Database.uploadInvoice(mockConn, "user1", 100.0, LocalDate.now(), InvoiceType.SUPERMARKET, InvoiceStatus.PENDING, mockFile, 0.0, mockController);

            verify(mockController).displayMessage("Date must be within the current month.", "red");
            verify(mockConn).rollback();
        }
    }

    @Test
    public void testUploadInvoice_InvoiceAlreadyExists() throws Exception {
        Connection mockConn = mock(Connection.class);
        SubmitBillController mockController = mock(SubmitBillController.class);
        File mockFile = mock(File.class);

        try (MockedStatic<InvoiceScan> invoiceScanStatic = mockStatic(InvoiceScan.class);
             MockedStatic<Database> databaseStatic = mockStatic(Database.class, CALLS_REAL_METHODS)) {

            invoiceScanStatic.when(() -> InvoiceScan.isWithinCurrentMonth(any(LocalDate.class))).thenReturn(true);
            databaseStatic.when(() -> Database.invoiceExists(mockConn, "user1", LocalDate.now())).thenReturn(true);

            Database.uploadInvoice(mockConn, "user1", 100.0, LocalDate.now(), InvoiceType.SUPERMARKET, InvoiceStatus.PENDING, mockFile, 0.0, mockController);

            verify(mockController).displayMessage("Invoice already exists for this date!", "red");
            verify(mockConn).rollback();
        }
    }




    /*
        InvoiceScan.java
     */

    //checks the type of the invoice
    @Test
    void testDetermineInvoiceType_RestaurantOnly() {
        // Test when only restaurant is true
        InvoiceType result = InvoiceScan.determineInvoiceType(true,false);
        assertEquals(InvoiceType.SUPERMARKET, result);
    }

    //checks if it is a supermarket -> expected to be a supermarket
    @Test
    void testSupermarket (){
        String text = "HYPERMARKT\n" +
                "LandstraBer HauptstraBe 1b\n" +
                "1030 Wien 01/7140242\n" +
                "UID: ATU37198705\n" +
                "FN58299i\n" +
                "Ihr Einkauf am 13.09.2023 um 13:46 Uhr\n" +
                "EUR\n" +
                "VEGGIE FRANKFURTER 2,79 A\n" +
                "App-Joker 25% -0,70\n" +
                "VEGGI VEG.ROSTBRATW. 3,49 A\n" +
                "App-Joker 25% -0,87\n" +
                "VEGGIE VEGAN.TEMPEH 3,49 A\n" +
                "App-Joker 25% -0,87\n" +
                "TT BRATWURST VEGAN 3,49 A\n" +
                "App-Joker 25% -0,87\n" +
                "SUMME : 9,95\n" +
                "Ihre Ersparnis heute: 3,31 EUR\n" +
                "ZAHLUNG MASTERCARD 9,95";
        boolean supermarket = InvoiceScan.extractSupermarkt(text);
        assertTrue(supermarket);
    }

    //checks if it is a restaurant -> expected to be a restaurant
    @Test
    void testRestaurant () {
        String text = "\n" +
                "Rechnung Nr. :10309\n" +
                "Tisch #10\n" +
                "Speisen\n" +
                "Bob der Bauneister 19% 3,00€\n" +
                "Steak Hawal i 19% 12,00€\n" +
                "Kleine Portion\n" +
                "Steak Hubertus 3\n" +
                "Windbeute! 4 P\n" +
                "Getranke\n" +
                "| #0i2)Fabbrause 19% 2,00 €\n" +
                "0.5 Cola 19% 4,50 €\n" +
                "0.2 Saft 19% . 2.60°€\n" +
                "Total \"~ 40,60 €\n" +
                "Nettounsatz 34,12 €\n" +
                "MwSt 19% 6,48 €\n" +
                "EC 40,60 €\n" +
                "Sequenznumner: 10309\n" +
                "Datun und Zeit: 28.10.2017 18:16:31\n" +
                "10309\n" +
                "Kasse\n" +
                "E5 bediente Sie\n" +
                "KELLNER 2\n" +
                "Beehren Sie uns bald wieder";
        boolean restaurant = InvoiceScan.extractRestaurant(text);
        assertTrue(restaurant);
    }

    //extracts sum from a Invoice text
    @Test
    void testExtractSum (){
        String text = "\n" +
                "Rechnung Nr. :10309\n" +
                "Tisch #10\n" +
                "Speisen\n" +
                "Bob der Bauneister 19% 3,00€\n" +
                "Steak Hawal i 19% 12,00€\n" +
                "Kleine Portion\n" +
                "Steak Hubertus 3\n" +
                "Windbeute! 4 P\n" +
                "Getranke\n" +
                "| #0i2)Fabbrause 19% 2,00 €\n" +
                "0.5 Cola 19% 4,50 €\n" +
                "0.2 Saft 19% . 2.60°€\n" +
                "Total \"~ 40,60 €\n" +
                "Nettounsatz 34,12 €\n" +
                "MwSt 19% 6,48 €\n" +
                "EC 40,60 €\n" +
                "Sequenznumner: 10309\n" +
                "Datun und Zeit: 28.10.2017 18:16:31\n" +
                "10309\n" +
                "Kasse\n" +
                "E5 bediente Sie\n" +
                "KELLNER 2\n" +
                "Beehren Sie uns bald wieder";

            double sum = InvoiceScan.extractSum(text);
            assertEquals(40.60,sum);
    }


    @Test
    void testExtractNoSum (){
        String text = "Hallo.";

        double sum = InvoiceScan.extractSum(text);
        assertEquals(-1.0,sum);
    }

    //tests a normal day
    @Test
    void testIsWorkday_True (){
        LocalDate workday = LocalDate.of(2025,3,26); //normal workday
        assertTrue(InvoiceScan.isWorkday(workday));
    }

    //tests different days that are no workdays in austria
    @Test
    void testIsWorkday_False (){
        LocalDate noWorkday = LocalDate.of(2025,3,29); //saturday
        assertFalse(InvoiceScan.isWorkday(noWorkday));
        LocalDate noWorkday2 = LocalDate.of(2024,10,26); //national holiday in Austria
        assertFalse(InvoiceScan.isWorkday(noWorkday2));
        LocalDate noWorkday3 = LocalDate.of(2025,4,21); //easter-monday
        assertFalse(InvoiceScan.isWorkday(noWorkday3));
    }

    //checks if the invoice date is within this month
    @Test
    void testIsWithinCurrentMonth (){
        LocalDate today = LocalDate.now();
        assertTrue(InvoiceScan.isWithinCurrentMonth(today));
    }

    //checks if the method isWithinCurrentMonth with null as input
    @Test
    void testIsNotWithinCurrentMonth (){
        assertFalse(InvoiceScan.isWithinCurrentMonth(null));
    }

    @Test
    void testIsNotInTheFuture (){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertTrue (InvoiceScan.isDateInThePastOrToday(today));
        assertTrue (InvoiceScan.isDateInThePastOrToday(yesterday));
    }

    //converts different date formats into one standard format
    @Test
    void testStringtoDate (){
        String date = "24,05,25";
        assertEquals(LocalDate.of(2025,5,24),InvoiceScan.stringToDate(date));
        String date2 = "24.Mai,2025";
        assertEquals(LocalDate.of(2025,5,24),InvoiceScan.stringToDate(date));

    }

    //extracts date out of an invoice text
    @Test
    void testExtractDate (){
        String text = "\n" +
                "Rechnung Nr. :10309\n" +
                "Tisch #10\n" +
                "Speisen\n" +
                "Bob der Bauneister 19% 3,00€\n" +
                "Steak Hawal i 19% 12,00€\n" +
                "Kleine Portion\n" +
                "Steak Hubertus 3\n" +
                "Windbeute! 4 P\n" +
                "Getranke\n" +
                "| #0i2)Fabbrause 19% 2,00 €\n" +
                "0.5 Cola 19% 4,50 €\n" +
                "0.2 Saft 19% . 2.60°€\n" +
                "Total \"~ 40,60 €\n" +
                "Nettounsatz 34,12 €\n" +
                "MwSt 19% 6,48 €\n" +
                "EC 40,60 €\n" +
                "Sequenznumner: 10309\n" +
                "Datun und Zeit: 28.10.2017 18:16:31\n" +
                "10309\n" +
                "Kasse\n" +
                "E5 bediente Sie\n" +
                "KELLNER 2\n" +
                "Beehren Sie uns bald wieder";
        assertEquals("28.10.2017",InvoiceScan.extractDate(text));

        String text1 = "Hallo";
        assertEquals("Kein Datum gefunden.",InvoiceScan.extractDate(text1));
    }


    @Test
    void testisValidSum (){
        InvoiceScan.isValidSum(12.3);
    }

    //method stringToDate
    @Test
    void testStringToDateNull () {
        assertNull(InvoiceScan.stringToDate(null));
        assertNull(InvoiceScan.stringToDate(""));
    }

    //
    @Test
    void testDetermineInvoiceType (){
        assertEquals(InvoiceScan.determineInvoiceType(true,false),InvoiceType.SUPERMARKET);
        assertEquals(InvoiceScan.determineInvoiceType(true,true),InvoiceType.UNDEFINED);
        assertEquals(InvoiceScan.determineInvoiceType(false,false),InvoiceType.UNDEFINED);
        assertEquals(InvoiceScan.determineInvoiceType(false,true),InvoiceType.RESTAURANT);
    }



}
