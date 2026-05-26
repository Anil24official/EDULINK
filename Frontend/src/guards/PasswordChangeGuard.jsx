import React, { useContext } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

/**
 * If the server flagged this account as mustChangePassword=true, redirect every
 * non-change-password route to /change-password until the user updates it.
 */
export default function PasswordChangeGuard() {
  const { mustChangePassword } = useContext(AuthContext);
  const location = useLocation();

  if (mustChangePassword && location.pathname !== "/change-password") {
    return <Navigate to="/change-password" replace />;
  }
  return <Outlet />;
}
