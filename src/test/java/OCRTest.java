import javafx.application.Platform;
import jku.se.*;
import jku.se.Controller.SubmitBillController;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import static jku.se.Database.*;
import static org.junit.jupiter.api.Assertions.*;

public class OCRTest {

    // Initializes JavaFX once for all tests
    @BeforeAll
    static void initJFX() throws Exception {
        System.setProperty("java.awt.headless", "true");
        Platform.startup(() -> {});
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

    //checks if there is already an invoice uploaded for that day -> expected that an invoice already exists
    @Test
    public void testInvoiceExists_True() throws SQLException {
        File tempImage = new File("src/test/resources/testfile.jpg");
        InvoiceType typ = InvoiceType.RESTAURANT;
        InvoiceStatus status = InvoiceStatus.PENDING;
        SubmitBillController controller = new SubmitBillController();
        InvoiceScan invoiceScan = new InvoiceScan(controller);
        Connection connection= Database.getConnection();

        String username = "user";
        LocalDate date = LocalDate.now();
        uploadInvoice(connection, username, 100.50, date, typ, status, tempImage,3.0, controller);

        boolean exists = Database.invoiceExists(connection, "user", date);
        assertTrue(exists);

        Database.deleteInvoice(connection, username, date);
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

    //uploads a invoice
    @Test
    void testSuccessfulInvoiceUpload() throws Exception {
        File tempImage = new File("src/test/resources/testfile.jpg");
        InvoiceType typ = InvoiceType.RESTAURANT;
        InvoiceStatus status = InvoiceStatus.PENDING;
        SubmitBillController controller = new SubmitBillController();
        InvoiceScan invoiceScan = new InvoiceScan(controller);
        Connection connection= Database.getConnection();

        String username = "user";
        LocalDate date = LocalDate.now();
        uploadInvoice(connection, username, 100.50, date, typ, status, tempImage,3.0, controller);

        var result = connection.createStatement().executeQuery("SELECT COUNT(*) FROM rechnungen WHERE username = 'user' AND datum = '" + date.toString() + "'");
        result.next();
        assertEquals(1, result.getInt(1));

        Database.deleteInvoice(connection, username, date);
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

    //tests a normal day
    @Test
    void testIsWorkday_True (){
        LocalDate workday = LocalDate.of(2025,03,26); //normal workday
        assertTrue(InvoiceScan.isWorkday(workday));
    }

    //tests different days that are no workdays in austria
    @Test
    void testIsWorkday_False (){
        LocalDate noWorkday = LocalDate.of(2025,03,29); //saturday
        assertFalse(InvoiceScan.isWorkday(noWorkday));
        LocalDate noWorkday2 = LocalDate.of(2024,10,26); //national holiday in Austria
        assertFalse(InvoiceScan.isWorkday(noWorkday2));
        LocalDate noWorkday3 = LocalDate.of(2025,04,21); //easter-monday
        assertFalse(InvoiceScan.isWorkday(noWorkday3));
    }

    //checks if the invoice date is within this month
    @Test
    void testIsWithinCurrentMonth (){
        LocalDate today = LocalDate.now();
        assertTrue(InvoiceScan.isWithinCurrentMonth(today));
    }

    //converts different date formats into one standard format
    @Test
    void testStringtoDate (){
        String date = "24,05,25";
        assertEquals(LocalDate.of(2025,05,24),InvoiceScan.stringToDate(date));
        String date2 = "24.Mai,2025";
        assertEquals(LocalDate.of(2025,05,24),InvoiceScan.stringToDate(date));

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
    }

    @Test
    void testScanInvoice_Success() throws TesseractException, IOException, SQLException {
        // Beispiel-Testbild (sollte eine echte Rechnung sein)
        String testImagePath = "src/test/resources/testfile.jpg";

        // Scan durchführen
        SubmitBillController controller = new SubmitBillController();
        InvoiceScan invoiceScan = new InvoiceScan(controller);
        Invoice invoice = invoiceScan.scanInvoice(testImagePath);

        // Prüfen, ob Werte extrahiert wurden
        assertEquals(LocalDate.of(2023,9,13),invoice.getDate());
        assertEquals(9.95,invoice.getSum());
        assertEquals(InvoiceType.SUPERMARKET,invoice.getTyp());
    }

}
