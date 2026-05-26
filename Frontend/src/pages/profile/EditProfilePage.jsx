import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import { storeUser } from "../../services/tokenService";
import identityApi from "../../api/identityApi";
import notify from "../../utils/notify";
import "./EditProfilePage.css";

const GENDER_OPTIONS = [
  { value: "", label: "—" },
  { value: "MALE", label: "Male" },
  { value: "FEMALE", label: "Female" },
  { value: "OTHER", label: "Other" },
  { value: "PREFER_NOT_TO_SAY", label: "Prefer not to say" },
];

const initialForm = {
  fullName: "",
  dob: "",
  phone: "",
  address: "",
  gender: "",
};

function formatDateTime(value) {
  if (!value) return "—";
  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
}

export default function EditProfilePage() {
  const { currentUser: user, setCurrentUser } = useContext(AuthContext);
  const navigate = useNavigate();

  const [form, setForm] = useState(initialForm);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await identityApi.getProfile();
        const data = res.data?.data || res.data || {};
        setProfile(data);
        setForm({
          fullName: data.fullName || "",
          dob: data.dob || "",
          phone: data.phone || "",
          address: data.address || "",
          gender: data.gender || "",
        });
      } catch {
        // Fall back to context if /auth/profile fails (offline, etc.)
        setProfile(user || {});
        setForm({
          fullName: user?.fullName || "",
          dob: "",
          phone: "",
          address: "",
          gender: "",
        });
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [user]);

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.fullName.trim()) {
      notify.error("Full name is required.");
      return;
    }
    setSaving(true);
    try {
      const payload = {
        fullName: form.fullName.trim(),
        dob: form.dob || null,
        phone: form.phone || null,
        address: form.address || null,
        gender: form.gender || null,
      };
      const res = await identityApi.updateProfile(payload);
      const updated = res.data?.data || res.data || {};
      setProfile(updated);
      // Keep all existing user fields, update only what came back.
      const newUser = {
        ...user,
        fullName: updated.fullName ?? user.fullName,
        schoolId: updated.schoolId ?? user.schoolId,
        rollNumber: updated.rollNumber ?? user.rollNumber,
      };
      setCurrentUser(newUser);
      storeUser(newUser);
      notify.success("Profile updated successfully.");
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Failed to update profile.";
      notify.error(msg);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="edit-profile-loading">Loading profile…</div>;
  }

  const role = profile?.role || user?.role || "";
  const email = profile?.email || user?.email || "";
  const schoolId = profile?.schoolId || user?.schoolId || "";
  const classId = profile?.classId != null ? String(profile.classId) : "";
  const rollNumber = profile?.rollNumber || user?.rollNumber || "";
  const createdAt = profile?.createdAt;
  const status = profile?.active === false ? "Inactive" : "Active";

  return (
    <div className="edit-profile-container">
      <div className="edit-profile-card">
        <h2 className="edit-profile-title">
          <span className="material-icons-round">person</span>
          My Profile
        </h2>

        <form onSubmit={handleSubmit} className="edit-profile-form">
          {/* ── Account (read-only) ── */}
          <div>
            <div className="edit-profile-section-title">Account</div>
            <div className="edit-profile-grid">
              <div className="edit-profile-field">
                <label htmlFor="email">Email</label>
                <input id="email" type="text" value={email} disabled />
                <small>Email cannot be changed.</small>
              </div>
              <div className="edit-profile-field">
                <label htmlFor="role">Role</label>
                <input id="role" type="text" value={role} disabled />
              </div>
              <div className="edit-profile-field">
                <label htmlFor="status">Status</label>
                <input id="status" type="text" value={status} disabled />
              </div>
              <div className="edit-profile-field">
                <label htmlFor="createdAt">Member Since</label>
                <input id="createdAt" type="text" value={formatDateTime(createdAt)} disabled />
              </div>
            </div>
          </div>

          {/* ── Affiliation (read-only) ── */}
          {(schoolId || classId || rollNumber) && (
            <div className="edit-profile-section">
              <div className="edit-profile-section-title">Affiliation</div>
              <div className="edit-profile-grid">
                <div className="edit-profile-field">
                  <label htmlFor="schoolId">School ID</label>
                  <input id="schoolId" type="text" value={schoolId || "—"} disabled />
                </div>
                <div className="edit-profile-field">
                  <label htmlFor="classId">Class ID</label>
                  <input id="classId" type="text" value={classId || "—"} disabled />
                </div>
                {rollNumber && (
                  <div className="edit-profile-field">
                    <label htmlFor="rollNumber">Roll Number</label>
                    <input id="rollNumber" type="text" value={rollNumber} disabled />
                  </div>
                )}
              </div>
            </div>
          )}

          {/* ── Personal Details (editable) ── */}
          <div className="edit-profile-section">
            <div className="edit-profile-section-title">Personal Details</div>
            <div className="edit-profile-grid">
              <div className="edit-profile-field edit-profile-grid--full">
                <label htmlFor="fullName">Full Name *</label>
                <input
                  id="fullName"
                  name="fullName"
                  type="text"
                  value={form.fullName}
                  onChange={onChange}
                  placeholder="Enter your full name"
                  required
                />
              </div>
              <div className="edit-profile-field">
                <label htmlFor="dob">Date of Birth</label>
                <input
                  id="dob"
                  name="dob"
                  type="date"
                  value={form.dob}
                  onChange={onChange}
                />
              </div>
              <div className="edit-profile-field">
                <label htmlFor="gender">Gender</label>
                <select id="gender" name="gender" value={form.gender} onChange={onChange}>
                  {GENDER_OPTIONS.map((g) => (
                    <option key={g.value} value={g.value}>{g.label}</option>
                  ))}
                </select>
              </div>
              <div className="edit-profile-field">
                <label htmlFor="phone">Phone</label>
                <input
                  id="phone"
                  name="phone"
                  type="tel"
                  value={form.phone}
                  onChange={onChange}
                  placeholder="+1 555 0123"
                  maxLength={20}
                />
              </div>
              <div className="edit-profile-field edit-profile-grid--full">
                <label htmlFor="address">Address</label>
                <textarea
                  id="address"
                  name="address"
                  rows={3}
                  value={form.address}
                  onChange={onChange}
                  placeholder="Street, City, State, ZIP"
                  maxLength={500}
                />
              </div>
            </div>
          </div>

          <div className="edit-profile-actions">
            <button type="button" className="btn-cancel" onClick={() => navigate(-1)}>
              Cancel
            </button>
            <button type="submit" className="btn-save" disabled={saving}>
              {saving ? "Saving…" : "Save Changes"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
