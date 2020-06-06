package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobSeekers {
    private final List<JobSeeker> jobSeekers = new ArrayList<>();

    public void saveJobSeeker(String jobSeekerName, String jobName, JobType jobType) {
        JobSeeker jobSeeker = getJobSeeker(jobSeekerName);
        jobSeeker.saveJob(new Job(jobName, jobType));
        jobSeekers.add(jobSeeker);
    }

    private JobSeeker getJobSeeker(String jobSeekerName) {
        return jobSeekers.stream()
                .filter(jobSeeker -> jobSeeker.getName().equals(jobSeekerName))
                .findFirst()
                .orElse(new JobSeeker(jobSeekerName));
    }

    public List<Job> getJobSeekerJobs(String jobSeekerName) {
        return jobSeekers.stream()
                .filter(seeker -> seeker.getName().equals(jobSeekerName))
                .map(JobSeeker::getSavedJobs)
                .findFirst()
                .orElseGet(ArrayList::new);
    }

}
