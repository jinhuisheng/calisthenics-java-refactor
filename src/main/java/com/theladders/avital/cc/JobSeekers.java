package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobSeekers {
    private final HashMap<String, List<Job>> jobSeekers = new HashMap<>();

    public void saveJobSeeker(String jobSeeker, String jobName, String jobType) {
        List<Job> saved = jobSeekers.getOrDefault(jobSeeker, new ArrayList<>());
        saved.add(new Job(jobName, JobType.valueOf(jobType)));
        jobSeekers.put(jobSeeker, saved);
    }

    public List<Job> getJobSeekerJobs(String jobSeeker) {
        return jobSeekers.get(jobSeeker);
    }

}
