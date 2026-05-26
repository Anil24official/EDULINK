import React, { useEffect, useState, useCallback } from "react";
import studentApi from "../../api/studentApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

const DOC_TYPES = ["IDProof", "ReportCard"];

function statusBadgeClass(status) {
  switch ((status || "").toUpperCase()) {
    case "VERIFIED": return "bg-green-100 text-green-800";
    case "PENDING":  return "bg-yellow-100 text-yellow-800";
    case "REJECTED": return "bg-red-100 text-red-800";
    default:         return "bg-gray-100 text-gray-700";
  }
}

export default function MyDocumentsPage() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [docType, setDocType] = useState("IDProof");
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await studentApi.fetchMyDocuments();
      const list = res?.data?.data ?? res?.data ?? [];
      setDocs(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const onUpload = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (!file) {
      setError("Please select a file to upload.");
      return;
    }
    setUploading(true);
    try {
      await studentApi.uploadDocument(docType, file);
      setSuccess(`${docType} uploaded — awaiting verification.`);
      setFile(null);
      load();
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setUploading(false);
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

  const onDelete = async (id) => {
    if (!window.confirm("Delete this document?")) return;
    try {
      await studentApi.deleteDocument(id);
      setSuccess("Document deleted.");
      load();
    } catch (err) {
      setError(parseApiError(err));
    }
  };

  const columns = [
    { key: "docType", label: "Type" },
    { key: "fileName", label: "File" },
    {
      key: "fileSize",
      label: "Size",
      render: (row) => row.fileSize ? `${(row.fileSize / 1024).toFixed(1)} KB` : "—",
    },
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
      key: "actions",
      label: "Actions",
      render: (row) => (
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => onDownload(row)}
            className="px-2 py-1 text-xs bg-blue-50 text-blue-700 rounded hover:bg-blue-100"
          >
            Download
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
      <div className="max-w-5xl mx-auto">
        <SectionHeader
          title="My Documents"
          subtitle="Upload ID Proof and Report Cards for school verification."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />
        <AlertBanner type="success" message={success} onClose={() => setSuccess("")} />

        {/* Upload form */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">Upload New Document</h3>
          <form onSubmit={onUpload} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Document Type *</label>
              <select
                value={docType}
                onChange={(e) => setDocType(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                {DOC_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">File *</label>
              <input
                type="file"
                onChange={(e) => setFile(e.target.files?.[0] || null)}
                className="w-full text-sm text-gray-700"
              />
            </div>
            <div>
              <button
                type="submit"
                disabled={uploading || !file}
                className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {uploading ? "Uploading…" : "Upload"}
              </button>
            </div>
          </form>
        </div>

        {/* List */}
        <div className="bg-white rounded-lg shadow-md p-4">
          {loading ? (
            <Spinner />
          ) : (
            <GenericTable
              columns={columns}
              data={docs}
              emptyMessage="No documents uploaded yet."
            />
          )}
        </div>
      </div>
    </div>
  );
}
