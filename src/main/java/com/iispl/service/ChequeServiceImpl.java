package com.iispl.service;

import com.iispl.dao.ChequeDao;
import com.iispl.dao.ChequeDaoImpl;
import com.iispl.model.ChequeDetails;

public class ChequeServiceImpl implements ChequeService {

    private final ChequeDao dao = new ChequeDaoImpl();

    @Override
    public ChequeDetails findByChequeNumber(String chequeNumber) {
        return dao.findByChequeNumber(chequeNumber);
    }

    /**
     * Persists a manually entered cheque to the cheques master table.
     * Called from the controller only when the cheque was not already found.
     */
    @Override
    public void saveCheque(ChequeDetails cheque) {
        dao.insertCheque(cheque);
    }

    @Override
    public boolean validateLogin(String username, String password) {
        return dao.validateLogin(username, password);
    }
}