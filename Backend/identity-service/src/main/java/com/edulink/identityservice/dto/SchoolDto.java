package com.edulink.identityservice.dto;

import com.edulink.identityservice.entity.School;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SchoolDto {

    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String principalName;
    private LocalDate establishedDate;
    private int studentNumber;
    private int teacherNumber;
    private LocalDateTime createdAt;

    public SchoolDto() {}

    public static SchoolDto fromEntity(School s) {
        if (s == null) return null;
        SchoolDto d = new SchoolDto();
        d.id = s.getId();
        d.name = s.getName();
        d.address = s.getAddress();
        d.phone = s.getPhone();
        d.email = s.getEmail();
        d.principalName = s.getPrincipalName();
        d.establishedDate = s.getEstablishedDate();
        d.studentNumber = s.getStudentNumber();
        d.teacherNumber = s.getTeacherNumber();
        d.createdAt = s.getCreatedAt();
        return d;
    }

    public static List<SchoolDto> fromEntities(List<School> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(SchoolDto::fromEntity).collect(Collectors.toList());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPrincipalName() { return principalName; }
    public void setPrincipalName(String principalName) { this.principalName = principalName; }
    public LocalDate getEstablishedDate() { return establishedDate; }
    public void setEstablishedDate(LocalDate establishedDate) { this.establishedDate = establishedDate; }
    public int getStudentNumber() { return studentNumber; }
    public void setStudentNumber(int studentNumber) { this.studentNumber = studentNumber; }
    public int getTeacherNumber() { return teacherNumber; }
    public void setTeacherNumber(int teacherNumber) { this.teacherNumber = teacherNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
