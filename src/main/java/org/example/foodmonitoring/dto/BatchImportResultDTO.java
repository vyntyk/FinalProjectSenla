package org.example.foodmonitoring.dto;

import java.util.List;
import java.util.ArrayList;

public class BatchImportResultDTO {
    private int totalRows;
    private int successfullyImportedRows;
    private int failedRows;
    private List<String> errorMessages = new ArrayList<>();

    // Constructors, Getters, and Setters/Adders
    public BatchImportResultDTO() {}

    public BatchImportResultDTO(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    public int getSuccessfullyImportedRows() { return successfullyImportedRows; }
    public void setSuccessfullyImportedRows(int successfullyImportedRows) { this.successfullyImportedRows = successfullyImportedRows; }
    public void incrementSuccess() { this.successfullyImportedRows++; }
    public int getFailedRows() { return failedRows; }
    public void setFailedRows(int failedRows) { this.failedRows = failedRows; }
    public void incrementFailure() { this.failedRows++; }
    public List<String> getErrorMessages() { return errorMessages; }
    public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }
    public void addErrorMessage(String errorMessage) { this.errorMessages.add(errorMessage); }
}
