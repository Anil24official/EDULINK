import React, { useEffect, useState, useCallback } from "react";
import documentsApi from "../../api/documentsApi";
import studentApi from "../../api/studentApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

const STATUSES = ["PENDING", "VERIFIED", "REJECTED"];
const DOC_TYPES = ["IDProof", "ReportCard"];

function statusBadgeClass(status) {
  switch ((status || "").toUpperCase()) {
    case "VERIFIED": return "bg-green-100 text-green-800";
    case "PENDING":  return "bg-yellow-100 text-yellow-800";
    case "REJECTED": return "bg-red-100 text-red-800";
    default:         return "bg-gray-100 text-gray-700";
  }
}

export default function VerifyDocumentsPage() {
  const [docs, setDocs] = useState([]);
  const [filter, setFilter] = useState({ verificationStatus: "PENDING", docType: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [notesById, setNotesById] = useState({});

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = {};
      if (filter.verificationStatus) params.verificationStatus = filter.verificationStatus;
      if (filter.docType) params.docType = filter.docType;
      const res = await documentsApi.adminList(params);
      const list = res?.data?.data ?? res?.data ?? [];
      setDocs(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => { load(); }, [load]);

  const onFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter((f) => ({ ...f, [name]: value }));
  };

  const onVerify = async (id, status) => {
    try {
      const notes = notesById[id] || "";
      await documentsApi.verify(id, { status, notes });
      setSuccess(`Document #${id} marked ${status}.`);
      load();
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const onDownload = async (doc) => {
    try {
      const res = await studentApi.downloadDocument(doc.id);
      const blob = new Blob([res.data], { type: doc.contentType || "application/octet-stream" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = doc.fileName || `document-${doc.id}`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const columns = [
    { key: "id", label: "ID" },
    { key: "studentEmail", label: "Student" },
    { key: "docType", label: "Type" },
    { key: "fileName", label: "File" },
    {
      key: "uploadedDate",
      label: "Uploaded",
      render: (row) => row.uploadedDate ? new Date(row.uploadedDate).toLocaleString() : "—",
    },
    {
      key: "verificationStatus",
      label: "Status",
      render: (row) => (
        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${statusBadgeClass(row.verificationStatus)}`}>
          {row.verificationStatus}
        </span>
      ),
    },
    {
      key: "notes",
      label: "Notes",
      render: (row) => (
        <input
          type="text"
          value={notesById[row.id] ?? row.verificationNotes ?? ""}
          onChange={(e) => setNotesById((m) => ({ ...m, [row.id]: e.target.value }))}
          placeholder="optional notes"
          className="w-44 px-2 py-1 border border-gray-300 rounded text-xs"
        />
      ),
    },
    {
      key: "actions",
      label: "Actions",
      render: (row) => (
        <div className="flex flex-wrap gap-1">
          <button
            type="button"
            onClick={() => onDownload(row)}
            className="px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded hover:bg-gray-200"
          >
            Download
          </button>
          <button
            type="button"
            onClick={() => onVerify(row.id, "VERIFIED")}
            className="px-2 py-1 text-xs bg-green-50 text-green-700 rounded hover:bg-green-100"
          >
            Approve
          </button>
          <button
            type="button"
            onClick={() => onVerify(row.id, "REJECTED")}
            className="px-2 py-1 text-xs bg-red-50 text-red-700 rounded hover:bg-red-100"
          >
            Reject
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <SectionHeader
          title="Verify Student Documents"
          subtitle="Approve or reject ID Proofs and Report Cards submitted by students."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />
        <AlertBanner type="success" message={success} onClose={() => setSuccess("")} />

        <div className="bg-white rounded-lg shadow-md p-4 mb-6 grid grid-cols-1 md:grid-cols-3 gap-3">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Status</label>
            <select
              name="verificationStatus"
              value={filter.verificationStatus}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">All</option>
              {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Document Type</label>
            <select
              name="docType"
              value={filter.docType}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">All</option>
              {DOC_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div className="flex items-end">
            <button
              type="button"
              onClick={() => setFilter({ verificationStatus: "", docType: "" })}
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
              data={docs}
              emptyMessage="No documents to verify."
            />
          )}
        </div>
      </div>
    </div>
  );
}
