import com.fasterxml.jackson.databind.ObjectMapper;
import jku.se.Controller.ExportDataController;
import jku.se.InvoiceStatus;
import jku.se.InvoicesTotal;
import jku.se.InvoiceExport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportDataControllerTest { //Alles von AI generiert

    private ExportDataController controller;

    @BeforeEach
    public void setUp() {
        controller = new ExportDataController();
    }

    @Test
    public void testExportInvoicesForMonth() throws Exception {
        // Benutzerinteraktion mit dem Datumsauswahlfeld simulieren
        controller = new ExportDataController();
        InvoicesTotal invoicesTotal = controller.getInvoicesForMonth(2025, 4);

        // Ergebnisse verifizieren
        assertNotNull(invoicesTotal);
    }

    @Test
    public void testExportIncludesInvoicesAndSum() throws Exception {
        // Methode aufrufen, um Rechnungen für den ausgewählten Monat zu exportieren
        InvoicesTotal invoicesTotal = controller.getInvoicesForMonth(2025, 4);

        // Überprüfen, ob die Liste der Rechnungen nicht leer ist
        assertNotNull(invoicesTotal);
        assertTrue(invoicesTotal.getInvoices().size() > 0);

        // Berechnen der Summe aller Rechnungen
        double calculatedSum = invoicesTotal.getInvoices().stream()
                .mapToDouble(invoice -> invoice.getRefund())
                .sum();

        // Vergleiche die berechnete Summe mit der totalRefund-Summe
        // delta = 0.01, d.h. der Unterschied darf maximal 0.01 betragen
        assertEquals(invoicesTotal.getTotalRefund(), calculatedSum, 0.01);
    }

    /*@Test
    public void testExportIsInJsonFormat() throws Exception {
        // Benutzerinteraktion mit dem Datumsauswahlfeld simulieren
        // Methode aufrufen, um Rechnungen für den ausgewählten Monat zu exportieren
        InvoicesTotal invoicesTotal = controller.getInvoicesForMonth(2025, 4);

        // Vorbereitungen für den Export
        File exportFile = new File("invoices-2025-04.json");

        // Methode zum Exportieren der Rechnungen aufrufen (muss eine Datei im JSON-Format erstellen)
        controller.exportInvoicesToJson(
                invoicesTotal.getInvoices(),
                invoicesTotal.getTotalRefund(),
                invoicesTotal.getRefundToPay(),
                Paths.get(exportFile.getAbsolutePath()),  // Pfad vom FileChooser
                2025, 4
        );

        // Überprüfen, ob die Datei tatsächlich erstellt wurde und die richtige Erweiterung hat
        assertTrue(exportFile.exists(), "Die Exportdatei sollte existieren");
        assertTrue(exportFile.getName().endsWith(".json"), "Die exportierte Datei sollte eine .json-Erweiterung haben");

        // Überprüfen, ob die Datei nicht leer ist
        assertTrue(exportFile.length() > 0, "Die exportierte Datei sollte nicht leer sein");
    }*/

}
