package com.iispl.dao;

import com.iispl.model.Batch;
import com.iispl.util.Db;

import java.sql.*;
import java.util.*;

public class BatchDaoImpl implements BatchDao {

    @Override
    public void save(Batch b) {
        String sql = "INSERT INTO batches " +
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
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM batches";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error counting batches", e);
        }
    }

    @Override
    public void saveStatus(String batchId, String status, String rejectionReason) {
        String sql = "INSERT INTO batch_status (batch_id, status, rejection_reason) " +
                     "VALUES (?, ?, ?) " +
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