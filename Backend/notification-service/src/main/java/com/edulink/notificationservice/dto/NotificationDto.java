package com.edulink.notificationservice.dto;

import com.edulink.notificationservice.entity.Notification;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private Long id;

    @NotBlank(message = "Recipient ID is required")
    private String recipientId;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient email must be valid")
    private String recipientEmail;

    @NotBlank(message = "Recipient role is required")
    private String recipientRole;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Type is required")
    private String type;

    private boolean readStatus;
    private boolean delivered;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;

    public static NotificationDto fromEntity(Notification e) {
        if (e == null) return null;
        NotificationDto d = new NotificationDto();
        d.setId(e.getId());
        d.setRecipientId(e.getRecipientId());
        d.setRecipientEmail(e.getRecipientEmail());
        d.setRecipientRole(e.getRecipientRole());
        d.setTitle(e.getTitle());
        d.setMessage(e.getMessage());
        d.setType(e.getType());
        d.setReadStatus(e.isReadStatus());
        d.setDelivered(e.isDelivered());
        d.setScheduledAt(e.getScheduledAt());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    public static List<NotificationDto> fromEntities(List<Notification> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(NotificationDto::fromEntity).collect(Collectors.toList());
    }

    public Notification toEntity() {
        Notification n = new Notification();
        n.setId(this.id);
        n.setRecipientId(this.recipientId);
        n.setRecipientEmail(this.recipientEmail);
        n.setRecipientRole(this.recipientRole);
        n.setTitle(this.title);
        n.setMessage(this.message);
        n.setType(this.type);
        n.setReadStatus(this.readStatus);
        n.setDelivered(this.delivered);
        n.setScheduledAt(this.scheduledAt);
        n.setCreatedAt(this.createdAt);
        return n;
    }

    // ── Explicit getters/setters (fallback if Lombok annotation processing is disabled) ──
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientRole() { return recipientRole; }
    public void setRecipientRole(String recipientRole) { this.recipientRole = recipientRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isReadStatus() { return readStatus; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }

    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
