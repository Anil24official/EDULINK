# EduLink — Implementation vs PDF Requirements Gap Analysis

Module-by-module audit comparing the current codebase against the EduLink PDF spec.

---

## Architecture (PDF §3)

| Item | Status |
|---|---|
| Frontend: React | ✅ Implemented (`Frontend/src/`) |
| Backend: REST microservices | ✅ 8 services (identity, student, course, exam, attendance, compliance, notification + api-gateway, eureka, config-server) |
| Relational DB (MySQL/PostgreSQL/SQL Server) | ✅ MySQL (`Backend/database-schema.sql`) |
| API gateway | ✅ Spring Cloud Gateway with JWT filter, circuit breaker, retry |
| WAF | ❌ Not configured |
| Centralized logging | ❌ Only local file logs; no ELK / Splunk / Loki / OpenTelemetry / Sleuth-Zipkin |

---

## 2.1 Identity & Access Management

**PDF Entities:** `User`, `AuditLog(AuditID, UserID, Action, Resource, Timestamp)`

| Item | Status | Notes |
|---|---|---|
| User entity (multi-role) | ✅ | `identity-service/.../User.java` — covers all 7 roles |
| JWT login + refresh | ✅ | `/auth/login`, `/auth/refresh` |
| RBAC via `@PreAuthorize` | ✅ | Applied across services |
| Force-change-password flow | ✅ | `mustChangePassword` + temporary passwords |
| **AuditLog entity** | ❌ **MISSING** | Required by PDF — no entity, no `/audit` endpoint, no auto-write on login/CRUD |

**Changes needed:**
1. Create `AuditLog` entity in `identity-service` with fields: `auditId, userEmail, action, resource, timestamp, ipAddress`.
2. Create `AuditLogRepository` + `AuditLogService`.
3. Add a Spring AOP aspect (or interceptor) that writes audit rows on: login, logout, password change, user create/delete, admin actions, grade entry, attendance marking, compliance approvals.
4. Add `GET /audit/logs` (filterable by user, resource, date-range) restricted to `COMPLIANCE_OFFICER` and `OPERATOR`.
5. Add an **Audit Log Viewer** page in the frontend (already partially exists for rules in `pages/compliance/AuditRules.jsx` — extend it).

---

## 2.2 Student Registration & Profile Management

**PDF Entities:** `Student`, `StudentDocument(DocumentID, StudentID, DocType[IDProof/ReportCard], FileURI, UploadedDate, VerificationStatus)`

| Item | Status | Notes |
|---|---|---|
| Student entity + registration | ✅ | School admin creates students; roll number auto-generated |
| Profile view / edit | ✅ | `pages/profile/EditProfilePage.jsx` |
| **StudentDocument entity** | ❌ **MISSING** | No IDProof / ReportCard upload anywhere |
| **Document verification workflow** | ❌ **MISSING** | No `VerificationStatus` (PENDING/VERIFIED/REJECTED) |

