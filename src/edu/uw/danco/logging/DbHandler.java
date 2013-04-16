package edu.uw.danco.logging;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: dcostinett
 * Date: 4/13/13
 * Time: 6:41 PM
 */
public class DbHandler extends Handler {
    /** SQL for inserting a log record. */
    private static final String INSERT_LOG_RECORD_SQL =
            "INSERT INTO log"
                    + " (level, sequence, class, method, time, message)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";

    //implement constructor to initialize -- register drive with Class.forName(driverName)
    /** The jdbc driver */
    private final String jdbcDriver = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.driver");

    /** The database URL. */
    private final String dbUrl = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.url");

    /** Username for accessing the database. */
    private final String username = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.account");

    /** Password for the username. */
    private final String password = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.password");



    // make the connection and the prepared statement instance methods as well -- the class itself is a Singleton



    @Override
    public void publish(LogRecord record) {
        // should implement isLoggable to test if record should be logged
        if (getLevel().intValue() < record.getLevel().intValue()) {
            return;
        }

        Filter filter = getFilter();
        if (filter != null && !filter.isLoggable(record)) {
            return;
        }

        Formatter f = getFormatter();

        // insert record into DB
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DriverManager.getConnection(dbUrl, username, password);
            ps = conn.prepareStatement(INSERT_LOG_RECORD_SQL);
            ps.setString(1, record.getLevel().toString());
            ps.setLong(2, record.getSequenceNumber()); //this should be a Long
            ps.setString(3, record.getSourceClassName());
            ps.setString(4, record.getSourceMethodName());
            ps.setLong(5, new Date(new java.util.Date().getTime()).getTime());
            ps.setString(6, record.getMessage());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {
        // check the prepared statement and the conn, close it here
    }
}
