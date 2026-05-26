import React, { useEffect, useState, useCallback } from "react";
import studentApi from "../../api/studentApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import GenericTable from "../../components/shared/GenericTable";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

export default function MyPerformancePage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [courseFilter, setCourseFilter] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = {};
      if (courseFilter) params.courseId = Number(courseFilter);
      const res = await studentApi.fetchMyPerformanceMetrics(params);
      const list = res?.data?.data ?? res?.data ?? [];
      setRows(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [courseFilter]);

  useEffect(() => { load(); }, [load]);

  const columns = [
    {
      key: "metricDate",
      label: "Date",
      render: (row) => row.metricDate ? new Date(row.metricDate).toLocaleDateString() : "—",
    },
    { key: "metricType", label: "Metric" },
    { key: "courseId", label: "Course" },
    { key: "score", label: "Score" },
    { key: "status", label: "Status" },
  ];

  const avgScore = rows.length > 0
    ? (rows.reduce((sum, r) => sum + (Number(r.score) || 0), 0) / rows.length).toFixed(2)
    : "—";

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-5xl mx-auto">
        <SectionHeader
          title="My Performance"
          subtitle="Per-course performance metrics recorded by your teachers."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow-md p-4">
            <div className="text-xs text-gray-500">Metrics Recorded</div>
            <div className="text-2xl font-semibold text-gray-800">{rows.length}</div>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <div className="text-xs text-gray-500">Average Score</div>
            <div className="text-2xl font-semibold text-gray-800">{avgScore}</div>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <label className="block text-xs font-medium text-gray-500 mb-1">Filter Course ID</label>
            <input
              type="number"
              value={courseFilter}
              onChange={(e) => setCourseFilter(e.target.value)}
              placeholder="all courses"
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-4">
          {loading ? (
            <Spinner />
          ) : (
            <GenericTable
              columns={columns}
              data={rows}
              emptyMessage="No performance metrics recorded yet."
            />
          )}
        </div>
      </div>
    </div>
  );
}
