import httpClient from "./httpClient";
import Endpoints from "../config/endpoints";

const attendanceApi = {
  /* ── Teacher ── */
  markAttendance: (payload) =>
    httpClient.post(Endpoints.attendance.markAttendance, payload),

  /** Save Present/Absent/OD for an entire class on a given date in one request. */
  markAttendanceBulk: (payload) =>
    httpClient.post(Endpoints.attendance.markAttendanceBulk, payload),

  /** Fetch existing attendance for a class+date so the marking table can pre-fill. */
  fetchClassAttendance: ({ courseId, date }) =>
    httpClient.get(Endpoints.attendance.teacherClassAttendance, {
      params: { courseId, date },
    }),

  /* ── Student ── */
  fetchStudentAttendance: () =>
    httpClient.get(Endpoints.attendance.studentAttendance),

  /* ── School Admin ── */
  fetchAdminReport: (params) =>
    httpClient.get(Endpoints.attendance.adminReport, { params }),

  /* ── Performance Metrics (PDF 4.6) ── */
  fetchTeacherMetrics: (params) =>
    httpClient.get(Endpoints.attendance.teacherMetrics, { params }),

  fetchAdminMetrics: (params) =>
    httpClient.get(Endpoints.attendance.adminMetrics, { params }),

  fetchStudentMetrics: (params) =>
    httpClient.get(Endpoints.attendance.studentMetrics, { params }),

  createMetric: (payload) =>
    httpClient.post(Endpoints.attendance.teacherMetrics, payload),

  updateMetric: (id, payload) =>
    httpClient.put(Endpoints.attendance.teacherMetricById(id), payload),

  deleteMetric: (id) =>
    httpClient.delete(Endpoints.attendance.teacherMetricById(id)),
};

export default attendanceApi;
