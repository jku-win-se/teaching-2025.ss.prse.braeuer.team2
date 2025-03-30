import jku.se.InvoiceType;
import jku.se.Refund;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RefundTest {

    private Refund refund = new Refund();

    @Test
    public void testCalculateSupermarketRefund_maxRefund() throws SQLException {
        double ref = refund.getRefundSupermarket();
        refund.setRefundSupermarket(2.5);
        double refundC = Refund.refundCalculation(23.0, InvoiceType.SUPERMARKET);
        assertEquals(2.5,refundC);
        refund.setRefundSupermarket(ref);
    }

    @Test
    public void testCalculateSupermarketRefund_sum() throws SQLException {
        double ref = refund.getRefundSupermarket();
        refund.setRefundSupermarket(4);
        double refundC = Refund.refundCalculation(3.5, InvoiceType.SUPERMARKET);
        assertEquals(3.5,refundC);
        refund.setRefundSupermarket(ref);
    }

    @Test
    public void testCalculateSupermarketRefund_refundIsSum() throws SQLException {
        double ref = refund.getRefundSupermarket();
        refund.setRefundSupermarket(2.5);
        double refundC = Refund.refundCalculation(2.5, InvoiceType.SUPERMARKET);
        assertEquals(2.5,refundC);
        refund.setRefundSupermarket(ref);
    }

    @Test
    void testRefundSupermarket() throws SQLException {
        double ref = refund.getRefundSupermarket();
        refund.setRefundSupermarket(5);
        double result = refund.getRefundSupermarket();
        refund.setRefundSupermarket(ref);
        assertEquals(5, result);
    }


    @Test
    public void testCalculateRestaurantRefund_maxRefund() throws SQLException {
        double ref = refund.getRefundRestaurant();
        refund.setRefundRestaurant(2.5);
        double refundC = Refund.refundCalculation(23.0, InvoiceType.RESTAURANT);
        assertEquals(2.5, refundC);
        refund.setRefundRestaurant(ref);
    }

    @Test
    public void testCalculateRestaurantRefund_sum() throws SQLException {
        double ref = refund.getRefundRestaurant();
        refund.setRefundRestaurant(5);
        double refundC = Refund.refundCalculation(1.96, InvoiceType.RESTAURANT);
        assertEquals(1.96, refundC);
        refund.setRefundRestaurant(ref);
    }

    @Test
    public void testCalculateRestaurantRefund_refundIsSum() throws SQLException {
        double ref = refund.getRefundRestaurant();
        refund.setRefundRestaurant(5);
        double refundC = Refund.refundCalculation(5.0, InvoiceType.RESTAURANT);
        assertEquals(5, refundC);
        refund.setRefundRestaurant(ref);
    }

    @Test
    void testRefundRestaurant() throws SQLException {
        double ref = refund.getRefundRestaurant();
        refund.setRefundRestaurant(5);
        double result = refund.getRefundRestaurant();
        refund.setRefundRestaurant(ref);
        assertEquals(5, result);
    }


}
