package com.resumex.models;

import java.util.List;

public class JobDescription {

    private int id;
    private String jobTitle;
    private String description;
    private List<String> requiredSkills;

    public JobDescription(int id,
                          String jobTitle,
                          String description,
                          List<String> requiredSkills) {

        this.id = id;
        this.jobTitle = jobTitle;
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

    public int getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }
}