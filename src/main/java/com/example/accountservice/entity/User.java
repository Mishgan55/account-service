package com.example.accountservice.entity;

import com.example.accountservice.utill.enums.DocumentType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "document_number")
    private String documentNumber;
    @Column(name = "document_type")
    @Enumerated(value = EnumType.STRING)
    private DocumentType documentType;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Account> accounts;

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
