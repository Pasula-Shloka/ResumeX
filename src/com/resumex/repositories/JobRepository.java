package com.resumex.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.resumex.models.JobDescription;

/**
 * In-memory repository for storing and managing Job Descriptions.
 */
public class JobRepository {

    private final List<JobDescription> jobs;

    public JobRepository() {
        jobs = new CopyOnWriteArrayList<>();
        initializeDefaultJobs();
    }

    private void initializeDefaultJobs() {
        jobs.add(new JobDescription(
                101,
                "Java Backend Developer",
                "We are seeking an experienced Java Backend Developer to build scalable enterprise microservices. "
                        + "The ideal candidate will have expertise in Java, Spring Boot, REST APIs, SQL, MySQL, Git, "
                        + "Docker, Data Structures, and Algorithms.",
                Arrays.asList(
                        "Java",
                        "Spring Boot",
                        "REST API",
                        "SQL",
                        "MySQL",
                        "Git",
                        "Docker",
                        "Data Structures",
                        "Algorithms"
                )
        ));

        jobs.add(new JobDescription(
                102,
                "Frontend React Developer",
                "Looking for a Frontend Developer proficient in React, JavaScript, HTML, CSS, and Git. "
                        + "Must be skilled in building responsive user interfaces, integrating REST APIs, and state management.",
                Arrays.asList(
                        "React",
                        "JavaScript",
                        "HTML",
                        "CSS",
                        "Git",
                        "REST API"
                )
        ));

        jobs.add(new JobDescription(
                103,
                "Data Analyst & ML Engineer",
                "Seeking a Data Analyst with practical experience in Python, SQL, Machine Learning, Deep Learning, "
                        + "and database management using PostgreSQL and MySQL.",
                Arrays.asList(
                        "Python",
                        "SQL",
                        "Machine Learning",
                        "Deep Learning",
                        "PostgreSQL",
                        "DBMS"
                )
        ));

        jobs.add(new JobDescription(
                104,
                "Full Stack Java Engineer",
                "Hiring a Full Stack Developer capable of building end-to-end applications using Java, Spring Boot, "
                        + "React, REST APIs, MySQL, and Docker.",
                Arrays.asList(
                        "Java",
                        "Spring Boot",
                        "React",
                        "JavaScript",
                        "MySQL",
                        "REST API",
                        "Git",
                        "Docker"
                )
        ));
    }

    public List<JobDescription> getAllJobs() {
        return new ArrayList<>(jobs);
    }

    public JobDescription findById(int id) {
        for (JobDescription job : jobs) {
            if (job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public synchronized void addJob(JobDescription job) {
        if (job != null) {
            jobs.removeIf(j -> j.getId() == job.getId());
            jobs.add(job);
        }
    }

    public synchronized boolean deleteJob(int id) {
        return jobs.removeIf(j -> j.getId() == id);
    }

    public int size() {
        return jobs.size();
    }
}
