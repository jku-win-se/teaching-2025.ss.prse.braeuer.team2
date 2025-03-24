/*
import jku.se.Database;
import jku.se.invoice_typ;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    @Test
    public void connectionTest() {
        boolean success = false;
        try (Connection connection = Database.getConnection()) {
            success = true;
        } catch (SQLException e) {
            success = false;
        }
        Assertions.assertTrue(success);
    }

    @Test
    public void uploadInvoiceTest (){
        boolean success = false;
        try (Connection connection = Database.getConnection()) {
            success= true;
                    //Database.insertInvoice(connection, "User1", 19.99, "2024-03-19", invoice_typ.Restaurant, false,);
        } catch (SQLException e) {
            success = false;
        }
        Assertions.assertTrue(success);
    }
}
*/
