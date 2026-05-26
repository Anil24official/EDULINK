package com.edulink.studentservice.controller;

import com.edulink.studentservice.dto.ApiResponse;
import com.edulink.studentservice.dto.StudentDocumentDto;
import com.edulink.studentservice.service.GridFsService;
import com.edulink.studentservice.service.StudentDocumentService;
import com.edulink.studentservice.util.JwtExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StudentDocumentController {

    private final StudentDocumentService documentService;
    private final GridFsService gridFsService;
    private final JwtExtractor jwtExtractor;

    /* ─── Student: upload own document ─── */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(value = "/student/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StudentDocumentDto>> upload(
            HttpServletRequest req,
            @RequestParam("docType") String docType,
            @RequestParam("file") MultipartFile file) throws IOException {
        String email = jwtExtractor.extractEmail(req);
        Long studentId = jwtExtractor.extractUserId(req);
        StudentDocumentDto saved = documentService.upload(email, studentId, docType, file);
        return ResponseEntity.ok(ApiResponse.success("Document uploaded", saved));
    }

    /* ─── Student: list own documents ─── */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/documents")
    public ResponseEntity<ApiResponse<List<StudentDocumentDto>>> myDocs(HttpServletRequest req) {
        String email = jwtExtractor.extractEmail(req);
        return ResponseEntity.ok(ApiResponse.success("Documents fetched",
                documentService.listForStudent(email)));
    }

    /* ─── Student: delete own document ─── */
    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/student/documents/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOwn(HttpServletRequest req, @PathVariable Long id) {
        String email = jwtExtractor.extractEmail(req);
        StudentDocumentDto doc = documentService.getOne(id);
        if (!email.equalsIgnoreCase(doc.getStudentEmail())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Not your document"));
        }
        documentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }

    /* ─── Admin: list / filter all documents ─── */
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @GetMapping("/admin/documents")
    public ResponseEntity<ApiResponse<List<StudentDocumentDto>>> adminList(
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) String docType) {
        return ResponseEntity.ok(ApiResponse.success("Documents fetched",
                documentService.filter(verificationStatus, docType)));
    }

    /* ─── Admin: verify (PENDING|VERIFIED|REJECTED) ─── */
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @PatchMapping("/admin/documents/{id}/verify")
    public ResponseEntity<ApiResponse<StudentDocumentDto>> verify(
            HttpServletRequest req,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String verifier = jwtExtractor.extractEmail(req);
        String status = body.getOrDefault("status", "VERIFIED");
        String notes = body.get("notes");
        return ResponseEntity.ok(ApiResponse.success("Document updated",
                documentService.verify(id, status, verifier, notes)));
    }

    /* ─── Download (student owns, or school admin) ─── */
    @PreAuthorize("hasAnyRole('STUDENT','SCHOOL_ADMIN')")
    @GetMapping("/student/documents/{id}/download")
    public ResponseEntity<Resource> download(HttpServletRequest req, @PathVariable Long id) throws IOException {
        StudentDocumentDto doc = documentService.getOne(id);

        // Students can only download their own
        if (req.isUserInRole("STUDENT")) {
            String email = jwtExtractor.extractEmail(req);
            if (!email.equalsIgnoreCase(doc.getStudentEmail())) {
                return ResponseEntity.status(403).build();
            }
        }

        byte[] data = documentService.download(id);
        String fileName = doc.getFileName() != null ? doc.getFileName() : ("document-" + id);
        String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(data.length)
                .body(resource);
    }
}
