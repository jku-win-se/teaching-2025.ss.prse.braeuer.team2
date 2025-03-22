package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final String URL = "jdbc:postgresql://postgres:[YOUR-PASSWORD]@db.pwltfjlqripcrhenhgnk.supabase.co:5432/postgres";
    private static final String USER = "Lunchify";
    private static final String PASSWORD = "ujCpo7WdTPUzWpss";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
