package com.iispl.service;

import com.iispl.model.Batch;
import com.iispl.model.ChequeDetails;
import java.util.List;
import java.util.Map;

public interface BatchService {
    /**
     * Validates, assigns a batch ID and persists the cheque.
     * Returns an error message string if the cheque is a duplicate, or null on success.
     */
    String addToBatch(ChequeDetails cd, String sessionId);

    List<Batch> getAllBatches();
    List<Batch> getBatchById(String batchId);

    List<Map<String, String>> toBatchTableRows(List<Batch> batches, boolean includeBatchId);

    String generateCxfXml(List<Batch> batches);
    String generateRrfXml(String batchId, String status);
    String generateBpxfXml(String batchId, String reason);

    void saveBatchStatus(String batchId, String status, String reason);

    /** Called on new session — removes the session row so batch numbers restart. */
    void clearSession(String sessionId);
}