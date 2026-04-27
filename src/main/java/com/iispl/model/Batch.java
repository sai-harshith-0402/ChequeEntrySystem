package com.iispl.model;

public class Batch {
    private String batchId;
    private String chequeNumber;
    private double amount;
    private String accountNumber;
    private String presentDate;
    private String receiverName;
    private String micrCode;

    public Batch() {}

    public Batch(String batchId, ChequeDetails cd) {
        this.batchId       = batchId;
        this.chequeNumber  = cd.getChequeNumber();
        this.amount        = cd.getAmount();
        this.accountNumber = cd.getAccountNumber();
        this.presentDate   = cd.getPresentDate();
        this.receiverName  = cd.getReceiverName();
        this.micrCode      = cd.getMicrCode();
    }

    public String getBatchId()               { return batchId; }
    public void   setBatchId(String v)       { this.batchId = v; }
    public String getChequeNumber()          { return chequeNumber; }
    public void   setChequeNumber(String v)  { this.chequeNumber = v; }
    public double getAmount()                { return amount; }
    public void   setAmount(double v)        { this.amount = v; }
    public String getAccountNumber()         { return accountNumber; }
    public void   setAccountNumber(String v) { this.accountNumber = v; }
    public String getPresentDate()           { return presentDate; }
    public void   setPresentDate(String v)   { this.presentDate = v; }
    public String getReceiverName()          { return receiverName; }
    public void   setReceiverName(String v)  { this.receiverName = v; }
    public String getMicrCode()              { return micrCode; }
    public void   setMicrCode(String v)      { this.micrCode = v; }
}