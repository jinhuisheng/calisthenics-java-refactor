package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedJobs {
    private final HashMap<JobSeeker, List<Job>> savedJobs = new HashMap<>();

    void save(JobSeeker jobSeeker, Job job) {
        List<Job> alreadyPublished_temp = savedJobs.getOrDefault(jobSeeker, new ArrayList<>());
        alreadyPublished_temp.add(job);
        savedJobs.put(jobSeeker, alreadyPublished_temp);
    }

    List<Job> getJobs(String jobSeeker) {
        return savedJobs.get(new JobSeeker(jobSeeker));
    }
}
