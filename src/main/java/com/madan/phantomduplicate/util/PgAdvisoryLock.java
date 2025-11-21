package com.madan.phantomduplicate.util;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
public class PgAdvisoryLock {

    private final JdbcTemplate jdbcTemplate;
    private final long key;

    public PgAdvisoryLock(JdbcTemplate jdbcTemplate, long key) {
        this.jdbcTemplate = jdbcTemplate;
        this.key = key;
    }

    public void lock() {
        jdbcTemplate.execute((ConnectionCallback<Void>) con -> {
            PreparedStatement ps = con.prepareStatement("SELECT pg_advisory_lock(?::bigint)");
            ps.setLong(1, key);
            // execute() will run the statement; pg_advisory_lock blocks until acquired.
            ps.execute();
            // close is handled by the template / try-with-resources in the driver
            return null; // no result expected
        });
    }


    public boolean tryLock() {
        Boolean res = jdbcTemplate.queryForObject(
                "SELECT pg_try_advisory_lock(?::bigint)",
                Boolean.class,
                key
        );
        boolean got = res != null && res;
        return got;
    }

    public boolean unlock() {
        Boolean res = jdbcTemplate.queryForObject(
                "SELECT pg_advisory_unlock(?::bigint)",
                Boolean.class,
                key
        );
        boolean released = res != null && res;
        return released;
    }
}
