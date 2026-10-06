package com.resumex.tools;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class GenerateExtraResumes {

    private static void createResume(
            String filename,
            String name,
            String email,
            String phone,
            String summary,
            String[] skills,
            String[] experience,
            String[] education
    ) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = 780;

                // Name
                cs.beginText();
                cs.setFont(fontBold, 18);
                cs.newLineAtOffset(50, y);
                cs.showText(name);
                cs.endText();
                y -= 20;

                // Contact
                cs.beginText();
                cs.setFont(fontRegular, 10);
                cs.newLineAtOffset(50, y);
                cs.showText("Email: " + email + " | Phone: " + phone);
                cs.endText();
                y -= 25;

                // Summary Header
                cs.beginText();
                cs.setFont(fontBold, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Professional Summary");
                cs.endText();
                y -= 16;

                // Summary Text
                cs.beginText();
                cs.setFont(fontRegular, 9.5f);
                cs.newLineAtOffset(50, y);
                cs.showText(summary);
                cs.endText();
                y -= 25;

                // Technical Skills Header
                cs.beginText();
                cs.setFont(fontBold, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Technical Skills");
                cs.endText();
                y -= 16;

                // Skills List
                cs.beginText();
                cs.setFont(fontRegular, 9.5f);
                cs.newLineAtOffset(50, y);
                cs.showText(String.join(", ", skills));
                cs.endText();
                y -= 25;

                // Experience Header
                cs.beginText();
                cs.setFont(fontBold, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Work Experience");
                cs.endText();
                y -= 18;

                for (String exp : experience) {
                    cs.beginText();
                    cs.setFont(fontRegular, 9.5f);
                    cs.newLineAtOffset(55, y);
                    cs.showText("- " + exp);
                    cs.endText();
                    y -= 16;
                }
                y -= 10;

                // Education Header
                cs.beginText();
                cs.setFont(fontBold, 12);
                cs.newLineAtOffset(50, y);
                cs.showText("Education");
                cs.endText();
                y -= 18;

                for (String edu : education) {
                    cs.beginText();
                    cs.setFont(fontRegular, 9.5f);
                    cs.newLineAtOffset(55, y);
                    cs.showText("- " + edu);
                    cs.endText();
                    y -= 16;
                }
            }

            File f = new File(filename);
            doc.save(f);
            System.out.println("Generated: " + f.getAbsolutePath());
        }
    }

    public static void main(String[] args) throws Exception {
        String base1 = "/Users/pasulashlokareddy/eclipse-workspace/ResumeX/resumes/";
        String base2 = System.getProperty("user.home") + "/Desktop/ResumeX_Test_Resumes/";

        // 1. Neha Kapoor - Full Stack
        createResume(
                base1 + "Neha_Kapoor_FullStack.pdf",
                "Neha Kapoor",
                "neha.kapoor.dev@example.com",
                "+91 98765 11223",
                "Full Stack Developer with 4+ years of experience building Java microservices and React frontends.",
                new String[]{"Java", "Spring Boot", "React", "JavaScript", "SQL", "MySQL", "REST API", "Git", "Docker", "Data Structures", "Algorithms"},
                new String[]{
                        "Software Engineer at TechCorp: Developed scalable microservices using Spring Boot and MySQL.",
                        "Built responsive frontends with React, consuming enterprise REST APIs.",
                        "Containerized applications using Docker and established CI/CD Git pipelines."
                },
                new String[]{
                        "B.Tech in Computer Science and Engineering, 2022 (GPA: 8.9/10)"
                }
        );

        // 2. Vikram Malhotra - DevOps & Cloud
        createResume(
                base1 + "Vikram_Malhotra_DevOps.pdf",
                "Vikram Malhotra",
                "vikram.malhotra.ops@example.com",
                "+91 98450 99887",
                "Senior DevOps and Cloud Infrastructure Engineer specializing in Kubernetes, Docker, and CI/CD pipelines.",
                new String[]{"Docker", "Kubernetes", "Linux", "AWS", "Git", "Python", "SQL", "CI/CD", "Terraform"},
                new String[]{
                        "DevOps Specialist at CloudScale: Managed multi-cluster Kubernetes deployments on AWS.",
                        "Automated CI/CD deployment pipelines using Git and Linux shell scripts.",
                        "Containerized monolithic systems into lightweight Docker containers."
                },
                new String[]{
                        "B.E. in Information Technology, 2021 (GPA: 8.5/10)"
                }
        );

        // Copy to Desktop folder too
        new File(base1 + "Neha_Kapoor_FullStack.pdf").renameTo(new File(base2 + "Neha_Kapoor_FullStack.pdf"));
        new File(base1 + "Vikram_Malhotra_DevOps.pdf").renameTo(new File(base2 + "Vikram_Malhotra_DevOps.pdf"));
        
        // Also keep in base1
        java.nio.file.Files.copy(
                new File(base2 + "Neha_Kapoor_FullStack.pdf").toPath(),
                new File(base1 + "Neha_Kapoor_FullStack.pdf").toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
        java.nio.file.Files.copy(
                new File(base2 + "Vikram_Malhotra_DevOps.pdf").toPath(),
                new File(base1 + "Vikram_Malhotra_DevOps.pdf").toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
    }
}
