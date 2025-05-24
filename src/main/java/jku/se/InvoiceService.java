package jku.se;

import jku.se.exceptions.InvoiceOperationException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {

    public InvoiceService() {
    }

    public ResultSet getFilteredInvoices(String[] filters) throws SQLException {
        String query = buildQuery(filters);
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        setParameters(stmt, filters);
        return stmt.executeQuery();
    }

    // // Mit Hilfe von KI erstellt
    public String buildQuery(String[] filters) {
        final String baseQuery = "SELECT id, betrag, datum, typ, username, status, image FROM rechnungen";
        StringBuilder queryBuilder = new StringBuilder(baseQuery);
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

    public void setParameters(PreparedStatement stmt, String[] filters) throws SQLException {
        int paramIndex = 1;

        if (notEmpty(filters[0])) stmt.setInt(paramIndex++, Integer.parseInt(filters[0]));
        if (notEmpty(filters[1])) stmt.setString(paramIndex++, "%" + filters[1] + "%");
        if (notEmpty(filters[2])) stmt.setString(paramIndex++, "%" + filters[2] + "%");
        if (notEmpty(filters[3])) stmt.setString(paramIndex, filters[3]);
    }

    // Mit Hilfe von KI erstellt
    public void openInvoiceLink(String link) {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(link));
        } catch (IOException e) {
            throw new InvoiceOperationException("Failed to open invoice link: " + link, e);
        } catch (UnsupportedOperationException e) {
            throw new InvoiceOperationException("Opening links is not supported on this platform", e);
        } catch (SecurityException e) {
            throw new InvoiceOperationException("Security restrictions prevent opening the invoice link", e);
        }
    }

    // Mit Hilfe von KI erstellt
    private boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}