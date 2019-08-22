package com.mazen.document.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazen.document.entities.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {


}
