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

    @Override
    public boolean validateLogin(String username, String password) {
        return dao.validateLogin(username, password);
    }
}