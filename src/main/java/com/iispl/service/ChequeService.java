package com.iispl.service;

import com.iispl.model.ChequeDetails;

public interface ChequeService {

    ChequeDetails findByChequeNumber(String chequeNumber);

    /**
     * Saves a new manually entered cheque to the cheques master table.
     * Should only be called when findByChequeNumber returned null.
     */
    void saveCheque(ChequeDetails cheque);

    boolean validateLogin(String username, String password);
}