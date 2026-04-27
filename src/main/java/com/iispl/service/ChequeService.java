package com.iispl.service;

import com.iispl.model.ChequeDetails;

public interface ChequeService {
    ChequeDetails findByChequeNumber(String chequeNumber);
    boolean validateLogin(String username, String password);
}