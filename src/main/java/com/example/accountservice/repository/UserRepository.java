package com.example.accountservice.repository;

import com.example.accountservice.entity.User;
import com.example.accountservice.utill.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDocumentNumberAndDocumentType(String documentNumber, DocumentType documentType);
}
