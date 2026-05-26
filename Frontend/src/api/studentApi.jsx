import httpClient from "./httpClient";
import Endpoints from "../config/endpoints";

const studentApi = {
  /* ── GET /student/courses ── */
  fetchEnrolledCourses: () =>
    httpClient.get(Endpoints.student.enrolledCourses),

  /* ── GET /student/available-courses ── */
  fetchAvailableCourses: () =>
    httpClient.get(Endpoints.student.availableCourses),

  /* ── POST /student/enroll  body: { courseCode } ── */
  enrollCourse: (courseCode) =>
    httpClient.post(Endpoints.student.enrollCourse, { courseCode }),

  /* ── GET /student/materials/{courseCode} ── */
  fetchMaterials: (courseCode) =>
    httpClient.get(Endpoints.student.materials(courseCode)),

  /* ── GET /student/materials/download/{fileId} → binary ── */
  downloadMaterial: (fileId) =>
    httpClient.get(Endpoints.student.downloadMaterial(fileId), {
      responseType: "blob",
    }),

  /* ── GET /student/assignments/{courseCode} ── */
  fetchAssignments: (courseCode) =>
    httpClient.get(Endpoints.student.assignments(courseCode)),

  /* ── POST /student/assignments/upload → multipart/form-data ──
       Fields: assignmentNum (int), courseCode (str), assignmentTitle (str),
               submissionContent? (str), file? (File)                      */
  uploadSubmission: (formData) =>
    httpClient.post(Endpoints.student.uploadSubmission, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    }),

  /* ── POST /student/assignments/upload-legacy → JSON ──
       Body: { assignmentNum, courseCode, assignmentTitle,
               submissionContent?, fileUrl? }                */
  uploadSubmissionLegacy: (payload) =>
    httpClient.post(Endpoints.student.uploadSubmissionLegacy, payload),

  /* ── GET /exam/student/grades — exam grades from exam-service ── */
  fetchGrades: () =>
    httpClient.get(Endpoints.exam.studentGrades),

  /* ── GET /student/my-grades/assignments — assignment grades from student-service ── */
  fetchAssignmentGrades: () =>
    httpClient.get(Endpoints.student.myAssignmentGrades),

  /* ── GET /student/attendance — via attendance-service gateway route ── */
  fetchAttendance: () =>
    httpClient.get(Endpoints.attendance.studentAttendance),

  /* ── POST /student/profile (public, no auth needed) ── */
  createProfile: (payload) =>
    httpClient.post(Endpoints.student.createProfile, payload),

  /* ── Student Documents (PDF 4.2) ── */
  fetchMyDocuments: () =>
    httpClient.get(Endpoints.student.documents),

  uploadDocument: (docType, file) => {
    const fd = new FormData();
    fd.append("docType", docType);
    fd.append("file", file);
    return httpClient.post(Endpoints.student.uploadDocument, fd, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  downloadDocument: (id) =>
    httpClient.get(Endpoints.student.downloadDocument(id), {
      responseType: "blob",
    }),

  deleteDocument: (id) =>
    httpClient.delete(Endpoints.student.documentById(id)),

  /* ── Student Performance Metrics (PDF 4.6) ── */
  fetchMyPerformanceMetrics: (params) =>
    httpClient.get(Endpoints.student.performanceMetrics, { params }),
};

export default studentApi;
