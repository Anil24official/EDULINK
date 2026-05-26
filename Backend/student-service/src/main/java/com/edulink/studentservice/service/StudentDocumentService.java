package com.edulink.studentservice.service;

import com.edulink.studentservice.dto.StudentDocumentDto;
import com.edulink.studentservice.entity.StudentDocument;
import com.edulink.studentservice.repository.StudentDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentDocumentService {

    private static final Set<String> ALLOWED_TYPES = Set.of("IDProof", "ReportCard");
    private static final Set<String> ALLOWED_STATUSES = Set.of("PENDING", "VERIFIED", "REJECTED");

    private final StudentDocumentRepository repository;
    private final GridFsService gridFsService;

    public StudentDocumentDto upload(String studentEmail, Long studentId, String docType, MultipartFile file)
            throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (!ALLOWED_TYPES.contains(docType)) {
            throw new IllegalArgumentException("docType must be one of " + ALLOWED_TYPES);
        }

        String fileId = gridFsService.uploadFile(file, "student-doc:" + studentEmail);

        StudentDocument doc = StudentDocument.builder()
                .studentEmail(studentEmail)
                .studentId(studentId)
                .docType(docType)
                .fileId(fileId)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .verificationStatus("PENDING")
                .build();
        return toDto(repository.save(doc));
    }

    public List<StudentDocumentDto> listForStudent(String studentEmail) {
        return repository.findByStudentEmailOrderByUploadedDateDesc(studentEmail).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    public List<StudentDocumentDto> listAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<StudentDocumentDto> filter(String verificationStatus, String docType) {
        List<StudentDocument> rows;
        if (verificationStatus != null && !verificationStatus.isBlank()) {
            rows = repository.findByVerificationStatusOrderByUploadedDateDesc(verificationStatus);
        } else if (docType != null && !docType.isBlank()) {
            rows = repository.findByDocTypeOrderByUploadedDateDesc(docType);
        } else {
            rows = repository.findAll();
        }
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    public byte[] download(Long id) throws IOException {
        StudentDocument doc = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id));
        return gridFsService.downloadFile(doc.getFileId());
    }

    public StudentDocumentDto getOne(Long id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id)));
    }

    public StudentDocumentDto verify(Long id, String status, String verifierEmail, String notes) {
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("verificationStatus must be one of " + ALLOWED_STATUSES);
        }
        StudentDocument doc = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id));
        doc.setVerificationStatus(status);
        doc.setVerifiedBy(verifierEmail);
        doc.setVerifiedAt(LocalDateTime.now());
        doc.setVerificationNotes(notes);
        return toDto(repository.save(doc));
    }

    public void delete(Long id) {
        StudentDocument doc = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id));
        try {
            gridFsService.deleteFile(doc.getFileId());
        } catch (Exception ignored) {
            // best-effort cleanup
        }
        repository.delete(doc);
    }

    private StudentDocumentDto toDto(StudentDocument d) {
        return StudentDocumentDto.builder()
                .id(d.getId())
                .studentEmail(d.getStudentEmail())
                .studentId(d.getStudentId())
                .docType(d.getDocType())
                .fileId(d.getFileId())
                .fileName(d.getFileName())
                .contentType(d.getContentType())
                .fileSize(d.getFileSize())
                .verificationStatus(d.getVerificationStatus())
                .verifiedBy(d.getVerifiedBy())
                .verifiedAt(d.getVerifiedAt())
                .verificationNotes(d.getVerificationNotes())
                .uploadedDate(d.getUploadedDate())
                .build();
    }
}
