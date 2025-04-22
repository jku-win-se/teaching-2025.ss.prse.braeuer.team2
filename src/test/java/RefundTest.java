import javafx.collections.ObservableList;
import jku.se.Controller.RefundController;
import jku.se.Database;
import jku.se.InvoiceType;
import jku.se.Refund;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RefundTest {

    //checks if the correct supermarkt refund is stored correct
    @Test
    void GetRefundSupermarket() {
        try (Connection con = Database.getConnection();){
            Refund.setDailyRefunds(2.5,3, LocalDate.of(1999,01,01),"user");
            double sup = Refund.getRefundForDate (LocalDate.of(1999,01,01),"supermarket" );
            assertEquals(sup, 2.5);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //checks if the correct restaurant refund is stored correct
    @Test
    void GetRefundRestaurant() {
        try (Connection con = Database.getConnection();){
            Refund.setDailyRefunds(2.5,3, LocalDate.of(1999,01,01),"user");
            double sup = Refund.getRefundForDate (LocalDate.of(1999,01,01),"restaurant" );
            assertEquals(sup, 3.0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //checks the method for the table
    @Test
    void GetRefund() throws SQLException {
        try {
            ObservableList<Refund> refund = Refund.getAllRefunds();
            int count = refund.size();
            assertTrue(count>=1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //invoice amount is bigger than supermarkt refund
    @Test
    void CalculateRefundSupermarktBigger () throws SQLException {
        double refund = Refund.refundCalculation(20.0, InvoiceType.SUPERMARKET, LocalDate.of(1999,01,01));
        assertEquals(refund, 2.5);
    }

    //invoice amount is lower than supermarkt refund
    @Test
    void CalculateRefundSupermarktLower () throws SQLException {
        double refund = Refund.refundCalculation(2.0, InvoiceType.SUPERMARKET, LocalDate.of(1999,01,01));
        assertEquals(refund, 2.0);
    }

    //invoice amount is bigger than supermarkt refund
    @Test
    void CalculateRefundRestaurantBigger () throws SQLException {
        double refund = Refund.refundCalculation(20.0, InvoiceType.RESTAURANT, LocalDate.of(1999,01,01));
        assertEquals(refund, 3.0);
    }

    //invoice amount is lower than supermarkt refund
    @Test
    void CalculateRefundRestaurantLower () throws SQLException {
        double refund = Refund.refundCalculation(2.0, InvoiceType.RESTAURANT, LocalDate.of(1999,01,01));
        assertEquals(refund, 2.0);
    }

}
