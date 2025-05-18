package jku.se;

import jku.se.controller.SubmitBillController;
import net.sourceforge.tess4j.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.*;
import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/**
 * implements Tesseract-OCR
 */
public class InvoiceScan {

    /** tesseract-instance */
    private final Tesseract tesseract;
    /** controller for SubmitBillcontroller */
    private final SubmitBillController controller;
    /** Sum */
    public double sum;

    /**
     * constructor (AI)
     * @param controller
     */
    public InvoiceScan(SubmitBillController controller) {
        this.controller = controller; // Stores the passed controller in an instance variable
        tesseract = new Tesseract(); // Create new tesseract-instance
        tesseract.setTessVariable("user_defined_dpi", "70"); // Set dpi

        // Set the path for tessdata after extracting it from the JAR
        try {
            initializeOCR(); // Initialize OCR by extracting traineddata from the JAR
        } catch (IOException e) {
            e.printStackTrace();
            controller.displayMessage("Error loading Tesseract language files.", "red");
        }

        tesseract.setLanguage("deu+eng"); // Set language to German and English
    }

    private void initializeOCR() throws IOException {
        // Temp directory for tessdata
        Path tempDir = Files.createTempDirectory("tesseract-tessdata");
        Path tessdataDir = tempDir.resolve("tessdata");
        Files.createDirectories(tessdataDir);

        // Extract the German and English traineddata files from the JAR into temp directory
        extractTrainedData("/Tesseract-OCR/tessdata/deu.traineddata", tessdataDir.resolve("deu.traineddata"));
        extractTrainedData("/Tesseract-OCR/tessdata/eng.traineddata", tessdataDir.resolve("eng.traineddata"));

        // Set the datapath for Tesseract
        tesseract.setDatapath(tessdataDir.toString()); // Use the temp directory path for Tesseract

        // Set the TESSDATA_PREFIX to the directory that contains the tessdata
        System.setProperty("TESSDATA_PREFIX", tessdataDir.getParent().toString());
    }

    private void extractTrainedData(String resourcePath, Path destination) throws IOException {
        // Try to get the resource stream from the JAR
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new FileNotFoundException("Sprachdatei nicht gefunden: " + resourcePath);
            }
            // Copy the resource to the destination file
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * scans the uploaded invoice and returns a invoice (date, sum, type, status) (AI)
     * @param imagePath
     * @return Invoice(lDate, sum, type,status,refund)
     * @throws TesseractException
     * @throws IOException
     * @throws SQLException
     */
    public Invoice scanInvoice(String imagePath) throws TesseractException, IOException, SQLException {

        String text;
        // Generates a file object
        File imageFile = new File(imagePath);

        // Checks if it is a jpg and change the image so that it can be read by the OCR
        if (imagePath.toLowerCase().endsWith(".jpg")) {
            // Read the image file into a BufferedImage
            BufferedImage bufferedImage = ImageIO.read(imageFile);

            // Re-save the image to fix potential issues, working directly in memory
            BufferedImage newBufferedImage = new BufferedImage(
                    bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Draw the original image onto the new BufferedImage
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, null);

            // Pass the in-memory BufferedImage to the OCR engine
            text = tesseract.doOCR(newBufferedImage);
        } else {
            // Tesseract is processing the invoice to a text
            text = tesseract.doOCR(imageFile);
        }

        // Extract data out of the extracted text (with methods)
        String date = extractDate(text);
        LocalDate lDate = stringToDate(date);
        sum = extractSum(text);
        boolean supermarkt = extractSupermarkt(text);
        boolean restaurant = extractRestaurant(text);
        InvoiceType type = determineInvoiceType(supermarkt, restaurant);

        // If OCR could find all needed parameters the invoice is accepted automatically
        // Otherwise, it is on status pending -> must be checked by an admin
        InvoiceStatus status;
        if (type != InvoiceType.UNDEFINED && isValidSum(sum) && isWithinCurrentMonth(lDate)) {
            status = InvoiceStatus.ACCEPTED;
        } else {
            status = InvoiceStatus.PENDING;
        }

        // If sum is not correct, display error message and prompt users to enter it manually
        if (!isValidSum(sum)) {
            controller.displayMessage("Amount could not be read.", "red");
            sum = controller.requestManualSum();
        }

        // If date is null, display error message and prompt users to enter it manually
        if (lDate == null) {
            controller.displayMessage("Date could not be read.", "red");
            date = controller.requestManualDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            lDate = stringToDate(date);
        }

        // If date is not within the current month, display error message
        if (!isWithinCurrentMonth(lDate)) {
            controller.displayMessage("Date must be within the current month.", "red");
        }

        // If type is UNDEFINED, invoice could not be categorized
        if (type == InvoiceType.UNDEFINED) {
            controller.displayMessage("Type could not be recognized.", "red");
            type = controller.requestManualType();
        }

        Invoice invoice = controller.requestManualAll(lDate, sum, type, status);
        if (invoice == null) {
            return null;
        }

        lDate = invoice.getDate();
        sum = invoice.getSum();
        type = invoice.getTyp();
        status = invoice.getStatus();

        // Calculate refund
        double refund = Refund.refundCalculation(sum, type, lDate);
        return new Invoice(lDate, sum, type, status, refund);
    }

