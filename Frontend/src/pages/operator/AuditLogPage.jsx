import React, { useEffect, useState, useCallback } from "react";
import identityApi from "../../api/identityApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

function statusBadgeClass(status) {
  switch ((status || "").toUpperCase()) {
    case "SUCCESS": return "bg-green-100 text-green-800";
    case "FAILURE": return "bg-red-100 text-red-800";
    default:        return "bg-gray-100 text-gray-700";
  }
}

export default function AuditLogPage() {
  const [logs, setLogs] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [filter, setFilter] = useState({ userEmail: "", action: "" });
  const [page, setPage] = useState(0);
  const [size] = useState(50);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = { page, size };
      if (filter.userEmail) params.userEmail = filter.userEmail;
      if (filter.action) params.action = filter.action;
      const res = await identityApi.fetchAuditLogs(params);
      const body = res?.data?.data || res?.data || {};
      setLogs(Array.isArray(body.content) ? body.content : Array.isArray(body) ? body : []);
      setTotalElements(body.totalElements ?? 0);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [page, size, filter]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  const onFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter((f) => ({ ...f, [name]: value }));
    setPage(0);
  };

  const columns = [
    { key: "id", label: "ID" },
    {
      key: "timestamp",
      label: "When",
      render: (row) => row.timestamp ? new Date(row.timestamp).toLocaleString() : "—",
    },
    { key: "userEmail", label: "User" },
    { key: "userRole", label: "Role" },
    { key: "action", label: "Action" },
    { key: "resource", label: "Resource" },
    {
      key: "status",
      label: "Status",
      render: (row) => (
        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${statusBadgeClass(row.status)}`}>
          {row.status}
        </span>
      ),
    },
    { key: "ipAddress", label: "IP" },
    {
      key: "details",
      label: "Details",
      render: (row) => row.details
        ? (row.details.length > 60 ? row.details.slice(0, 60) + "…" : row.details)
        : "—",
    },
  ];

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <SectionHeader
          title="Audit Logs"
          subtitle="System-wide record of authentication and identity actions."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6 grid grid-cols-1 md:grid-cols-3 gap-3">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">User Email</label>
            <input
              type="text"
              name="userEmail"
              value={filter.userEmail}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
              placeholder="exact match"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Action</label>
            <select
              name="action"
              value={filter.action}
              onChange={onFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">All</option>
              <option value="LOGIN">LOGIN</option>
              <option value="CHANGE_PASSWORD">CHANGE_PASSWORD</option>
              <option value="UPDATE_PROFILE">UPDATE_PROFILE</option>
            </select>
          </div>
          <div className="flex items-end">
            <button
              type="button"
              onClick={() => { setFilter({ userEmail: "", action: "" }); setPage(0); }}
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
            <>
              <GenericTable
                columns={columns}
                data={logs}
                emptyMessage="No audit logs found."
                pageSize={size}
                showPagination={false}
              />
              {/* Server-side pagination controls */}
              <div className="flex items-center justify-between mt-3">
                <div className="text-sm text-gray-600">
                  Showing page {page + 1}{totalElements > 0 ? ` (${totalElements} total)` : ""}
                </div>
                <div className="flex gap-2">
                  <button
                    type="button"
                    disabled={page === 0}
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                    className="px-3 py-1.5 text-sm bg-gray-100 text-gray-700 rounded hover:bg-gray-200 disabled:opacity-50"
                  >
                    ← Previous
                  </button>
                  <button
                    type="button"
                    disabled={logs.length < size}
                    onClick={() => setPage((p) => p + 1)}
                    className="px-3 py-1.5 text-sm bg-gray-100 text-gray-700 rounded hover:bg-gray-200 disabled:opacity-50"
                  >
                    Next →
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
