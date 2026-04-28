package com.iispl.service;

import com.iispl.dao.BatchDao;
import com.iispl.dao.BatchDaoImpl;
import com.iispl.model.Batch;
import com.iispl.model.ChequeDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BatchServiceImpl implements BatchService {

    private final BatchDao dao = new BatchDaoImpl();

    // ── Bug 2 fix: check duplicate before saving ───────────────────────────────
    // ── Bug 3 fix: use sessionId to assign batch ID ───────────────────────────
    @Override
    public String addToBatch(ChequeDetails cd, String sessionId) {
        // Duplicate guard
        if (dao.existsByChequeNumber(cd.getChequeNumber())) {
            return "Cheque " + cd.getChequeNumber() + " has already been entered in a batch.";
        }
        String batchId = dao.assignBatchId(sessionId);
        dao.save(new Batch(batchId, cd));
        return null; // null = success
    }

    @Override
    public List<Batch> getAllBatches() { return dao.findAll(); }

    @Override
    public List<Batch> getBatchById(String batchId) { return dao.findByBatchId(batchId); }

    @Override
    public List<Map<String, String>> toBatchTableRows(List<Batch> batches, boolean includeBatchId) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Batch b : batches) {
            Map<String, String> row = new LinkedHashMap<>();
            if (includeBatchId) row.put("Batch ID",      b.getBatchId());
            row.put("Cheque No.",    b.getChequeNumber());
            row.put("Amount (₹)",    String.format("%.2f", b.getAmount()));
            row.put("Account No.",   b.getAccountNumber());
            row.put("Date",          b.getPresentDate());
            row.put("Receiver Name", b.getReceiverName());
            row.put("MICR Code",     b.getMicrCode());
            rows.add(row);
        }
        return rows;
    }

    @Override
    public String generateCxfXml(List<Batch> batches) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Map<String, List<Batch>> grouped = new LinkedHashMap<>();
        for (Batch b : batches)
            grouped.computeIfAbsent(b.getBatchId(), k -> new ArrayList<>()).add(b);

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<CXF>\n");
        sb.append("  <Header><DateTime>").append(now).append("</DateTime>")
          .append("<TotalBatches>").append(grouped.size()).append("</TotalBatches></Header>\n");
        sb.append("  <Batches>\n");
        for (Map.Entry<String, List<Batch>> e : grouped.entrySet()) {
            sb.append("    <Batch id=\"").append(e.getKey()).append("\">\n");
            for (Batch b : e.getValue()) {
                sb.append("      <Cheque>")
                  .append("<ChequeNumber>").append(b.getChequeNumber()).append("</ChequeNumber>")
                  .append("<Amount>").append(b.getAmount()).append("</Amount>")
                  .append("<AccountNumber>").append(b.getAccountNumber()).append("</AccountNumber>")
                  .append("<Date>").append(b.getPresentDate()).append("</Date>")
                  .append("<ReceiverName>").append(b.getReceiverName()).append("</ReceiverName>")
                  .append("<MICRCode>").append(b.getMicrCode()).append("</MICRCode>")
                  .append("</Cheque>\n");
            }
            sb.append("    </Batch>\n");
        }
        sb.append("  </Batches>\n</CXF>");
        return sb.toString();
    }

    @Override
    public String generateRrfXml(String batchId, String status) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<RRF>\n" +
               "  <Header>\n" +
               "    <DateTime>" + now + "</DateTime>\n" +
               "    <BatchID>" + batchId + "</BatchID>\n" +
               "    <Status>" + status + "</Status>\n" +
               "  </Header>\n</RRF>";
    }

    @Override
    public String generateBpxfXml(String batchId, String reason) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<BPXF>\n" +
               "  <Header>\n" +
               "    <DateTime>" + now + "</DateTime>\n" +
               "    <BatchID>" + batchId + "</BatchID>\n" +
               "    <Status>REJECTED</Status>\n" +
               "  </Header>\n" +
               "  <RejectionDetails><Reason>" + reason + "</Reason></RejectionDetails>\n</BPXF>";
    }

    @Override
    public void saveBatchStatus(String batchId, String status, String reason) {
        dao.saveStatus(batchId, status, reason);
    }

    // ── Bug 3 fix: delete session row so next login gets fresh batch numbers ───
    @Override
    public void clearSession(String sessionId) {
        if (sessionId != null) dao.deleteSession(sessionId);
    }
}