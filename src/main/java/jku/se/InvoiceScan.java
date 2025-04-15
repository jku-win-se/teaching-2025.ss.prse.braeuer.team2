package jku.se;

import jku.se.Controller.SubmitBillController;
import net.sourceforge.tess4j.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.*;
import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;


public class InvoiceScan {

    //variables
    private final Tesseract tesseract;
    private SubmitBillController controller;
    public double sum;

    //constructor (AI)
    public InvoiceScan(SubmitBillController controller) {
        this.controller = controller; //Stores the passed controller in an instance variable
        tesseract = new Tesseract(); //create new tesseract-instance
        tesseract.setTessVariable("user_defined_dpi", "70"); //set dpi
        tesseract.setDatapath("src/main/resources/Tesseract-OCR/tessdata"); //datapath to tesseract
        tesseract.setLanguage("deu+eng"); //set language to german and englisch
    }

    //scans the uploaded invoice and returns a invoice (date, sum, type, status) (AI)
    public Invoice scanInvoice(String imagePath) throws TesseractException, IOException, SQLException {

        String text;
        //generates a file object
        File imageFile = new File(imagePath);

        //checks if it is a jpg and change the image so that it can be read by the OCR
        if (imagePath.toLowerCase().endsWith(".jpg")){

            // Read the image file into a BufferedImage
            BufferedImage bufferedImage = ImageIO.read(imageFile);

            // Re-save the image to fix potential issues, working directly in memory
            BufferedImage newBufferedImage = new BufferedImage(
                    bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Draw the original image onto the new BufferedImage
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, null);

            // Pass the in-memory BufferedImage to the OCR engine
            text = tesseract.doOCR(newBufferedImage);
        }
        else{
            //Tesseract is processing the invoice to a text
            text = tesseract.doOCR(imageFile);
        }



        //output of the text
        System.out.println(text);

        // extract data out of the extracted text (with methods)
        String date = extractDate(text);
        LocalDate lDate = stringToDate (date);
        sum = extractSum(text);
        boolean supermarkt = extractSupermarkt(text);
        boolean restaurant = extractRestaurant(text);
        InvoiceType type = determineInvoiceType(supermarkt, restaurant);

        //if OCR could find all needed parameters the invoice is accepted automatically
        //otherwise it is on status pending -> must be checked by an admin
        InvoiceStatus status;
        if (type != InvoiceType.UNDEFINED && isValidSum(sum) && isWithinCurrentMonth(lDate)) {
            status = InvoiceStatus.ACCEPTED;
        }
        else{
            status = InvoiceStatus.PENDING;
        }

        //if sum is not correct, display error message and prompt users to enter it manually
        if (!isValidSum(sum)) {
            controller.displayMessage("Betrag konnte nicht gelesen werden.", "red");
            sum = controller.requestManualSum();
            System.out.println("Manuell eingegebener Betrag: " + sum);
        }

        //if date is null, display error message and prompt users to enter it manually
        if (lDate == null) {
            controller.displayMessage("Datum konnte nicht gelesen werden.", "red");
            date = controller.requestManualDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            lDate = stringToDate (date);
            System.out.println("Manuell eingegebenes Datum: " + lDate);
        }

        //if date is not within the current month, display error message
        if (!isWithinCurrentMonth(lDate)) {
            controller.displayMessage("Datum muss innerhalb des aktuellen Monats liegen.", "red");
        }

        //if type is UNDEFINED, invoice could not be categorized
        if (type == InvoiceType.UNDEFINED){
            controller.displayMessage("Typ konnte nicht erkannt werden.", "red");
            type = controller.requestManualType();
        }

        Invoice invoice = controller.requestManualAll(lDate,sum,type, status);
        if (invoice == null){
            return null;
        }
        lDate = invoice.getDate();
        sum = invoice.getSum();
        type = invoice.getTyp();
        status = invoice.getStatus();

        //calculate refund
        double refund = Refund.refundCalculation(sum,type, lDate);


        // Return an invoice with the extracted information
        System.out.println(text);
        System.out.println(lDate);
        System.out.println(sum);
        System.out.println(type);
        System.out.println(refund);
        return new Invoice(lDate, sum, type,status,refund);
    }



    /*
        Type
    */

    //checks if the type could be clearly identified
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

    //checks if it is a restaurant from the extracted OCR-text (AI)
    public static boolean extractRestaurant(String text) {
        Pattern pattern = Pattern.compile("\\b(Restaurant|RESTAURANT|Tisch|KELLNER|bediente|Mensa)\\b");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    //checks if it is a supermarket from the extracted OCR-text (AI)
    public static boolean extractSupermarkt(String text) {
        Pattern pattern = Pattern.compile("\\b(Spar|Ihr Einkauf|Hofer|HOFER|Lidl|Billa|jö)\\b");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }


    /*
        Date
    */

    //finds the date from the extracted OCR-text (AI)
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

    //converts the different types of dates to a LocalDate (AI)
    public static LocalDate stringToDate(String dateStr) {

        // if String is null, date is null
        if (dateStr == null) {
            return null;
        }

        // Replace commas with points
        dateStr = dateStr.replace(',', '.');

        //list with possible date types
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("d.M.yyyy"),
                DateTimeFormatter.ofPattern("d.M.yy"),
                DateTimeFormatter.ofPattern("d.MMMM.yyyy", Locale.GERMAN),
                DateTimeFormatter.ofPattern("d.MMMM.yy", Locale.GERMAN)
        );

        //goes through the list and trys to format the date
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {}
        }

        //if the passed date does not match one of the formats;
        System.out.println("Ungültiges Datum: " + dateStr);
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
            System.out.println("Kein Arbeitstag");
            return false;
        }

        //Checks if the date is a holiday
        for (Holiday holiday : holidays) {
            if (holiday.getDate().equals(date)) {
                System.out.println("Kein Arbeitstag");
                return false;
            }
        }

        // if it is no holiday or sunday or saturday, it must be a work day
        System.out.println("Arbeitstag");
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
        Pattern pattern = Pattern.compile("(?:SUMME|PREIS|Preis|Summe|Total|zahlen|summe|Sunne|Mastercard|Betrag:|EC)" +
                "\\s*(?:EUR|€|:)?\\s*(\\d{1,3}(?:[.,]\\d{2}))");
        // first bracket musst be a match; second bracket optional; than there is the sum

        Matcher matcher = pattern.matcher(text); //searches the sum based on the pattern

        //if there are found more than one sum, the highest double is the correct one
        List<Double> betraege = new ArrayList<>();
        while (matcher.find()) {
            // Save number as double (replace comma with period for parsing)
            double betrag = Double.parseDouble(matcher.group(1).replace(",", "."));
            betraege.add(betrag);
        }
        if (!betraege.isEmpty()) {
            double summe = Collections.max(betraege); //find highest number
            return summe;
        } else {
            return -1.0; //if the sum couldn't be found return -1 (is invalid Sum)
        }
    }

    //checks if the amount is correct
    private boolean isValidSum(Double amount) {
        return amount != null && amount >= 0;
    }


}