**Changes needed:**
1. Create `StudentDocument` entity in `identity-service` (or `student-service`): `documentId, studentEmail, docType (IDProof/ReportCard/Other), fileId (GridFS), uploadedDate, verificationStatus, verifiedBy, verifiedAt, remarks`.
2. Add endpoints:
   - `POST /student/documents/upload` (multipart)
   - `GET /student/documents` (student's own)
   - `GET /admin/documents/pending` (SCHOOL_ADMIN)
   - `PUT /admin/documents/{id}/verify` (approve/reject + remarks)
3. Add frontend pages:
   - `student/UploadDocumentsPage.jsx` — upload + status list
   - `schoolAdmin/DocumentVerificationPage.jsx` — pending queue + approve/reject
4. Reuse existing `GridFsService` pattern from `course-service`/`student-service`.

---

## 2.3 Course & Class Management

**PDF Entities:** `Course(CourseID, Title, Subject, GradeLevel, Credits, Status)`, `Class(ClassID, CourseID, TeacherID, Schedule, Status)`

| Item | Status | Notes |
|---|---|---|
| Course entity | ✅ | `course-service/.../Course.java` — but missing **Credits** and explicit **Status** |
| Class entity (`ClassRoom`) | ⚠️ Partial | Has className/grade/section/capacity. **Missing `Schedule` and `Status`** |
| Admin create course / class | ✅ | `AddCoursePage.jsx`, `AddClassPage.jsx` |
| Teacher → class roster | ✅ | `ClassRosterPage.jsx` |
| Enrollment workflow | ✅ | Student-initiated enrollment via `student-service` |

**Changes needed:**
1. Add `credits` and `status` (ACTIVE/ARCHIVED) to `Course` entity.
2. Add `schedule` (e.g., JSON or string like `"MWF 9:00-10:00"`) and `status` (ACTIVE/INACTIVE) to `ClassRoom`.
3. Update create/edit UIs in school-admin portal to capture these fields.
4. Add a `Status` filter on `ViewStudentsPage` / `ViewTeachersPage` for ACTIVE/INACTIVE.

---

## 2.4 Digital Learning Content Delivery

**PDF Entities:** `LearningMaterial`, `Assignment(AssignmentID, CourseID, StudentID, Title, SubmissionDate, Status)`

| Item | Status | Notes |
|---|---|---|
| LearningMaterial (GridFS) | ✅ | Mongo + GridFS storage |
| Material upload (teacher) | ✅ | `UploadMaterialPage.jsx` |
| Material download (student) | ✅ | |
| Assignment create / submit | ✅ | |
| Assignment grading | ✅ | `marksObtained`, `maxMarks`, `remarks`, `evaluatedBy` |
| **Usage tracking** (PDF: "tracks usage") | ❌ **MISSING** | No counter/log for material downloads or views |

**Changes needed:**
1. Add `MaterialAccessLog` entity (or reuse audit log) capturing `studentEmail, fileId, downloadedAt`.
2. Increment on every `GET /materials/download/{fileId}` hit.
3. Add teacher-side analytics: "Material X downloaded by N/M students" on `UploadMaterialPage`.

---

## 2.5 Examination & Grading Management

**PDF Entities:** `Exam(ExamID, CourseID, Type[Midterm/Final], Date, Status)`, `Grade(GradeID, ExamID, StudentID, Score, Grade, Status)`

| Item | Status | Notes |
|---|---|---|
| Exam entity + scheduling | ✅ | Includes `examType`, `examDate`, `durationMinutes`, `passingMarks` |
| Timed exam (countdown) | ✅ | `startedAt` + `durationMinutes` |
| Exam submission (text only) | ✅ | |
| Grade entity + grading workflow | ✅ | Teacher grades, student sees grades |
| Reset attempt | ✅ | |
| **Exam Status field** (SCHEDULED/ONGOING/COMPLETED) | ❌ Implicit only | No explicit lifecycle |
| **File upload in exam submission** | ❌ | Text-only as noted in your code |

**Changes needed:**
1. Add explicit `status` enum to `Exam` (SCHEDULED, ACTIVE, CLOSED, ARCHIVED) and compute/transition automatically based on `examDate` + `durationMinutes`.
2. Add `status` to `Grade` (DRAFT, PUBLISHED) so teachers can grade in bulk before releasing to students.
3. (Optional) Support file upload in `submit-exam` for subjective exams.

---

## 2.6 Attendance & Performance Tracking

**PDF Entities:** `Attendance`, `PerformanceMetric(MetricID, StudentID, CourseID, Score, Date, Status)`

| Item | Status | Notes |
|---|---|---|
| Attendance entity + marking | ✅ | Single + bulk |
| Attendance status enum (PRESENT/ABSENT/LATE/EXCUSED) | ✅ | |
| Low-attendance flag | ✅ | `/teacher/low-attendance` |
| Attendance report (admin) | ✅ | `AttendanceReportPage.jsx` |
| Student attendance view | ✅ | `ViewAttendancePage.jsx` |
| **PerformanceMetric entity** | ❌ **MISSING** | Entity exists but is **commented out** in compliance-service |
| **Performance trend over time** | ❌ **MISSING** | No time-series charts |

**Changes needed:**
1. Activate `PerformanceMetric` entity in a dedicated service (suggest a new `performance-service` or put under `exam-service`/`compliance-service`).
2. Auto-compute on grade entry: aggregate `(studentEmail, courseCode, examType, score, date)`.
3. Add scheduled job (`@Scheduled`) to compute monthly/quarterly performance snapshots.
4. Add endpoints:
   - `GET /performance/student/{email}` — time series
   - `GET /performance/class/{classId}/trend`
5. Add Recharts line/area charts on:
   - `student/ViewGradesPage` — grade trend
   - `teacher/TeacherDashboard` — class average trend
   - `schoolAdmin/SchoolAdminDashboard` — school-wide trend

---

## 2.7 Compliance & Audit Management

**PDF Entities:** `ComplianceRecord(ComplianceID, EntityID, Type[Course/Exam/Attendance], Result, Date, Notes)`, `Audit(AuditID, OfficerID, Scope, Findings, Date, Status)`

| Item | Status | Notes |
|---|---|---|
| Rule entity (compliance rules) | ✅ | Board officer creates, regulator reviews, compliance officer activates |
| Rule lifecycle (create → review → activate) | ✅ | 3-role flow exists |
| **ComplianceRecord entity** | ❌ Commented out |
| **Audit entity** | ❌ Commented out |
| **Audit against actual data** (course/exam/attendance) | ❌ **MISSING** | Rules exist but aren't evaluated against real entities |
| Compliance dashboard | ⚠️ Basic | KPIs only, no real audit execution |

**Changes needed:**
1. Re-enable `ComplianceRecord` and `Audit` entities (remove commented-out code in `compliance-service/.../entity/`).
2. Build a **compliance evaluator** — scheduled job that:
   - Pulls active rules
   - Queries course/exam/attendance services via Feign
   - Evaluates rule (e.g., "minimum 75% attendance", "minimum 40% pass rate")
   - Writes a `ComplianceRecord` per (school, rule, period) with `Result` = COMPLIANT/NON_COMPLIANT
3. Add `Audit` write workflow: when compliance officer reviews findings, an `Audit` row is created with `Findings`, `Scope`, `Status`.
4. Add endpoints:
   - `GET /compliance/records?schoolId=&type=`
   - `POST /compliance/audits` (officer files audit)
   - `GET /compliance/audits` (regulator/board reviews)
5. Frontend additions:
   - `compliance/ComplianceRecordsPage.jsx` — list of records by school/type
   - `compliance/CreateAuditPage.jsx` — file an audit

---

## 2.8 Reporting & Analytics

**PDF Entities:** `Report(ReportID, Scope[Student/Course/Exam], Metrics, GeneratedDate)`

| Item | Status | Notes |
|---|---|---|
| **Report entity** | ❌ **MISSING** | No persistence of generated reports |
| **Report generation service** | ❌ **MISSING** | No PDF/CSV export |
| Dashboards (KPIs) | ⚠️ Partial | Static counters exist; missing the required KPIs |
| **Required KPIs (NFR §8)** | ❌ | No exam pass rate, attendance %, compliance adherence as trend metrics |

**Changes needed:**
1. Create a new `reporting-service` (or add a module to compliance-service).
2. `Report` entity: `id, scope (STUDENT/COURSE/EXAM/SCHOOL), filters (JSON), metrics (JSON), generatedBy, generatedAt, fileId (GridFS PDF/CSV)`.
3. Endpoints:
   - `POST /reports/generate` — accepts scope + filters, computes metrics
   - `GET /reports` — list previously generated
   - `GET /reports/{id}/download` — download PDF/CSV
4. Use Apache POI (Excel) or iText/OpenPDF (PDF) for export.
5. Add **KPI cards** on every dashboard:
   - Exam pass rate (%)
   - Average attendance (%)
   - Compliance adherence (%)
   - Material engagement (downloads/student)
6. Add a `shared/ReportsPage.jsx` with role-based scope picker.

---

## 2.9 Notifications & Alerts

**PDF Entity:** `Notification(NotificationID, UserID, EntityID, Message, Category[Enrollment/Course/Exam/Compliance], Status, CreatedDate)`

| Item | Status | Notes |
|---|---|---|
| Notification entity | ✅ | `id, recipientEmail, title, message, type, readStatus, scheduledAt` |
| Send + schedule | ✅ | Manual via `ComposeNotificationPage` |
| Inbox + mark read | ✅ | `InboxPage.jsx` |
| **Explicit Categories** (Enrollment/Course/Exam/Compliance) | ❌ **MISSING** | Only generic `type` string |
| **EntityID** (linking notification to a course/exam/etc.) | ❌ **MISSING** | |
| **SMS channel** | ❌ **MISSING** | |
| **Email channel** | ❌ **MISSING** | No SMTP integration |
| **Auto-triggered notifications** | ❌ **MISSING** | Manual compose only — PDF expects deadline/schedule/compliance reminders |

**Changes needed:**
1. Add to `Notification` entity: `category` enum (ENROLLMENT, COURSE, EXAM, COMPLIANCE, ATTENDANCE, GENERAL), `entityId`, `entityType`, `channel` (IN_APP, EMAIL, SMS).
2. Add **event-driven notification triggers** (Kafka/RabbitMQ or simple service calls):
   - On enrollment → notify student
   - On assignment created → notify enrolled students
   - On exam scheduled → notify students 24h/1h before
   - On grade published → notify student
   - On compliance audit failure → notify school admin
3. Add **Email integration** (Spring `JavaMailSender`) and **SMS integration** (Twilio/MSG91 or a stub).
4. Frontend:
   - Add **category filter dropdown** to `InboxPage`
   - Add **channel selector** to `ComposeNotificationPage`
   - Show category badges (color-coded) on each notification

---

## Non-Functional Requirements (PDF §8)

| NFR | Status |
|---|---|
| 50,000 concurrent users | ⚠️ Not load-tested; no horizontal scaling config (K8s/Docker Swarm) |
| Role-based access | ✅ |
| Encrypted data storage | ⚠️ Passwords BCrypt-hashed; **no DB-level encryption-at-rest** noted |
| **Immutable audit logs** | ❌ Not implemented (see §2.1) |
| Multi-school / multi-board | ⚠️ Schema supports `schoolId`, but no board hierarchy / tenant isolation |
| 99.9% uptime | ❌ No HA config, no health-check dashboard |
| **API versioning** (`/api/v1/`) | ❌ Not implemented |
| **Automated migrations** | ❌ No Flyway/Liquibase — using raw `database-schema.sql` |
| **Observability** (centralized logging, KPIs) | ❌ Local file logs only |

**Changes needed:**
1. Adopt **Flyway** or **Liquibase** for versioned DB migrations.
2. Adopt **`/api/v1/...`** prefix on all controllers + gateway routes.
3. Add **Spring Boot Actuator** health endpoints (`/actuator/health`) — partially present.
4. Add **Micrometer + Prometheus** + Grafana for KPI dashboards.
5. Add **Sleuth/OpenTelemetry + Zipkin** for distributed tracing.
6. Add **rate limiting** on api-gateway (Bucket4j / Resilience4j).
7. Add **CORS** + **Helmet-equivalent security headers** centrally.
8. **Dockerize each service** + add `docker-compose.yml` (probably already partly done — verify).

---

## Summary — Prioritized Action List

### 🔴 Critical (PDF mandatory, currently missing)
1. **Audit Log** entity + auto-write + viewer (§2.1)
2. **Re-enable ComplianceRecord + Audit** entities (§2.7) — currently commented out
3. **PerformanceMetric** service (§2.6) — entity commented out
4. **Report entity + generation** service with PDF/CSV export (§2.8)
5. **Notification categories + channels (SMS/email)** + auto-triggers (§2.9)
6. **StudentDocument** upload + verification (§2.2)

### 🟡 Important (PDF feature gaps)
7. Add `credits`, `status` to Course; `schedule`, `status` to Class (§2.3)
8. Material usage tracking (§2.4)
9. Exam status lifecycle; Grade DRAFT/PUBLISHED (§2.5)
10. KPI cards (pass rate, attendance %, compliance %) on dashboards (§2.8)

### 🟢 Quality / NFR
11. Flyway/Liquibase migrations
12. `/api/v1/` versioning
13. Centralized logging (ELK or Loki)
14. Distributed tracing (Zipkin)
15. Rate limiting on gateway

### 🔵 Tech debt (orthogonal to PDF but worth fixing)
16. Replace direct entity returns with DTOs across all services
17. Add unit + integration tests (none currently)
18. Standardize error response shape via `@ControllerAdvice`
