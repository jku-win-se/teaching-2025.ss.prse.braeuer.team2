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
    void testSetters() {
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


}

