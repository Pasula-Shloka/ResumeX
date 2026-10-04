package com.resumex.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillExtractor {

    private final List<String> skillDatabase;

    public SkillExtractor() {
        skillDatabase = Arrays.asList(
                "Java",
                "Python",
                "C",
                "C++",
                "C#",
                "JavaScript",
                "TypeScript",
                "HTML",
                "CSS",
                "Spring",
                "Spring Boot",
                "Springboot",
                "Django",
                "Flask",
                "React",
                "Node.js",
                "Node",
                "Express",
                "MySQL",
                "PostgreSQL",
                "MongoDB",
                "Oracle",
                "Redis",
                "REST API",
                "REST",
                "GraphQL",
                "Git",
                "GitHub",
                "Docker",
                "Kubernetes",
                "AWS",
                "Azure",
                "GCP",
                "Data Structures",
                "Algorithms",
                "Machine Learning",
                "Deep Learning",
                "Operating Systems",
                "DBMS",
                "Computer Networks",
                "SQL",
                "NoSQL",
                "Linux"
        );
    }

    public List<String> extractSkills(String resumeText) {
        List<String> detectedSkills = new ArrayList<>();

        if (resumeText == null || resumeText.trim().isEmpty()) {
            return detectedSkills;
        }

        for (String skill : skillDatabase) {
            if (containsSkill(resumeText, skill)) {
                // Canonicalize "Springboot" to "Spring Boot" if appropriate
                String canonical = "Springboot".equalsIgnoreCase(skill) ? "Spring Boot" : skill;
                if (!detectedSkills.contains(canonical)) {
                    detectedSkills.add(canonical);
                }
            }
        }

        return detectedSkills;
    }

    public boolean containsSkill(String text, String skill) {
        if (text == null || skill == null || text.isEmpty() || skill.isEmpty()) {
            return false;
        }

        // For single-letter or special symbols like C, C++, C#
        if (skill.equalsIgnoreCase("C")) {
            Pattern p = Pattern.compile("(?i)(^|[^a-zA-Z0-9+#])C([^a-zA-Z0-9+#]|$)");
            return p.matcher(text).find();
        } else if (skill.equalsIgnoreCase("C++")) {
            Pattern p = Pattern.compile("(?i)(^|[^a-zA-Z0-9])C\\+\\+([^a-zA-Z0-9]|$)");
            return p.matcher(text).find();
        } else if (skill.equalsIgnoreCase("C#")) {
            Pattern p = Pattern.compile("(?i)(^|[^a-zA-Z0-9])C#([^a-zA-Z0-9]|$)");
            return p.matcher(text).find();
        }

        // For general words, ensure word boundary matching so "SQL" does not match "MySQL" incorrectly
        String patternString = "(?i)\\b" + Pattern.quote(skill) + "\\b";
        try {
            Pattern pattern = Pattern.compile(patternString);
            return pattern.matcher(text).find();
        } catch (Exception e) {
            return text.toLowerCase().contains(skill.toLowerCase());
        }
    }

    public List<String> getSkillDatabase() {
        return new ArrayList<>(skillDatabase);
    }

    public void printSkills(List<String> skills) {
        System.out.println("\nDetected Skills\n---------------");
        if (skills == null || skills.isEmpty()) {
            System.out.println("No skills detected.");
            return;
        }
        for (String skill : skills) {
            System.out.println("✓ " + skill);
        }
    }
}