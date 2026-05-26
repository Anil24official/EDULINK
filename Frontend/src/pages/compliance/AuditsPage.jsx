import React, { useEffect, useState, useCallback } from "react";
import complianceApi from "../../api/complianceApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

const STATUS_OPTIONS = ["OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"];

const initialForm = {
  scope: "",
  findings: "",
  status: "OPEN",
  schoolId: "",
};

function statusBadgeClass(status) {
  switch ((status || "").toUpperCase()) {
    case "OPEN":        return "bg-yellow-100 text-yellow-800";
    case "IN_PROGRESS": return "bg-blue-100 text-blue-800";
    case "RESOLVED":    return "bg-green-100 text-green-800";
    case "CLOSED":      return "bg-gray-200 text-gray-700";
    default:            return "bg-gray-100 text-gray-700";
  }
}

export default function AuditsPage() {
  const [audits, setAudits] = useState([]);
  const [filter, setFilter] = useState({ status: "", schoolId: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchAudits = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = {};
      if (filter.status) params.status = filter.status;
      if (filter.schoolId) params.schoolId = filter.schoolId;
      const res = await complianceApi.fetchAudits(params);
      const list = res?.data?.data ?? res?.data ?? [];
      setAudits(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    fetchAudits();
  }, [fetchAudits]);

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
        await complianceApi.updateAudit(editingId, form);
        setSuccess("Audit updated.");
      } else {
        await complianceApi.createAudit(form);
        setSuccess("Audit created.");
      }
      resetForm();
      fetchAudits();
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const onEdit = (row) => {
    setEditingId(row.id);
    setForm({
      scope: row.scope || "",
      findings: row.findings || "",
      status: row.status || "OPEN",
      schoolId: row.schoolId || "",
    });
  };

  const onStatusChange = async (id, status) => {
    try {
      await complianceApi.updateAuditStatus(id, status);
      setSuccess(`Audit #${id} marked ${status}.`);
      fetchAudits();
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const onDelete = async (id) => {
    if (!window.confirm("Delete this audit?")) return;
    try {
      await complianceApi.deleteAudit(id);
      setSuccess("Audit deleted.");
      fetchAudits();
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const columns = [
    { key: "id", label: "ID" },
    { key: "scope", label: "Scope" },
    { key: "officerEmail", label: "Officer" },
    { key: "schoolId", label: "School" },
    {
      key: "auditDate",
      label: "Date",
      render: (row) => row.auditDate ? new Date(row.auditDate).toLocaleString() : "—",
    },
    {
      key: "findings",
      label: "Findings",
      render: (row) => row.findings
        ? (row.findings.length > 60 ? row.findings.slice(0, 60) + "…" : row.findings)
        : "—",
    },
    {
      key: "status",
      label: "Status",
      render: (row) => (
        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${statusBadgeClass(row.status)}`}>
          {row.status}
        </span>
      ),
    },
    {
      key: "actions",
      label: "Actions",
      render: (row) => (
        <div className="flex flex-wrap gap-1">
          <select
            value={row.status || ""}
            onChange={(e) => onStatusChange(row.id, e.target.value)}
            className="text-xs px-2 py-1 border border-gray-300 rounded"
          >
            {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
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
          title="Audits"
          subtitle="Open, track and close compliance audits across schools."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />
        <AlertBanner type="success" message={success} onClose={() => setSuccess("")} />

        {/* Create / Edit Form */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">
            {editingId ? `Edit Audit #${editingId}` : "New Audit"}
          </h3>
          <form onSubmit={onSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Scope *</label>
              <input
                type="text"
                name="scope"
                value={form.scope}
                onChange={onFormChange}
                placeholder="e.g. Q2 Course Audit, Exam Integrity Review"
                required
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
                placeholder="optional"
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
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Findings</label>
              <textarea
                name="findings"
                value={form.findings}
                onChange={onFormChange}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div className="md:col-span-2 flex justify-end gap-2">
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
                {submitting ? "Saving…" : editingId ? "Update Audit" : "Create Audit"}
              </button>
            </div>
          </form>
        </div>

        {/* Filter */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6 grid grid-cols-1 md:grid-cols-3 gap-3">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Filter Status</label>
            <select
              name="status"
              value={filter.status}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">All</option>
              {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
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
          <div className="flex items-end">
            <button
              type="button"
              onClick={() => setFilter({ status: "", schoolId: "" })}
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
              data={audits}
              emptyMessage="No audits found."
            />
          )}
        </div>
      </div>
    </div>
  );
}
