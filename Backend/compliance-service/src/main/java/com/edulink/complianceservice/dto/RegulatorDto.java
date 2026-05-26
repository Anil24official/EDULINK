package com.edulink.complianceservice.dto;

import com.edulink.complianceservice.entity.Regulator;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class RegulatorDto {
    private Long id;
    @NotNull(message="RuleId not valid")
    private Long ruleId;
    private String regulatorOfficer;
    private String flag;
    private String message;


    public RegulatorDto(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "RuleId not valid") Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(@NotNull(message = "RuleId not valid") Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRegulatorOfficer() {
        return regulatorOfficer;
    }

    public void setRegulatorOfficer(String regulatorOfficer) {
        this.regulatorOfficer = regulatorOfficer;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static RegulatorDto fromEntity(Regulator r) {
        if (r == null) return null;
        RegulatorDto d = new RegulatorDto();
        d.setId(r.getId());
        d.setRuleId(r.getRuleId());
        d.setRegulatorOfficer(r.getRegulatorOfficer());
        d.setFlag(r.getFlag());
        d.setMessage(r.getMessage());
        return d;
    }

    public static List<RegulatorDto> fromEntities(List<Regulator> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(RegulatorDto::fromEntity).collect(Collectors.toList());
    }

    public Regulator toEntity() {
        Regulator r = new Regulator();
        r.setId(this.id);
        r.setRuleId(this.ruleId);
        r.setRegulatorOfficer(this.regulatorOfficer);
        r.setFlag(this.flag);
        r.setMessage(this.message);
        return r;
    }
}
