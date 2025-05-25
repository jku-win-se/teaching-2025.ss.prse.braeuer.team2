import jku.se.controller.ExportDataController;
import jku.se.InvoicesTotal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
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

}