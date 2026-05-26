import React, { useEffect, useState, useCallback } from "react";
import attendanceApi from "../../api/attendanceApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

const METRIC_TYPES = ["Quiz", "Project", "Participation", "Homework", "Overall"];
const STATUS_OPTIONS = ["ACTIVE", "ARCHIVED"];

const todayIso = () => new Date().toISOString().slice(0, 10);

const initialForm = {
  rollNumber: "",
  courseId: "",
  schoolId: "",
  score: "",
  metricType: "Quiz",
  metricDate: todayIso(),
  status: "ACTIVE",
};

export default function PerformanceMetricsPage() {
  const [rows, setRows] = useState([]);
  const [filter, setFilter] = useState({ rollNumber: "", courseId: "", schoolId: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = {};
      if (filter.rollNumber) params.rollNumber = filter.rollNumber;
      if (filter.courseId) params.courseId = filter.courseId;
      if (filter.schoolId) params.schoolId = filter.schoolId;
      const res = await attendanceApi.fetchTeacherMetrics(params);
      const list = res?.data?.data ?? res?.data ?? [];
      setRows(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => { load(); }, [load]);

  const onFormChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const onFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter((f) => ({ ...f, [name]: value }));
  };

  const resetForm = () => { setForm(initialForm); setEditingId(null); };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSubmitting(true);
    try {
      const payload = {
        ...form,
        courseId: form.courseId ? Number(form.courseId) : null,
        score: form.score === "" ? null : Number(form.score),
      };
      if (editingId) {
        await attendanceApi.updateMetric(editingId, payload);
        setSuccess("Performance metric updated.");
      } else {
        await attendanceApi.createMetric(payload);
        setSuccess("Performance metric recorded.");
      }
      resetForm();
      load();
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const onEdit = (row) => {
    setEditingId(row.id);
    setForm({
      rollNumber: row.rollNumber || "",
      courseId: row.courseId != null ? String(row.courseId) : "",
      schoolId: row.schoolId || "",
      score: row.score != null ? String(row.score) : "",
      metricType: row.metricType || "Quiz",
      metricDate: row.metricDate || todayIso(),
      status: row.status || "ACTIVE",
    });
  };

  const onDelete = async (id) => {
    if (!window.confirm("Delete this performance metric?")) return;
    try {
      await attendanceApi.deleteMetric(id);
      setSuccess("Performance metric deleted.");
      load();
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const columns = [
    { key: "id", label: "ID" },
    { key: "rollNumber", label: "Roll #" },
    { key: "courseId", label: "Course" },
    { key: "metricType", label: "Metric" },
    {
      key: "score",
      label: "Score",
      render: (row) => row.score != null ? row.score : "—",
    },
    {
      key: "metricDate",
      label: "Date",
      render: (row) => row.metricDate ? new Date(row.metricDate).toLocaleDateString() : "—",
    },
    { key: "status", label: "Status" },
    {
      key: "actions",
      label: "Actions",
      render: (row) => (
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => onEdit(row)}
            className="px-2 py-1 text-xs bg-blue-50 text-blue-700 rounded hover:bg-blue-100"
          >
            Edit
          </button>
          <button
            type="button"
            onClick={() => onDelete(row.id)}
            className="px-2 py-1 text-xs bg-red-50 text-red-700 rounded hover:bg-red-100"
          >
            Delete
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <SectionHeader
          title="Performance Metrics"
          subtitle="Record and review per-student performance metrics."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />
        <AlertBanner type="success" message={success} onClose={() => setSuccess("")} />

        {/* Create / Edit form */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">
            {editingId ? `Edit Metric #${editingId}` : "Record New Metric"}
          </h3>
          <form onSubmit={onSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Roll Number *</label>
              <input
                type="text"
                name="rollNumber"
                value={form.rollNumber}
                onChange={onFormChange}
                placeholder="e.g. SCH001101"
                pattern="^[A-Z]{3}\d{6}$"
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Course ID *</label>
              <input
                type="number"
                name="courseId"
                value={form.courseId}
                onChange={onFormChange}
                required
                min="1"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">School ID</label>
              <input
                type="text"
                name="schoolId"
                value={form.schoolId}
                onChange={onFormChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Metric Type *</label>
              <select
                name="metricType"
                value={form.metricType}
                onChange={onFormChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                {METRIC_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Score *</label>
              <input
                type="number"
                name="score"
                value={form.score}
                onChange={onFormChange}
                required
                step="0.01"
                min="0"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date *</label>
              <input
                type="date"
                name="metricDate"
                value={form.metricDate}
                onChange={onFormChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <select
                name="status"
                value={form.status}
                onChange={onFormChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div className="md:col-span-3 flex justify-end gap-2">
              {editingId && (
                <button
                  type="button"
                  onClick={resetForm}
                  className="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
              )}
              <button
                type="submit"
                disabled={submitting}
                className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {submitting ? "Saving…" : editingId ? "Update Metric" : "Record Metric"}
              </button>
            </div>
          </form>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6 grid grid-cols-1 md:grid-cols-4 gap-3">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Roll #</label>
            <input
              type="text"
              name="rollNumber"
              value={filter.rollNumber}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Course ID</label>
            <input
              type="number"
              name="courseId"
              value={filter.courseId}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">School ID</label>
            <input
              type="text"
              name="schoolId"
              value={filter.schoolId}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
          <div className="flex items-end">
            <button
              type="button"
              onClick={() => setFilter({ rollNumber: "", courseId: "", schoolId: "" })}
              className="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 text-sm"
            >
              Clear Filters
            </button>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-4">
          {loading ? (
            <Spinner />
          ) : (
            <GenericTable
              columns={columns}
              data={rows}
              emptyMessage="No performance metrics found."
            />
          )}
        </div>
      </div>
    </div>
  );
}
