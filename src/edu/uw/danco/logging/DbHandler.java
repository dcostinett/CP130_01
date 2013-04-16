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
public final class DbHandler extends Handler {
    /** Logger */
    private static final Logger logger = Logger.getLogger(DbHandler.class.getName());

    /** SQL for inserting a log record. */
    private static final String INSERT_LOG_RECORD_SQL =
            "INSERT INTO log"
                    + " (level, sequence, class, method, time, message)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";

    //implement constructor to initialize -- register drive with Class.forName(driverName)

    /** The database URL. */
    private final String dbUrl = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.url");

    /** Username for accessing the database. */
    private final String username = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.account");

    /** Password for the username. */
    private final String password = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.password");



    // make the connection and the prepared statement instance methods as well -- the class itself is a Singleton
    /** DB connection object */
    private Connection conn = null;

    /** Prepared statement used by logger */
    private PreparedStatement ps = null;


    /**
     * Instantiate the object (makes a connection to the target DB)
     * */
    public DbHandler() {
        try {
            /* The jdbc driver */
            String jdbcDriver = LogManager.getLogManager().getProperty("edu.uw.danco.logging.DbHandler.driver");
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Unable to load jdbc driver", e);
        }
        connect();
    }


    @Override
    public void publish(LogRecord record) {
        if (getLevel().intValue() < record.getLevel().intValue()) {
            return;
        }

        Filter filter = getFilter();
        if (filter != null && !filter.isLoggable(record)) {
            return;
        }

        // insert record into DB
        try {
            connect();
            ps.setString(1, record.getLevel().toString());
            ps.setLong(2, record.getSequenceNumber());
            ps.setString(3, record.getSourceClassName());
            ps.setString(4, record.getSourceMethodName());
            ps.setLong(5, new Date(new java.util.Date().getTime()).getTime());
            ps.setString(6, record.getMessage());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to insert log record", e);
        }
    }

    /**
     * Initialize the connection and prepared statement
     * */
    private void connect() {
        if (conn == null || ps == null) {
            try {
                conn = DriverManager.getConnection(dbUrl, username, password);
                ps = conn.prepareStatement(INSERT_LOG_RECORD_SQL);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Unable to connect to target DB", e);
            }
        }
    }

    @Override
    public void flush() {
        //no op
    }

    @Override
    public void close() throws SecurityException {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Unable to close the prepared statement", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Unable to close the connection", e);
            }
        }
    }
}
