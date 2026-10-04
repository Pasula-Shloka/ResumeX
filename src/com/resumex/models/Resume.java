package com.resumex.models;

import java.util.List;

public class Resume {

    private int id;
    private String candidateName;
    private String email;
    private String phone;
    private String rawText;
    private List<String> skills;
    private String fileName;

    public Resume(int id,
                  String candidateName,
                  String email,
                  String phone,
                  String rawText,
                  List<String> skills) {

        this(id, candidateName, email, phone, rawText, skills, "resume_" + id + ".pdf");
    }

    public Resume(int id,
                  String candidateName,
                  String email,
                  String phone,
                  String rawText,
                  List<String> skills,
                  String fileName) {

        this.id = id;
        this.candidateName = candidateName;
        this.email = email;
        this.phone = phone;
        this.rawText = rawText;
        this.skills = skills;
        this.fileName = fileName != null ? fileName : ("resume_" + id + ".pdf");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}