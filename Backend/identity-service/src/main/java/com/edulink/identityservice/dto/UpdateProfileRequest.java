package com.edulink.identityservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    /** Optional: ISO date string (yyyy-MM-dd). */
    private String dob;

    @Size(max = 20, message = "Phone must be 20 characters or fewer")
    private String phone;

    @Size(max = 500, message = "Address must be 500 characters or fewer")
    private String address;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY)?$",
            message = "Gender must be MALE, FEMALE, OTHER, or PREFER_NOT_TO_SAY")
    private String gender;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
