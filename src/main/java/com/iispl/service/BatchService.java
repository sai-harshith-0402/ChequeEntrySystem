package com.iispl.service;

import com.iispl.model.Batch;
import com.iispl.model.ChequeDetails;
import java.util.List;
import java.util.Map;

public interface BatchService {
    /** Assigns a batch ID and persists the cheque. Returns the assigned batchId. */
    String addToBatch(ChequeDetails cd);

    List<Batch> getAllBatches();
    List<Batch> getBatchById(String batchId);
    int getTotalCount();

    /** Returns List<Map<header, value>> rows — ready for DynamicTable. */
    List<Map<String, String>> toBatchTableRows(List<Batch> batches, boolean includeBatchId);

    String generateCxfXml(List<Batch> batches);
    String generateRrfXml(String batchId, String status);
    String generateBpxfXml(String batchId, String reason);

    void saveBatchStatus(String batchId, String status, String reason);
}