package ru.netology.qa.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlHelper {

    private static final QueryRunner RUNNER = new QueryRunner();

    private SqlHelper() {
    }

    private static Connection getConnection() throws SQLException {
        String url = System.getProperty("db.url", "jdbc:mysql://localhost:3306/app");
        String user = System.getProperty("db.user", "app");
        String password = System.getProperty("db.password", "pass");
        return DriverManager.getConnection(url, user, password);
    }

    public static String getVerificationCode(String login) {
        try (Connection conn = getConnection()) {
            return RUNNER.query(conn,
                    "SELECT ac.code FROM auth_codes ac " +
                            "JOIN users u ON u.id = ac.user_id " +
                            "WHERE u.login = ? " +
                            "ORDER BY ac.created DESC LIMIT 1;",
                    new ScalarHandler<String>(), login);
        } catch (SQLException e) {
            throw new RuntimeException("Could not get verification code", e);
        }
    }
}