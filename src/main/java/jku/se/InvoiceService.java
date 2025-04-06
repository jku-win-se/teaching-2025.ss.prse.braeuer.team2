package jku.se;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {

    public ResultSet getFilteredInvoices(String[] filters) throws SQLException {
        String query = buildQuery(filters);
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        setParameters(stmt, filters);
        return stmt.executeQuery();
    }

    // Deepseek Anfang
    private String buildQuery(String[] filters) {
        final String BASE_QUERY = "SELECT id, betrag, datum, typ, username, status, image FROM rechnungen";
        StringBuilder queryBuilder = new StringBuilder(BASE_QUERY);
        List<String> whereConditions = new ArrayList<>();

        // Filterbedingungen sammeln
        if (notEmpty(filters[0])) whereConditions.add("id = ?");
        if (notEmpty(filters[1])) whereConditions.add("typ::text LIKE ?");
        if (notEmpty(filters[2])) whereConditions.add("username LIKE ?");
        if (notEmpty(filters[3])) whereConditions.add("status::text = ?");
        if (notEmpty(filters[4])) whereConditions.add("""
            EXTRACT(YEAR FROM datum) = EXTRACT(YEAR FROM CURRENT_DATE)
            AND EXTRACT(MONTH FROM datum) = EXTRACT(MONTH FROM CURRENT_DATE)
            """);

        // WHERE-Klausel nur hinzufügen wenn mindestens eine Bedingung existiert
        if (!whereConditions.isEmpty()) {
            queryBuilder.append(" WHERE ")
                    .append(String.join(" AND ", whereConditions));
        }

        queryBuilder.append(" ORDER BY id DESC");

        return queryBuilder.toString();
    }
    // Deepseek Ende

    private void setParameters(PreparedStatement stmt, String[] filters) throws SQLException {
        int paramIndex = 1;

        if (notEmpty(filters[0])) stmt.setInt(paramIndex++, Integer.parseInt(filters[0]));
        if (notEmpty(filters[1])) stmt.setString(paramIndex++, "%" + filters[1] + "%");
        if (notEmpty(filters[2])) stmt.setString(paramIndex++, "%" + filters[2] + "%");
        if (notEmpty(filters[3])) stmt.setString(paramIndex, filters[3]);
    }

    // Chat GPT Anfang
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
    // Chat GPT Ende
}