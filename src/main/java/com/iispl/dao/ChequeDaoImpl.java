package com.iispl.dao;

import com.iispl.model.ChequeDetails;
import com.iispl.util.Db;

import java.sql.*;

public class ChequeDaoImpl implements ChequeDao {

    @Override
    public ChequeDetails findByChequeNumber(String chequeNumber) {
        String sql = "SELECT cheque_number, amount, account_number, receiver_name, micr_code " +
                     "FROM cheques WHERE cheque_number = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, chequeNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ChequeDetails(
                        rs.getString("cheque_number"),
                        rs.getDouble("amount"),
                        rs.getString("account_number"),
                        "",                             // presentDate filled at entry time
                        rs.getString("receiver_name"),
                        rs.getString("micr_code")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error looking up cheque: " + chequeNumber, e);
        }
        return null;
    }

    /**
     * Inserts a manually entered cheque into the cheques master table.
     * This is what was missing — without this, manual cheques never
     * appear in the cheques table even though they land in batches.
     */
    @Override
    public void insertCheque(ChequeDetails cheque) {
        String sql = "INSERT INTO cheques " +
                     "(cheque_number, amount, account_number, receiver_name, micr_code) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cheque.getChequeNumber());
            ps.setDouble(2, cheque.getAmount());
            ps.setString(3, cheque.getAccountNumber());
            ps.setString(4, cheque.getReceiverName());
            ps.setString(5, cheque.getMicrCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error inserting cheque: " + cheque.getChequeNumber(), e);
        }
    }

    @Override
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT 1 FROM cts_users WHERE username = ? AND password_hash = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during login", e);
        }
    }
}