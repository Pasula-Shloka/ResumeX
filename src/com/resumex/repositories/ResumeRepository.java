package com.resumex.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.resumex.models.Resume;

/**
 * Thread-safe repository for storing and retrieving parsed resumes in memory.
 */
public class ResumeRepository {

    private final List<Resume> resumes;
    private final AtomicInteger idCounter;

    public ResumeRepository() {
        resumes = new CopyOnWriteArrayList<>();
        idCounter = new AtomicInteger(1);
    }

    public synchronized void addResume(Resume resume) {
        if (resume != null) {
            // Replace if same ID exists, else add
            resumes.removeIf(r -> r.getId() == resume.getId());
            resumes.add(resume);
            if (resume.getId() >= idCounter.get()) {
                idCounter.set(resume.getId() + 1);
            }
        }
    }

    public int getNextId() {
        return idCounter.getAndIncrement();
    }

    public List<Resume> getAllResumes() {
        return new ArrayList<>(resumes);
    }

    public Resume findById(int id) {
        for (Resume resume : resumes) {
            if (resume.getId() == id) {
                return resume;
            }
        }
        return null;
    }

    public synchronized void removeResume(int id) {
        resumes.removeIf(resume -> resume.getId() == id);
    }

    public int size() {
        return resumes.size();
    }

    public synchronized void clear() {
        resumes.clear();
    }
}