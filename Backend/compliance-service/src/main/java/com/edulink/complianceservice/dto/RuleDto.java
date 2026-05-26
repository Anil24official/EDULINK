package com.edulink.complianceservice.dto;

import com.edulink.complianceservice.entity.Rule;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RuleDto {
    private long id;



    @NotNull(message = "ruleType not valid")
    private String ruleType;
    private String boardOfficerId;

    private String complianceOfferId;

    @NotNull(message = "ruleConfig not valid")
    private String ruleConfig;

    private String status;
    private  boolean active;
    private boolean review;

    private Date ruleCreate;
    private Date ruleActive;

    public RuleDto(){

    }

    public static RuleDto fromEntity(Rule r) {
        if (r == null) return null;
        RuleDto d = new RuleDto();
        d.id = r.getId() == null ? 0 : r.getId();
        d.ruleType = r.getRuleType();
        d.boardOfficerId = r.getBoardOfficerId();
        d.complianceOfferId = r.getComplianceOfferId();
        d.ruleConfig = r.getRuleConfig();
        d.status = r.getStatus();
        d.active = r.isActive();
        d.review = r.isReview();
        d.ruleCreate = r.getRuleCreate();
        d.ruleActive = r.getRuleActive();
        return d;
    }

    public static List<RuleDto> fromEntities(List<Rule> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(RuleDto::fromEntity).collect(Collectors.toList());
    }

    public Rule toEntity() {
        Rule r = new Rule();
        r.setId(this.id);
        r.setRuleType(this.ruleType);
        r.setBoardOfficerId(this.boardOfficerId);
        r.setComplianceOfferId(this.complianceOfferId);
        r.setRuleConfig(this.ruleConfig);
        if (this.status != null) r.setStatus(this.status);
        r.setActive(this.active);
        r.setReview(this.review);
        if (this.ruleCreate != null) r.setRuleCreate(this.ruleCreate);
        if (this.ruleActive != null) r.setRuleActive(this.ruleActive);
        return r;
    }

    public boolean isReview() { return review; }
    public void setReview(boolean review) { this.review = review; }

    public Date getRuleActive() {
        return ruleActive;
    }

    public void setRuleActive(Date ruleActive) {
        this.ruleActive = ruleActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull(message = "ruleType not valid") String getRuleType() {
        return ruleType;
    }

    public void setRuleType(@NotNull(message = "ruleType not valid") String ruleType) {
        this.ruleType = ruleType;
    }

    public @NotNull(message = "boardOfficerId not valid") String getBoardOfficerId() {
        return boardOfficerId;
    }

    public void setBoardOfficerId(@NotNull(message = "boardOfficerId not valid") String boardOfficerId) {
        this.boardOfficerId = boardOfficerId;
    }

    public String getComplianceOfferId() {
        return complianceOfferId;
    }

    public void setComplianceOfferId(String complianceOfferId) {
        this.complianceOfferId = complianceOfferId;
    }

    public @NotNull(message = "ruleConfig not valid") String getRuleConfig() {
        return ruleConfig;
    }

    public void setRuleConfig(@NotNull(message = "ruleConfig not valid") String ruleConfig) {
        this.ruleConfig = ruleConfig;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getRuleCreate() {
        return ruleCreate;
    }

    public void setRuleCreate(Date ruleCreate) {
        this.ruleCreate = ruleCreate;
    }
}
