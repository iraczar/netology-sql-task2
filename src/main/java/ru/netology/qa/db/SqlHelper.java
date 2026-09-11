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
        long deadline = System.currentTimeMillis() + 5000;
        String code = null;
        while (System.currentTimeMillis() < deadline) {
            try (Connection conn = getConnection()) {
                code = RUNNER.query(conn,
                        "SELECT ac.code FROM auth_codes ac " +
                                "JOIN users u ON u.id = ac.user_id " +
                                "WHERE u.login = ? " +
                                "ORDER BY ac.created DESC LIMIT 1;",
                        new ScalarHandler<String>(), login);
            } catch (SQLException e) {
                throw new RuntimeException("Ne udalos poluchit kod podtverzhdeniya iz BD", e);
            }
            if (code != null) {
                return code;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new RuntimeException("Kod podtverzhdeniya ne poyavilsya v BD dlya " + login);
    }

    public static int getCardBalanceInKopecks(String cardNumber) {
        try (Connection conn = getConnection()) {
            Object result = RUNNER.query(conn,
                    "SELECT balance_in_kopecks FROM cards WHERE number = ?;",
                    new ScalarHandler<>(), cardNumber);
            return ((Number) result).intValue();
        } catch (SQLException e) {
            throw new RuntimeException("Ne udalos poluchit balans karty iz BD", e);
        }
    }
}