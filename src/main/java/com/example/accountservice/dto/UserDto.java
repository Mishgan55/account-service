package com.example.accountservice.dto;

import com.example.accountservice.utill.enums.DocumentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class UserDto {
    @NotEmpty(message = "Firstname should not be empty!")
    @Size(min = 1, max = 100, message = "Name's characters should be between 1 and 100")
    private String name;
    @NotEmpty(message = "Document number should not be empty!")
    @Pattern(regexp = "^[A-Za-z0-9]{6}$", message = "Invalid document number")
    private String documentNumber;
    @NotEmpty(message = "Document type should not be empty!")
    private DocumentType documentType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
