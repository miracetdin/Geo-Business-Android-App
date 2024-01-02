package com.example.models;

public class Travel {

    private String id;
    private String username;
    private String travelDate;
    private String startLocation;
    private String endLocation;
    private String invoicePhoto;
    private String invoiceInfo;
    private String invoiceNote;
    private Float invoicePrice;
    private Float priceEstimate;
    private String suspicious;
    private String status;
    private String approveByAccountant;
    private String approveDate;

    public Travel(String id, String username, String travelDate, String startLocation,
                  String endLocation, String invoicePhoto, String invoiceInfo, String invoiceNote,
                  Float invoicePrice, Float priceEstimate, String suspicious, String status,
                  String approveByAccountant, String approveDate) {
        this.id = id;
        this.username = username;
        this.travelDate = travelDate;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.invoicePhoto = invoicePhoto;
        this.invoiceInfo = invoiceInfo;
        this.invoiceNote = invoiceNote;
        this.invoicePrice = invoicePrice;
        this.priceEstimate = priceEstimate;
        this.suspicious = suspicious;
        this.status = status;
        this.approveByAccountant = approveByAccountant;
        this.approveDate = approveDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getInvoicePhoto() {
        return invoicePhoto;
    }

    public void setInvoicePhoto(String invoicePhoto) {
        this.invoicePhoto = invoicePhoto;
    }

    public String getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(String invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }

    public String getInvoiceNote() {
        return invoiceNote;
    }

    public void setInvoiceNote(String invoiceNote) {
        this.invoiceNote = invoiceNote;
    }

    public Float getInvoicePrice() {
        return invoicePrice;
    }

    public void setInvoicePrice(Float invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    public Float getPriceEstimate() {
        return priceEstimate;
    }

    public void setPriceEstimate(Float priceEstimate) {
        this.priceEstimate = priceEstimate;
    }

    public String getSuspicious() {
        return suspicious;
    }

    public void setSuspicious(String suspicious) {
        this.suspicious = suspicious;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApproveByAccountant() {
        return approveByAccountant;
    }

    public void setApproveByAccountant(String approveByAccountant) {
        this.approveByAccountant = approveByAccountant;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }
}
