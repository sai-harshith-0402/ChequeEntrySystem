package com.iispl.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Db {

    private static final HikariDataSource DS;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/chequeentrysystem");
        cfg.setUsername("postgres");
        cfg.setPassword("password");
        cfg.setDriverClassName("org.postgresql.Driver");

        // Pool sizing — adjust to your DB server's max_connections
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);

        // Fail fast if DB is unreachable at startup
        cfg.setInitializationFailTimeout(5000);

        // Connection health checks
        cfg.setConnectionTimeout(30_000);   // ms to wait for a connection from pool
        cfg.setIdleTimeout(600_000);        // ms before idle connection is retired
        cfg.setMaxLifetime(1_800_000);      // ms max lifetime of any connection

        DS = new HikariDataSource(cfg);
    }

    public static Connection getConnection() throws SQLException {
        return DS.getConnection();
    }
}