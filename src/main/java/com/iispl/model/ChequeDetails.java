package com.iispl.model;

public class ChequeDetails {
    private String chequeNumber;
    private double amount;
    private String accountNumber;
    private String presentDate;
    private String receiverName;
    private String micrCode;

    public ChequeDetails() {}

    public ChequeDetails(String chequeNumber, double amount, String accountNumber,
                         String presentDate, String receiverName, String micrCode) {
        this.chequeNumber  = chequeNumber;
        this.amount        = amount;
        this.accountNumber = accountNumber;
        this.presentDate   = presentDate;
        this.receiverName  = receiverName;
        this.micrCode      = micrCode;
    }

    public String getChequeNumber()              { return chequeNumber; }
    public void   setChequeNumber(String v)      { this.chequeNumber = v; }
    public double getAmount()                    { return amount; }
    public void   setAmount(double v)            { this.amount = v; }
    public String getAccountNumber()             { return accountNumber; }
    public void   setAccountNumber(String v)     { this.accountNumber = v; }
    public String getPresentDate()               { return presentDate; }
    public void   setPresentDate(String v)       { this.presentDate = v; }
    public String getReceiverName()              { return receiverName; }
    public void   setReceiverName(String v)      { this.receiverName = v; }
    public String getMicrCode()                  { return micrCode; }
    public void   setMicrCode(String v)          { this.micrCode = v; }
}