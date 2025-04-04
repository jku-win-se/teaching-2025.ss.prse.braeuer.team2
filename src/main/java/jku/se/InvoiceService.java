package jku.se;

import java.io.IOException;
import java.sql.*;

public class InvoiceService {

    public ResultSet getFilteredInvoices(String[] filters) throws SQLException {
        String query = buildQuery(filters);
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        setParameters(stmt, filters);
        return stmt.executeQuery();
    }

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

    private void setParameters(PreparedStatement stmt, String[] filters) throws SQLException {
        int paramIndex = 1;

        if (notEmpty(filters[0])) {
            stmt.setInt(paramIndex++, Integer.parseInt(filters[0]));
        }
        if (notEmpty(filters[1])) {
            stmt.setString(paramIndex++, "%" + filters[1] + "%");
        }
        if (notEmpty(filters[2])) {
            stmt.setString(paramIndex++, "%" + filters[2] + "%");
        }
        if (notEmpty(filters[3])) {
            stmt.setString(paramIndex, filters[3]);
        }
    }

    public void openInvoiceLink(String link) {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(link));
        } catch (IOException e) {
            throw new RuntimeException("Failed to open invoice link", e);
        }
    }

    private boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}