    /*
        Type
     */

    /**
     * checks if the type could be clearly identified
     * @param supermarkt
     * @param restaurant
     * @return
     */
    public static InvoiceType determineInvoiceType(boolean supermarkt, boolean restaurant) {
        if (!supermarkt && !restaurant) {
            return InvoiceType.UNDEFINED; // if neither a supermarket nor a restaurant was found
        } else if (supermarkt && restaurant) {
            return InvoiceType.UNDEFINED; // if both types were found
        } else if (supermarkt) {
            return InvoiceType.SUPERMARKET; // if supermarkt was found
        } else {
            return InvoiceType.RESTAURANT; // if restaurant was found
        }
    }

    /**
     * checks if it is a restaurant from the extracted OCR-text (AI)
     * @param text
     * @return true if a match was found, else false
     */
    public static boolean extractRestaurant(String text) {
        Pattern pattern = Pattern.compile("\\b(Restaurant|RESTAURANT|Tisch|KELLNER|bediente|Mensa)\\b");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * checks if it is a supermarkt from the extracted OCR-text (AI)
     * @param text
     * @return true if a match was found, else false
     */
    public static boolean extractSupermarkt(String text) {
        Pattern pattern = Pattern.compile("\\b(Spar|Ihr Einkauf|Hofer|HOFER|Lidl|Billa|jö)\\b");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }

    /*
        Date
     */

    /**
     * finds the date from the extracted OCR-text (AI)
     * @param text
     * @return date
     */
    public static String extractDate(String text) {

        // Regular expression for different date formats
        Pattern pattern = Pattern.compile(
                "\\b(\\d{1,2}[./,-](?:\\d{1,2}|[a-zA-Z]+)[./,-](?:\\d{2}|\\d{4}))\\b"
        );

        Matcher matcher = pattern.matcher(text); //searches the date based on the pattern

        if (matcher.find()) {
            return matcher.group(1); // returns the found date
        } else {
            return "Kein Datum gefunden.";
        }
    }

    /**
     * converts the different types of dates to a LocalDate (AI)
     * @param dateString
     * @return date in the correct format
     */
    public static LocalDate stringToDate(String dateString) {

        // if String is null, date is null
        if (dateString == null) {
            return null;
        }

        // Replace commas with points
        String dateStr = dateString.replace(',', '.');

        // List with possible date types
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("d.M.yyyy"),
                DateTimeFormatter.ofPattern("d.M.yy"),
                DateTimeFormatter.ofPattern("d.MMMM.yyyy", Locale.GERMAN),
                DateTimeFormatter.ofPattern("d.MMMM.yy", Locale.GERMAN)
        );

        // Goes through the list and tries to format the date
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {}
        }

        // If the passed date does not match one of the formats;
        return null;
    }

    //checks if the found/inserted date is a workday in austria (AI)
    public static boolean isWorkday(LocalDate date) {

        //get all the austrian holidays for the current year and save them in a set
        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.AUSTRIA);
        Set<Holiday> holidays = manager.getHolidays(date.getYear());

        // Checks if the date is a Saturday or Sunday
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        //Checks if the date is a holiday
        for (Holiday holiday : holidays) {
            if (holiday.getDate().equals(date)) {
                return false;
            }
        }

        // if it is no holiday or sunday or saturday, it must be a work day
        return true;
    }

    //checks if the invoice date is within the current month (AI)
    public static boolean isWithinCurrentMonth(LocalDate date) {
        if (date == null) {
            return false; // Falls das Datum null ist, geben wir false zurück.
        }
        LocalDate today = LocalDate.now(); // get today's date
        return date.getYear() == today.getYear() && date.getMonth() == today.getMonth();
    }

    public static boolean isDateInThePastOrToday(LocalDate date) {
        // Überprüft, ob das Datum in der Vergangenheit oder heute liegt
        return !date.isAfter(LocalDate.now());
    }

    /*
        SUM
     */

    //finds the sum from the extracted OCR-text (AI)
    public static Double extractSum(String text) {
        Pattern pattern = Pattern.compile("(SUMME|PREIS|Preis|Summe|Total|zahlen|summe|Sunne|Mastercard|Betrag:|EC)" +
                "\\s*(EUR|€|:)?\\s*(\\d{1,3}[.,]\\d{2})");

        Matcher matcher = pattern.matcher(text);
        List<Double> betraege = new ArrayList<>();

        while (matcher.find()) {
            double betrag = Double.parseDouble(matcher.group(3).replace(",", "."));
            betraege.add(betrag);
        }

        return betraege.isEmpty() ? -1.0 : Collections.max(betraege);
    }


    /**
     * checks if the amount is correct
     * @param amount
     * @return true, if it is a correct sum, else false
     */
    private boolean isValidSum(Double amount) {
        return amount != null && amount >= 0;
    }


}
