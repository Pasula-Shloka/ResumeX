package com.resumex;

import java.util.List;

import com.resumex.models.Resume;
import com.resumex.services.ResumeProcessingService;

public class ResumeProcessingTest {

    public static void main(String[] args) {

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "                 RESUMEX"
        );

        System.out.println(
                "        RESUME PROCESSING TEST"
        );

        System.out.println(
                "=============================================="
        );

        /*
         * CHANGE THIS PATH LATER
         * to your resume folder.
         */
        String folderPath =
                "/Users/pasulashlokareddy/eclipse-workspace/ResumeX/resumes";

        ResumeProcessingService service =
                new ResumeProcessingService();

        List<Resume> resumes =
                service.processFolder(
                        folderPath
                );

        service.printCandidateInformation(
                resumes
        );

        System.out.println();

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "        RESUME PROCESSING COMPLETE"
        );

        System.out.println(
                "=============================================="
        );
    }
}