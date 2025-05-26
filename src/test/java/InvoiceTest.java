import jku.se.Invoice;
import jku.se.InvoiceExport;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
public class InvoiceTest {//AI generated
    @Test
    void testGettersAndSetters() {
        InvoiceExport invoice = new InvoiceExport(
                LocalDate.of(2025, 5, 25),
                150.0,
                InvoiceType.RESTAURANT,
                InvoiceStatus.PENDING,
                3.5,
                101,
                "carina");

        assertEquals(LocalDate.of(2025, 5, 25), invoice.getDate());
        assertEquals(150.0, invoice.getSum());
        assertEquals(InvoiceType.RESTAURANT, invoice.getTyp());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
        assertEquals(3.5, invoice.getRefund());
        assertEquals(101, invoice.getId());
        assertEquals("carina", invoice.getUser());
    }

    @Test
    void testExportSetters() {
        InvoiceExport invoice = new InvoiceExport(
                LocalDate.now(), 0, InvoiceType.RESTAURANT, InvoiceStatus.PENDING, 0, 0, "");

        invoice.setSum(199.99);
        invoice.setTyp(InvoiceType.SUPERMARKET);
        invoice.setStatus(InvoiceStatus.DENIED);
        invoice.setRefund(7.0);
        invoice.setId(999);
        invoice.setUser("user");

        assertEquals(199.99, invoice.getSum());
        assertEquals(InvoiceType.SUPERMARKET, invoice.getTyp());
        assertEquals(InvoiceStatus.DENIED, invoice.getStatus());
        assertEquals(7.0, invoice.getRefund());
        assertEquals(999, invoice.getId());
        assertEquals("user", invoice.getUser());
    }

    @Test
    void testGetRefundToPayWhenAccepted() {
        InvoiceExport invoice = new InvoiceExport(
                LocalDate.now(), 100, InvoiceType.RESTAURANT, InvoiceStatus.ACCEPTED, 5.0, 1, "user");
        assertEquals(5.0, invoice.getRefundToPay());
    }

    @Test
    void testGetRefundToPayWhenNotAccepted() {
        InvoiceExport invoice = new InvoiceExport(
                LocalDate.now(), 100, InvoiceType.RESTAURANT, InvoiceStatus.DENIED, 5.0, 1, "user");
        assertEquals(0.0, invoice.getRefundToPay());
    }

    @Test
    public void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        double sum = 150.75;
        InvoiceType type = InvoiceType.SUPERMARKET;
        InvoiceStatus status = InvoiceStatus.ACCEPTED;
        double refund = 30.0;

        Invoice invoice = new Invoice(date, sum, type, status, refund);

        assertEquals(date, invoice.getDate());
        assertEquals(sum, invoice.getSum());
        assertEquals(type, invoice.getTyp());
        assertEquals(status, invoice.getStatus());
        assertEquals(refund, invoice.getRefund());
    }

    @Test
    public void testSetters() {
        Invoice invoice = new Invoice(LocalDate.now(), 0, null, null, 0);

        LocalDate newDate = LocalDate.of(2025, 1, 1);
        invoice.setDate(newDate);
        assertEquals(newDate, invoice.getDate());

        invoice.setSum(200.0);
        assertEquals(200.0, invoice.getSum());

        invoice.setTyp(InvoiceType.RESTAURANT);
        assertEquals(InvoiceType.RESTAURANT, invoice.getTyp());

        invoice.setStatus(InvoiceStatus.PENDING);
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());

        invoice.setRefund(50.0);
        assertEquals(50.0, invoice.getRefund());
    }

    @Test
    public void testToString() {
        Invoice invoice = new Invoice(
                LocalDate.of(2024, 12, 1),
                123.45,
                InvoiceType.SUPERMARKET,
                InvoiceStatus.ACCEPTED,
                0.0
        );

        String expected = "Rechnung [Datum=2024-12-01, Summe=123.45 EUR, Typ=SUPERMARKET]";
        assertEquals(expected, invoice.toString());
    }

}

