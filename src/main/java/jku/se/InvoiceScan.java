package jku.se;

import jku.se.Controller.SubmitBillController;
import net.sourceforge.tess4j.*;

import java.io.File;
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


public class InvoiceScan {
    private static boolean dateOk = true;
    private final Tesseract tesseract;
    private SubmitBillController controller;

    public double sum;


    public InvoiceScan(SubmitBillController controller) {
        this.controller = controller;
        tesseract = new Tesseract();
        tesseract.setTessVariable("user_defined_dpi", "70");
        tesseract.setDatapath("src/main/resources/Tesseract-OCR/tessdata");
        tesseract.setLanguage("deu+eng");
    }


    public Invoice scanInvoice(String imagePath) throws TesseractException {
        File imageFile = new File(imagePath);
        String text = tesseract.doOCR(imageFile);

        //Ausgabe des Textes
        //System.out.println(text);

        // Extrahierte Daten aus dem Text
        String date = extractDate(text);
        sum = extractSum(text);
        boolean supermarkt = extractSupermarkt(text);
        boolean restaurant = extractRestaurant(text);
        InvoiceType type = determineInvoiceType(supermarkt, restaurant);
        //Scanner scanner = new Scanner(System.in);

        LocalDate cDate = stringToDate (date);


        InvoiceStatus status;

        if (type != InvoiceType.UNDEFINED && isValidSum(sum) && isValidDate(date) && isWithinCurrentMonth(cDate)) {
            status = InvoiceStatus.ACCEPTED;
        }
        else{
            status = InvoiceStatus.PENDING;
        }

        if (!isValidSum(sum)) {
            controller.displayMessage("Betrag konnte nicht gelesen werden.", "red");
            sum = controller.requestManualSum();
            System.out.println("Manuell eingegebener Betrag: " + sum);
        }


        if (!isValidDate(date)) {
            controller.displayMessage("Datum konnte nicht gelesen werden.", "red");
            date = controller.requestManualDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            System.out.println("Manuell eingegebenes Datum: " + date);
        }

        LocalDate lDate = stringToDate (date);

        if (!isWithinCurrentMonth(lDate)) {
            controller.displayMessage("Datum muss innerhalb des aktuellen Monats liegen.", "red");
        }

        if (type == InvoiceType.UNDEFINED){
            controller.displayMessage("Typ konnte nicht erkannt werden.", "red");
            type = controller.requestManualType();
        }



        // Rückgabe einer Invoice mit den extrahierten Informationen
        System.out.println(text);
        System.out.println(lDate);
        System.out.println(sum);
        System.out.println(type);
        return new Invoice(lDate, sum, type,status);
    }

    private InvoiceType determineInvoiceType(boolean supermarkt, boolean restaurant) {
        if (!supermarkt && !restaurant) {
            return InvoiceType.UNDEFINED; // Wenn weder Supermarkt noch Restaurant gefunden wurden
        } else if (supermarkt) {
            return InvoiceType.SUPERMARKET; // Wenn Supermarkt gefunden wurde
        } else {
            return InvoiceType.RESTAURANT; // Wenn Restaurant gefunden wurde
        }
    }

    private boolean extractRestaurant(String text) {
        Pattern pattern = Pattern.compile("\\b(Restaurant|RESTAURANT|Tisch|KELLNER|bediente)\\b");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }

    private boolean extractSupermarkt(String text) {
        Pattern pattern = Pattern.compile("\\b(Spar|Ihr Einkauf|Hofer|HOFER|Lidl|Billa|jö)\\b");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }

    private String extractDate(String text) {
        Pattern pattern = Pattern.compile("\\b(\\d{2}[./,-]\\d{2}[./,-](\\d{2}|\\d{4}))\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1); // Gibt das gefundene Datum zurück
        }
        else{
            dateOk=false;
            return "Kein Datum gefunden.";
        }

    }

    public static LocalDate stringToDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Ungültiges Datum: " + dateStr);
            return null;
        }
    }

    public static boolean isWorkday(LocalDate date) {
        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.AUSTRIA);
        Set<Holiday> holidays = manager.getHolidays(date.getYear());

        // Prüfen, ob das Datum ein Samstag oder Sonntag ist
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            System.out.println("Kein Arbeitstag");
            return false;
        }

        // Prüfen, ob das Datum ein Feiertag ist
        for (Holiday holiday : holidays) {
            if (holiday.getDate().equals(date)) {
                System.out.println("Kein Arbeitstag");
                return false; // Kein Arbeitstag, da es ein Feiertag ist
            }
        }
        //System.out.println("Arbeitstag");
        return true; // Wenn kein Feiertag und kein Wochenende, dann Arbeitstag
    }

    private static boolean isValidDate(String datum) {
        // Überprüft, ob das Datum dem Format TT.MM.JJJJ entspricht
        return datum.matches("\\d{2}\\.\\d{2}\\.\\d{4}");
    }

    public static boolean isWithinCurrentMonth(LocalDate date) {
        if (date == null) {
            return false; // Falls das Datum null ist, geben wir false zurück.
        }

        LocalDate today = LocalDate.now(); // Heutiges Datum holen
        return date.getYear() == today.getYear() && date.getMonth() == today.getMonth();
    }

    private Double extractSum(String text) {
        Pattern pattern = Pattern.compile("(?:SUMME|PREIS|Preis|Summe|Total|zahlen)\\s*(?:EUR|€|:)?\\s*(\\d{1,3}(?:[.,]\\d{2}))");

        Matcher matcher = pattern.matcher(text);

        List<Double> betraege = new ArrayList<>();

        while (matcher.find()) {
            // Zahl als Double speichern (Komma durch Punkt ersetzen für Parsing)
            double betrag = Double.parseDouble(matcher.group(1).replace(",", "."));
            betraege.add(betrag);
        }

        if (!betraege.isEmpty()) {
            double summe = Collections.max(betraege); // Höchsten Wert als Gesamtsumme nehmen
            return summe;
        } else {
            return -1.0;
        }
    }

    private boolean isValidSum(Double amount) {
        return amount != null && amount >= 0;
    }
}


