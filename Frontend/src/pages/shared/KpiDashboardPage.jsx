import React, { useEffect, useState, useCallback } from "react";
import complianceApi from "../../api/complianceApi";
import SectionHeader from "../../components/shared/SectionHeader";
import AlertBanner from "../../components/shared/AlertBanner";
import Spinner from "../../components/shared/Spinner";
import { parseApiError } from "../../utils/apiErrorParser";

function pctClass(value) {
  if (value == null) return "text-gray-500";
  if (value >= 80) return "text-green-600";
  if (value >= 50) return "text-amber-600";
  return "text-red-600";
}

function KpiCard({ title, value, suffix, hint, accent }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="text-xs font-medium text-gray-500 uppercase tracking-wide">{title}</div>
      <div className={`mt-2 text-4xl font-bold ${accent || "text-gray-800"}`}>
        {value}
        {suffix && <span className="text-xl text-gray-500 ml-1">{suffix}</span>}
      </div>
      {hint && <div className="mt-2 text-sm text-gray-500">{hint}</div>}
    </div>
  );
}

export default function KpiDashboardPage() {
  const [kpis, setKpis] = useState(null);
  const [schoolId, setSchoolId] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const params = schoolId ? { schoolId } : {};
      const res = await complianceApi.fetchAggregatedKpis(params);
      setKpis(res?.data?.data || res?.data || null);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setLoading(false);
    }
  }, [schoolId]);

  useEffect(() => { load(); }, [load]);

  const fmt = (n) => (n == null ? "—" : Number(n).toLocaleString());
  const pct = (n) => (n == null ? "—" : `${Number(n).toFixed(2)}`);

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <SectionHeader
          title="KPI Dashboard"
          subtitle="Named KPIs from PDF §8 — exam pass rate, attendance %, compliance adherence."
        />

        <AlertBanner type="error" message={error} onClose={() => setError("")} />

        {/* Filter */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6 grid grid-cols-1 md:grid-cols-3 gap-3 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">School ID (optional)</label>
            <input
              type="text"
              value={schoolId}
              onChange={(e) => setSchoolId(e.target.value)}
              placeholder="leave blank for system-wide"
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
          <div>
            <button
              type="button"
              onClick={load}
              disabled={loading}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? "Loading…" : "Refresh"}
            </button>
          </div>
          <div className="text-xs text-gray-500">
            Aggregator pulls live data from exam-service, attendance-service, and compliance-service.
          </div>
        </div>

        {loading && !kpis ? (
          <Spinner />
        ) : kpis ? (
          <>
            {/* The three PDF §8 named KPIs */}
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Named KPIs (PDF §8)</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
              <KpiCard
                title="Exam Pass Rate"
                value={pct(kpis.examPassRate)}
                suffix="%"
                hint={`${fmt(kpis.passingGrades)} / ${fmt(kpis.totalGrades)} grades passing`}
                accent={pctClass(kpis.examPassRate)}
              />
              <KpiCard
                title="Attendance %"
                value={pct(kpis.attendancePercentage)}
                suffix="%"
                hint={`${fmt(kpis.presentCount)} / ${fmt(kpis.totalAttendanceRecords)} records present`}
                accent={pctClass(kpis.attendancePercentage)}
              />
              <KpiCard
                title="Compliance Adherence"
                value={pct(kpis.complianceAdherenceRate)}
                suffix="%"
                hint={`${fmt(kpis.passingComplianceRecords)} / ${fmt(kpis.totalComplianceRecords)} records PASS`}
                accent={pctClass(kpis.complianceAdherenceRate)}
              />
            </div>

            {/* Supporting compliance metrics */}
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Compliance Detail</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
              <KpiCard
                title="Rule Activation Rate"
                value={pct(kpis.ruleActivationRate)}
                suffix="%"
                hint={`${fmt(kpis.activeRules)} / ${fmt(kpis.totalRules)} rules active`}
                accent={pctClass(kpis.ruleActivationRate)}
              />
              <KpiCard
                title="Audit Closure Rate"
                value={pct(kpis.auditClosureRate)}
                suffix="%"
                hint={`${fmt(kpis.closedAudits)} / ${fmt(kpis.totalAudits)} audits closed`}
                accent={pctClass(kpis.auditClosureRate)}
              />
              <KpiCard
                title="Scope"
                value={kpis.schoolId || "System-wide"}
                hint={kpis.schoolId ? "Filtered to a single school" : "No school filter applied"}
              />
            </div>
          </>
        ) : (
          <div className="bg-white rounded-lg shadow-md p-6 text-center text-gray-500">
            No KPI data available.
          </div>
        )}
      </div>
    </div>
  );
}
