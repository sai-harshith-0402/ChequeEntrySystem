package com.iispl.dao;

import com.iispl.model.Batch;
import com.iispl.util.Db;

import java.sql.*;
import java.util.*;

public class BatchDaoImpl implements BatchDao {

    // ── Duplicate cheque guard ─────────────────────────────────────────────────
    @Override
    public boolean existsByChequeNumber(String chequeNumber) {
        String sql = "SELECT 1 FROM batches WHERE cheque_number = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, chequeNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error checking duplicate cheque", e);
        }
    }

    // ── Session-aware batch assignment ─────────────────────────────────────────
    /**
     * How it works:
     *
     *  cheque_count is the number of cheques already saved for this session
     *  BEFORE this new one is inserted.
     *
     *  batch_number = (cheque_count / 5) + 1
     *
     *  cheque_count  batch_number
     *  ──────────────────────────
     *       0          1   (1st  cheque → BATCH001)
     *       1          1   (2nd  cheque → BATCH001)
     *       2          1   (3rd  cheque → BATCH001)
     *       3          1   (4th  cheque → BATCH001)
     *       4          1   (5th  cheque → BATCH001)
     *       5          2   (6th  cheque → BATCH002)
     *       6          2   ...
     *      10          3   (11th cheque → BATCH003)
     *
     *  After reading batch_number we increment cheque_count by 1.
     *  We never need to touch batch_number in the DB at all — it is
     *  always derived from cheque_count.
     */
    @Override
    public String assignBatchId(String sessionId) {
        String upsert =
            "INSERT INTO batch_session (session_id, cheque_count) " +
            "VALUES (?, 0) " +
            "ON CONFLICT (session_id) DO NOTHING";

        // Read current count, derive batch number, then increment count
        String readCount =
            "SELECT cheque_count FROM batch_session WHERE session_id = ?";

        String increment =
            "UPDATE batch_session SET cheque_count = cheque_count + 1 " +
            "WHERE session_id = ?";

        try (Connection con = Db.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. Ensure the session row exists
                try (PreparedStatement ps = con.prepareStatement(upsert)) {
                    ps.setString(1, sessionId);
                    ps.executeUpdate();
                }

                // 2. Read the current cheque_count (before this cheque)
                int chequeCount;
                try (PreparedStatement ps = con.prepareStatement(readCount)) {
                    ps.setString(1, sessionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new RuntimeException("Session row missing after upsert");
                        chequeCount = rs.getInt("cheque_count");
                    }
                }

                // 3. Derive batch number from count
                int batchNumber = (chequeCount / 5) + 1;

                // 4. Increment count for next call
                try (PreparedStatement ps = con.prepareStatement(increment)) {
                    ps.setString(1, sessionId);
                    ps.executeUpdate();
                }

                con.commit();
                return String.format("BATCH%03d", batchNumber);

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("Error assigning batch ID", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in assignBatchId", e);
        }
    }

    @Override
    public void save(Batch b) {
        String sql =
            "INSERT INTO batches " +
            "(batch_id, cheque_number, amount, account_number, present_date, receiver_name, micr_code) " +
            "VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getBatchId());
            ps.setString(2, b.getChequeNumber());
            ps.setDouble(3, b.getAmount());
            ps.setString(4, b.getAccountNumber());
            ps.setString(5, b.getPresentDate());
            ps.setString(6, b.getReceiverName());
            ps.setString(7, b.getMicrCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error saving batch row", e);
        }
    }

    @Override
    public List<Batch> findAll() {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches ORDER BY batch_id, id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching all batches", e);
        }
        return list;
    }

    @Override
    public List<Batch> findByBatchId(String batchId) {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches WHERE batch_id = ? ORDER BY id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, batchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching batch: " + batchId, e);
        }
        return list;
    }

    @Override
    public void saveStatus(String batchId, String status, String rejectionReason) {
        String sql =
            "INSERT INTO batch_status (batch_id, status, rejection_reason) VALUES (?, ?, ?) " +
            "ON CONFLICT (batch_id) DO UPDATE " +
            "SET status = EXCLUDED.status, " +
            "    rejection_reason = EXCLUDED.rejection_reason, " +
            "    processed_at = NOW()";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, batchId);
            ps.setString(2, status);
            ps.setString(3, rejectionReason);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error saving batch status", e);
        }
    }

    @Override
    public String getStatus(String batchId) {
        String sql = "SELECT status FROM batch_status WHERE batch_id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, batchId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : "PENDING";
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching batch status", e);
        }
    }

    @Override
    public void deleteSession(String sessionId) {
        String sql = "DELETE FROM batch_session WHERE session_id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error deleting session", e);
        }
    }

    private Batch map(ResultSet rs) throws SQLException {
        Batch b = new Batch();
        b.setBatchId(rs.getString("batch_id"));
        b.setChequeNumber(rs.getString("cheque_number"));
        b.setAmount(rs.getDouble("amount"));
        b.setAccountNumber(rs.getString("account_number"));
        b.setPresentDate(rs.getString("present_date"));
        b.setReceiverName(rs.getString("receiver_name"));
        b.setMicrCode(rs.getString("micr_code"));
        return b;
    }
}