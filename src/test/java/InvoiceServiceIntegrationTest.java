import jku.se.Database;
import jku.se.InvoiceType;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class InvoiceServiceIntegrationTest {

    private static final String TEST_USERNAME = "TestFilter";
    private Connection conn;

    @BeforeEach
    public void setup() throws SQLException {
        conn = Database.getConnection();
        conn.setAutoCommit(false);

        // Testdaten einfügen
        String sqlInsert = "INSERT INTO rechnungen (id, username, betrag, datum, typ, status, refund) " +
                "VALUES (?, ?, ?, ?, ?::invoicetype, ?::invoicestatus, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            insertTestData(pstmt, 1, 68.90, "2025-03-29", InvoiceType.RESTAURANT, "PENDING", 0.0);
            insertTestData(pstmt, 2, 19.95, "2025-03-28", InvoiceType.SUPERMARKET, "ACCEPTED", 0.0);
            insertTestData(pstmt, 3, 59.10, "2025-03-27", InvoiceType.RESTAURANT, "APPROVED", 0.0);
            insertTestData(pstmt, 4, 23.45, "2025-03-26", InvoiceType.SUPERMARKET, "ACCEPTED", 0.0);
            insertTestData(pstmt, 5, 15.00, "2025-03-25", InvoiceType.RESTAURANT, "APPROVED", 0.0);
            insertTestData(pstmt, 6, 72.30, "2025-03-24", InvoiceType.SUPERMARKET, "ACCEPTED", 0.0);
            insertTestData(pstmt, 7, 49.99, "2025-03-23", InvoiceType.SUPERMARKET, "ACCEPTED", 0.0);
        }
    }

    private void insertTestData(PreparedStatement pstmt, int id, double amount, String date,
                                InvoiceType type, String status, double refund) throws SQLException {
        pstmt.setInt(1, id);
        pstmt.setString(2, TEST_USERNAME);
        pstmt.setDouble(3, amount);
        pstmt.setDate(4, Date.valueOf(date));
        pstmt.setString(5, type.name()); // Convert enum to string
        pstmt.setString(6, status);
        pstmt.setDouble(7, refund);
        pstmt.executeUpdate();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        conn.rollback();
        conn.close();
    }

    @Test
    public void testBuildQuery_NoFilters() {
        String[] filters = new String[]{null, null, null, null, null};
        String query = buildQuery(filters);

        assertEquals("SELECT id, betrag, datum, typ, username, status, image FROM rechnungen", query);
    }

    @Test
    public void testBuildQuery_IdFilter() {
        String[] filters = new String[]{"I01", null, null, null, null};
        String query = buildQuery(filters);

        assertEquals("SELECT id, betrag, datum, typ, username, status, image FROM rechnungen WHERE id = ?", query);
    }

    @Test
    public void testBuildQuery_TypeFilter() {
        String[] filters = new String[]{null, "RESTAURANT", null, null, null};
        String query = buildQuery(filters);

        assertEquals("SELECT id, betrag, datum, typ, username, status, image FROM rechnungen WHERE typ::text LIKE ?", query);
    }

    @Test
    public void testBuildQuery_UsernameFilter() {
        String[] filters = new String[]{null, null, "TestFilter", null, null};
        String query = buildQuery(filters);

        assertEquals("SELECT id, betrag, datum, typ, username, status, image FROM rechnungen WHERE username LIKE ?", query);
    }

    @Test
    public void testBuildQuery_StatusFilter() {
        String[] filters = new String[]{null, null, null, "ACCEPTED", null};
        String query = buildQuery(filters);

        assertEquals("SELECT id, betrag, datum, typ, username, status, image FROM rechnungen WHERE status::text = ?", query);
    }

    @Test
    public void testBuildQuery_CurrentMonthFilter() {
        String[] filters = new String[]{null, null, null, null, "true"};
        String query = buildQuery(filters);

        String expected = "SELECT id, betrag, datum, typ, username, status, image FROM rechnungen WHERE " +
                "EXTRACT(YEAR FROM datum) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                "AND EXTRACT(MONTH FROM datum) = EXTRACT(MONTH FROM CURRENT_DATE)";
        assertEquals(expected, query);
    }

    @Test
    public void testBuildQuery_CombinedFilters() {
        String[] filters = new String[]{"I01", "SUPERMARKET", "TestFilter", "ACCEPTED", "true"};
        String query = buildQuery(filters);

        String expected = "SELECT id, betrag, datum, typ, username, status, image FROM rechnungen WHERE id = ? AND typ::text LIKE ? AND username LIKE ? AND status::text = ? AND " +
                "EXTRACT(YEAR FROM datum) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                "AND EXTRACT(MONTH FROM datum) = EXTRACT(MONTH FROM CURRENT_DATE)";
        assertEquals(expected, query);
    }



    // Hilfsmethode aus der zu testenden Klasse
    private boolean notEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // Die zu testende Methode
    private String buildQuery(String[] filters) {
        String query = "SELECT id, betrag, datum, typ, username, status, image FROM rechnungen";
        boolean hasWhere = false;

        if (notEmpty(filters[0])) {
            query += " WHERE id = ?";
            hasWhere = true;
        }

        if (notEmpty(filters[1])) {
            query += hasWhere ? " AND typ::text LIKE ?" : " WHERE typ::text LIKE ?";
            hasWhere = true;
        }

        if (notEmpty(filters[2])) {
            query += hasWhere ? " AND username LIKE ?" : " WHERE username LIKE ?";
            hasWhere = true;
        }

        if (notEmpty(filters[3])) {
            query += hasWhere ? " AND status::text = ?" : " WHERE status::text = ?";
            hasWhere = true;
        }

        if (notEmpty(filters[4])) {
            query += hasWhere ? " AND " : " WHERE ";
            query += "EXTRACT(YEAR FROM datum) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                    "AND EXTRACT(MONTH FROM datum) = EXTRACT(MONTH FROM CURRENT_DATE)";
        }

        return query;
    }
}