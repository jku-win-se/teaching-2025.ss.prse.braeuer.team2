import javafx.collections.ObservableList;
import jku.se.Controller.RefundController;
import jku.se.Database;
import jku.se.InvoiceType;
import jku.se.Refund;
import org.junit.jupiter.api.Assertions;
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
            Refund.setDailyRefunds(2.5,3, LocalDate.of(1999,1,1),"user");
            double sup = Refund.getRefundForDate (LocalDate.of(1999,1,1),"supermarket" );
            assertEquals(2.5, sup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //checks if the correct restaurant refund is stored correct
    @Test
    void GetRefundRestaurant() {
        try (Connection con = Database.getConnection();){
            Refund.setDailyRefunds(2.5,3, LocalDate.of(1999,1,1),"user");
            double sup = Refund.getRefundForDate (LocalDate.of(1999,1,1),"restaurant" );
            assertEquals(3.0, sup);
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
            Assertions.assertTrue(count>=1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //invoice amount is bigger than supermarkt refund
    @Test
    void CalculateRefundSupermarktBigger () throws SQLException {
        double refund = Refund.refundCalculation(20.0, InvoiceType.SUPERMARKET, LocalDate.of(1999,1,1));
        assertEquals(2.5, refund);
    }

    //invoice amount is lower than supermarkt refund
    @Test
    void CalculateRefundSupermarktLower () throws SQLException {
        double refund = Refund.refundCalculation(2.0, InvoiceType.SUPERMARKET, LocalDate.of(1999,1,1));
        assertEquals(2.0, refund);
    }

    //invoice amount is bigger than supermarkt refund
    @Test
    void CalculateRefundRestaurantBigger () throws SQLException {
        double refund = Refund.refundCalculation(20.0, InvoiceType.RESTAURANT, LocalDate.of(1999,1,1));
        assertEquals(3.0, refund);
    }

    //invoice amount is lower than supermarkt refund
    @Test
    void CalculateRefundRestaurantLower () throws SQLException {
        double refund = Refund.refundCalculation(2.0, InvoiceType.RESTAURANT, LocalDate.of(1999,1,1));
        assertEquals(2.0, refund);
    }

}
