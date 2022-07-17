package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {
    private final HashMap<JobSeeker, List<Job>> jobSeekerSavedJobs = new HashMap<>();

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

    void save(JobSeeker jobSeeker, Job job) {
        List<Job> alreadyPublished_temp = jobSeekerSavedJobs.getOrDefault(jobSeeker, new ArrayList<>());
        alreadyPublished_temp.add(job);
        jobSeekerSavedJobs.put(jobSeeker, alreadyPublished_temp);
    }

    List<Job> getJobs(JobSeeker jobSeeker) {
        return jobSeekerSavedJobs.get(jobSeeker);
    }
}
