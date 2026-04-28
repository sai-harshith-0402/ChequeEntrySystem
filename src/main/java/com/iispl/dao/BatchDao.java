package com.iispl.dao;

import com.iispl.model.Batch;
import java.util.List;

public interface BatchDao {
    /** Returns true if this cheque number already exists in the batches table. */
    boolean existsByChequeNumber(String chequeNumber);

    /** Save a new cheque row into batches. */
    void save(Batch batch);

    List<Batch> findAll();
    List<Batch> findByBatchId(String batchId);

    /**
     * Ensures a row exists for this session.
     * Returns the batch_id (e.g. "BATCH003") for the next cheque.
     * Increments cheque_count; bumps batch_number every 5 cheques.
     */
    String assignBatchId(String sessionId);

    void saveStatus(String batchId, String status, String rejectionReason);
    String getStatus(String batchId);

    /** Remove the session row so the next login starts fresh. */
    void deleteSession(String sessionId);
}