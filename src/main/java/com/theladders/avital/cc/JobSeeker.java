package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/6.
 */
public class JobSeeker {
    private String name;
    private List<Job> savedJobs = new ArrayList<>();
    private List<JobApplication> jobApplications = new ArrayList<>();

    public JobSeeker(String jobSeekerName) {
        this.name = jobSeekerName;
    }

    public String getName() {
        return this.name;
    }

    public List<Job> getSavedJobs() {
        return savedJobs;
    }

    public void saveJob(Job job) {
        savedJobs.add(job);
    }

    public void saveApply(JobApplication jobApplication) {
        jobApplications.add(jobApplication);
    }

    public List<JobApplication> getJobApplications() {
        return this.jobApplications;
    }
}
