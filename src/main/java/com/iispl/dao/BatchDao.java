package com.iispl.dao;

import com.iispl.model.Batch;
import java.util.List;

public interface BatchDao {
    void save(Batch batch);
    List<Batch> findAll();
    List<Batch> findByBatchId(String batchId);
    int countAll();
    void saveStatus(String batchId, String status, String rejectionReason);
    String getStatus(String batchId);
}