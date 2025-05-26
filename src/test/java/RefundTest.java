import javafx.collections.ObservableList;
import jku.se.Database;
import jku.se.InvoiceType;
import jku.se.Refund;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



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



    @Test
    public void testGetRestaurant() {
        Refund refund = new Refund(LocalDate.of(2024, 12, 1), 10.5, 5.0, "admin123");
        assertEquals(10.5, refund.getRestaurant(), 0.001);
    }

    @Test
    public void testGetSupermarket() {
        Refund refund = new Refund(LocalDate.of(2024, 12, 1), 10.5, 5.0, "admin123");
        assertEquals(5.0, refund.getSupermarket(), 0.001);
    }

    @Test
    public void testGetAdmin() {
        Refund refund = new Refund(LocalDate.of(2024, 12, 1), 10.5, 5.0, "admin123");
        assertEquals("admin123", refund.getAdmin());
    }

    @Test
    void testSetDailyRefunds() throws SQLException {
        LocalDate date = LocalDate.of(2024, 7, 1);
        Refund.setDailyRefunds(7.0, 8.0, date, "testAdmin");

        // Check if inserted
        double supermarketRefund = Refund.getRefundForDate(date, "supermarket");
        double restaurantRefund = Refund.getRefundForDate(date, "restaurant");

        assertEquals(7.0, supermarketRefund);
        assertEquals(8.0, restaurantRefund);

        // Update test: Ersetze Werte
        Refund.setDailyRefunds(9.0, 10.0, date, "testAdmin2");

        supermarketRefund = Refund.getRefundForDate(date, "supermarket");
        restaurantRefund = Refund.getRefundForDate(date, "restaurant");

        assertEquals(9.0, supermarketRefund);
        assertEquals(10.0, restaurantRefund);
    }

    @Test
    void testRefundCalculation() throws SQLException {
        // Setup: In-Memory DB hat schon Daten aus setupDatabase()

        // Rechnungssumme 10.0, Typ SUPERMARKET, Datum 2024-06-10 (Nächster kleiner Eintrag: 2024-06-01, Supermarkt 4.0)
        double refund = Refund.refundCalculation(10.0, InvoiceType.SUPERMARKET, LocalDate.of(2024, 6, 10));
        assertEquals(2.5, refund);

        // Rechnungssumme kleiner als Erstattung -> Rückgabe Summe
        refund = Refund.refundCalculation(3.0, InvoiceType.RESTAURANT, LocalDate.of(2024, 6, 10));
        assertEquals(3.0, refund);

        // Null oder ungültige Werte
        assertEquals(0.0, Refund.refundCalculation(null, InvoiceType.RESTAURANT, LocalDate.now()));
        assertEquals(0.0, Refund.refundCalculation(10.0, null, LocalDate.now()));
        assertEquals(0.0, Refund.refundCalculation(10.0, InvoiceType.RESTAURANT, null));
    }

    @Test
    public void testGetRefundForDateInvalidColumnThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            Refund.getRefundForDate(LocalDate.now(), "invalid_column");
        });
    }

    @Test
    public void testGetRefund() throws SQLException {


        double supermarketRefund = Refund.getRefundSupermarket();
        double restaurantRefund = Refund.getRefundRestaurant();

        assertEquals(2.5, supermarketRefund);
        assertEquals(3.0, restaurantRefund);


    }


}


