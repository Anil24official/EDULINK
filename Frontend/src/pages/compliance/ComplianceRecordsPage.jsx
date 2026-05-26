import React, { useEffect, useState, useCallback } from "react";
import complianceApi from "../../api/complianceApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

const TYPE_OPTIONS = ["Course", "Exam", "Attendance"];
const RESULT_OPTIONS = ["PASS", "FAIL", "REVIEW"];

const initialForm = {
  entityId: "",
  type: "Course",
  result: "PASS",
  schoolId: "",
  notes: "",
};

export default function ComplianceRecordsPage() {
  const [records, setRecords] = useState([]);
  const [filter, setFilter] = useState({ type: "", schoolId: "", entityId: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchRecords = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = {};
      if (filter.type) params.type = filter.type;
      if (filter.schoolId) params.schoolId = filter.schoolId;
      if (filter.entityId) params.entityId = filter.entityId;
      const res = await complianceApi.fetchComplianceRecords(params);
      const list = res?.data?.data ?? res?.data ?? [];
      setRecords(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    fetchRecords();
  }, [fetchRecords]);

  const onFormChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const onFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter((f) => ({ ...f, [name]: value }));
  };

  const resetForm = () => {
    setForm(initialForm);
    setEditingId(null);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSubmitting(true);
    try {
      if (editingId) {
        await complianceApi.updateComplianceRecord(editingId, form);
        setSuccess("Compliance record updated.");
      } else {
        await complianceApi.createComplianceRecord(form);
        setSuccess("Compliance record created.");
      }
      resetForm();
      fetchRecords();
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const onEdit = (row) => {
    setEditingId(row.id);
    setForm({
      entityId: row.entityId || "",
      type: row.type || "Course",
      result: row.result || "PASS",
      schoolId: row.schoolId || "",
      notes: row.notes || "",
    });
  };

  const onDelete = async (id) => {
    if (!window.confirm("Delete this compliance record?")) return;
    try {
      await complianceApi.deleteComplianceRecord(id);
      setSuccess("Record deleted.");
      fetchRecords();
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const columns = [
    { key: "id", label: "ID" },
    { key: "type", label: "Type" },
    { key: "entityId", label: "Entity ID" },
    { key: "result", label: "Result" },
    { key: "schoolId", label: "School" },
    {
      key: "recordedDate",
      label: "Date",
      render: (row) => row.recordedDate ? new Date(row.recordedDate).toLocaleString() : "—",
    },
    {
      key: "notes",
      label: "Notes",
      render: (row) => row.notes
        ? (row.notes.length > 60 ? row.notes.slice(0, 60) + "…" : row.notes)
        : "—",
    },
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
          title="Compliance Records"
          subtitle="Capture audit outcomes for courses, exams and attendance."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />
        <AlertBanner type="success" message={success} onClose={() => setSuccess("")} />

        {/* Create / Edit Form */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">
            {editingId ? `Edit Record #${editingId}` : "New Compliance Record"}
          </h3>
          <form onSubmit={onSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Type *</label>
              <select
                name="type"
                value={form.type}
                onChange={onFormChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                {TYPE_OPTIONS.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Entity ID *</label>
              <input
                type="text"
                name="entityId"
                value={form.entityId}
                onChange={onFormChange}
                placeholder="e.g. CSE101, EXAM-MID-1"
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Result *</label>
              <select
                name="result"
                value={form.result}
                onChange={onFormChange}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                {RESULT_OPTIONS.map((r) => <option key={r} value={r}>{r}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">School ID</label>
              <input
                type="text"
                name="schoolId"
                value={form.schoolId}
                onChange={onFormChange}
                placeholder="optional"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Notes</label>
              <textarea
                name="notes"
                value={form.notes}
                onChange={onFormChange}
                rows={2}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
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
                {submitting ? "Saving…" : editingId ? "Update Record" : "Create Record"}
              </button>
            </div>
          </form>
        </div>

        {/* Filter */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6 grid grid-cols-1 md:grid-cols-4 gap-3">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Filter Type</label>
            <select
              name="type"
              value={filter.type}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">All</option>
              {TYPE_OPTIONS.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
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
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Entity ID</label>
            <input
              type="text"
              name="entityId"
              value={filter.entityId}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
          <div className="flex items-end">
            <button
              type="button"
              onClick={() => setFilter({ type: "", schoolId: "", entityId: "" })}
              className="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 text-sm"
            >
              Clear Filters
            </button>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white rounded-lg shadow-md p-4">
          {loading ? (
            <Spinner />
          ) : (
            <GenericTable
              columns={columns}
              data={records}
              emptyMessage="No compliance records found."
            />
          )}
        </div>
      </div>
    </div>
  );
}
