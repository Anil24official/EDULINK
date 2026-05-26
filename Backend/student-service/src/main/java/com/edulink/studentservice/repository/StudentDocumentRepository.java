package com.edulink.studentservice.repository;

import com.edulink.studentservice.entity.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {

    List<StudentDocument> findByStudentEmailOrderByUploadedDateDesc(String studentEmail);

    List<StudentDocument> findByVerificationStatusOrderByUploadedDateDesc(String verificationStatus);

    List<StudentDocument> findByDocTypeOrderByUploadedDateDesc(String docType);
}
