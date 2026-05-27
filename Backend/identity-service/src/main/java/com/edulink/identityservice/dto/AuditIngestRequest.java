package com.edulink.identityservice.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload accepted by /internal/audit/record so other services can append immutable
 * audit entries through the central identity-service ledger (spec §8).
 */
public class AuditIngestRequest {

    @NotBlank
    private String action;

    private String userEmail;
    private String userRole;
    private String resource;
    private String details;
    /** SUCCESS or FAILURE. Defaults to SUCCESS server-side. */
    private String status;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
