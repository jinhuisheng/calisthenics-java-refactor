package com.theladders.avital.cc.job;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {
    private final HashMap<JobSeeker, List<Job>> savedJobs = new HashMap<>();

    private final HashMap<Employer, List<Job>> publishedJobs = new HashMap<>();

    public void publish(Job job, Employer employer) {
        List<Job> employerPublishedJobs = publishedJobs.getOrDefault(employer, new ArrayList<>());
        employerPublishedJobs.add(job);
        publishedJobs.put(employer, employerPublishedJobs);
    }

    public void save(JobSeeker jobSeeker, Job job) {
        List<Job> alreadyPublished_temp = savedJobs.getOrDefault(jobSeeker, new ArrayList<>());
        alreadyPublished_temp.add(job);
        savedJobs.put(jobSeeker, alreadyPublished_temp);
    }

    public List<Job> getJobs(Employer employer) {
        return this.publishedJobs.get(employer);
    }

    public List<Job> getJobs(JobSeeker jobSeeker) {
        return savedJobs.get(jobSeeker);
    }
}
