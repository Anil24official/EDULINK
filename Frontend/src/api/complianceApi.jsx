import httpClient from "./httpClient";
import Endpoints from "../config/endpoints";

const complianceApi = {
  /* ── Compliance Officer ── */
  auditSchool: (payload) =>
    httpClient.post(Endpoints.compliance.auditSchool, payload),

  fetchComplianceStatus: () =>
    httpClient.get(Endpoints.compliance.complianceStatus),

  registerSchool: (payload) =>
    httpClient.post(Endpoints.compliance.registerSchool, payload),

  fetchAuditRecords: () =>
    httpClient.get(Endpoints.compliance.auditRecords),

  /* ── Rules ── */
  fetchAllRules: () =>
    httpClient.get(Endpoints.compliance.allRules),

  fetchReviewRules: () =>
    httpClient.get(Endpoints.compliance.allReviewRules),

  validateRule: (id, status) =>
    httpClient.put(Endpoints.compliance.validateRule(id, status)),

  /* ── Compliance Records (PDF 4.7) ── */
  fetchComplianceRecords: (params) =>
    httpClient.get(Endpoints.compliance.records, { params }),

  fetchComplianceRecord: (id) =>
    httpClient.get(Endpoints.compliance.recordById(id)),

  createComplianceRecord: (payload) =>
    httpClient.post(Endpoints.compliance.records, payload),

  updateComplianceRecord: (id, payload) =>
    httpClient.put(Endpoints.compliance.recordById(id), payload),

  deleteComplianceRecord: (id) =>
    httpClient.delete(Endpoints.compliance.recordById(id)),

  /* ── Audits (PDF 4.7) ── */
  fetchAudits: (params) =>
    httpClient.get(Endpoints.compliance.audits, { params }),

  fetchAudit: (id) =>
    httpClient.get(Endpoints.compliance.auditById(id)),

  createAudit: (payload) =>
    httpClient.post(Endpoints.compliance.audits, payload),

  updateAudit: (id, payload) =>
    httpClient.put(Endpoints.compliance.auditById(id), payload),

  updateAuditStatus: (id, status) =>
    httpClient.patch(Endpoints.compliance.auditStatus(id, status)),

  deleteAudit: (id) =>
    httpClient.delete(Endpoints.compliance.auditById(id)),

  /* ── Reports (PDF 4.8) ── */
  fetchReports: (params) =>
    httpClient.get(Endpoints.compliance.reportsStore, { params }),

  fetchReport: (id) =>
    httpClient.get(Endpoints.compliance.reportById(id)),

  createReport: (payload) =>
    httpClient.post(Endpoints.compliance.reportsStore, payload),

  generateReport: (payload) =>
    httpClient.post(Endpoints.compliance.reportGenerate, payload),

  updateReport: (id, payload) =>
    httpClient.put(Endpoints.compliance.reportById(id), payload),

  deleteReport: (id) =>
    httpClient.delete(Endpoints.compliance.reportById(id)),

  /* ── KPIs (PDF §8 — Observability) ── */
  fetchComplianceKpis: (params) =>
    httpClient.get(Endpoints.compliance.complianceKpis, { params }),

  fetchAggregatedKpis: (params) =>
    httpClient.get(Endpoints.compliance.aggregatedKpis, { params }),

  /* ── Education Board Officer ── */
  fetchBoardSchools: () =>
    httpClient.get(Endpoints.compliance.boardSchools),

  fetchAcademicPerformance: (params) =>
    httpClient.get(Endpoints.compliance.boardAcademicPerformance, { params }),

  fetchBoardReports: () =>
    httpClient.get(Endpoints.compliance.boardReports),

  fetchComplianceSummary: () =>
    httpClient.get(Endpoints.compliance.boardComplianceSummary),

  /* ── Regulator ── */
  fetchRegulatorComplianceReports: () =>
    httpClient.get(Endpoints.compliance.regulatorComplianceReports),

  fetchAccreditationStatus: () =>
    httpClient.get(Endpoints.compliance.regulatorAccreditation),

  fetchSystemAudit: () =>
    httpClient.get(Endpoints.compliance.regulatorSystemAudit),
};

export default complianceApi;
