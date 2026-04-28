package com.iispl.dao;

import com.iispl.model.ChequeDetails;

public interface ChequeDao {

    /** Returns null if cheque number not found in master table. */
    ChequeDetails findByChequeNumber(String chequeNumber);

    /**
     * Inserts a new cheque into the cheques master table.
     * Called only for manually entered cheques (i.e. not already in the table).
     */
    void insertCheque(ChequeDetails cheque);

    /** Validates CTS login. Returns true if username/password match. */
    boolean validateLogin(String username, String password);
}