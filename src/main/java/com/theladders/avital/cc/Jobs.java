package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {
    private final HashMap<JobSeeker, List<Job>> savedJobs = new HashMap<>();

    private final HashMap<Employer, List<Job>> publishedJobs = new HashMap<>();

    void publish(Job job, Employer employer) throws NotSupportedJobTypeException {
        if (job.getJobType() != JobType.JReq && job.getJobType() != JobType.ATS) {
            throw new NotSupportedJobTypeException();
        }
        publishJob(job, employer);
    }

    private void publishJob(Job job, Employer employer) {
        List<Job> employerPublishedJobs = publishedJobs.getOrDefault(employer, new ArrayList<>());
        employerPublishedJobs.add(job);
        publishedJobs.put(employer, employerPublishedJobs);
    }

    void save(JobSeeker jobSeeker, Job job) {
        List<Job> alreadyPublished_temp = savedJobs.getOrDefault(jobSeeker, new ArrayList<>());
        alreadyPublished_temp.add(job);
        savedJobs.put(jobSeeker, alreadyPublished_temp);
    }

    List<Job> getJobs(Employer employer) {
        return this.publishedJobs.get(employer);
    }

    List<Job> getJobs(JobSeeker jobSeeker) {
        return savedJobs.get(jobSeeker);
    }
}
