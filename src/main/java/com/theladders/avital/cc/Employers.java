package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Employers {
    private final HashMap<String, List<Job>> publishedJobs = new HashMap<>();

    List<Job> getJobs(String employerName) {
        return this.publishedJobs.get(employerName);
    }

    void publish(String employerName, Job job) throws NotSupportedJobTypeException {
        if (job.getJobType() != JobType.JReq && job.getJobType() != JobType.ATS) {
            throw new NotSupportedJobTypeException();
        }
        List<Job> alreadyPublished = this.publishedJobs.getOrDefault(employerName, new ArrayList<>());
        alreadyPublished.add(job);
        this.publishedJobs.put(employerName, alreadyPublished);
    }
}
