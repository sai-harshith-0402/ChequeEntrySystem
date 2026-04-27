package com.iispl.dao;

import com.iispl.model.ChequeDetails;

public interface ChequeDao {
    /** Returns null if cheque number not found in master table. */
    ChequeDetails findByChequeNumber(String chequeNumber);

    /** Validates CTS login. Returns true if username/password match. */
    boolean validateLogin(String username, String password);
}