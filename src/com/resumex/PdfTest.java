package com.resumex;

import com.resumex.models.Resume;
import com.resumex.services.PdfResumeParser;

public class PdfTest {

    public static void main(String[] args) {

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "              RESUMEX PDF TEST"
        );

        System.out.println(
                "=============================================="
        );

        /*
         * CHANGE THIS PATH
         * to the PDF you want to test.
         */
        String pdfPath =
                "/Users/pasulashlokareddy/eclipse-workspace/ResumeX/resumes/Karan_Mehta.pdf";

        try {

            PdfResumeParser parser =
                    new PdfResumeParser();

            Resume resume =
                    parser.parsePDF(
                            pdfPath,
                            1
                    );

            System.out.println();

            System.out.println(
                    "PDF processed successfully!"
            );

            System.out.println();

            System.out.println(
                    "Candidate Information"
            );

            System.out.println(
                    "----------------------------------------------"
            );

            System.out.println(
                    "Name  : " +
                    resume.getCandidateName()
            );

            System.out.println(
                    "Email : " +
                    resume.getEmail()
            );

            System.out.println(
                    "Phone : " +
                    resume.getPhone()
            );

            System.out.println(
                    "Skills: " +
                    resume.getSkills()
            );

            System.out.println(
                    "----------------------------------------------"
            );

        } catch (Exception e) {

            System.out.println();

            System.out.println(
                    "Error while processing PDF:"
            );

            e.printStackTrace();
        }

        System.out.println();

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "                 TEST COMPLETE"
        );

        System.out.println(
                "=============================================="
        );
    }
}