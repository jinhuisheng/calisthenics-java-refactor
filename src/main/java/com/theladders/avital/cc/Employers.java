package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class Employers {
    private final HashMap<String, List<Job>> jobs = new HashMap<>();

    public void publishJob(String employerName, String jobName, JobType jobType) throws NotSupportedJobTypeException {
        if (jobType != JobType.JReq && jobType != JobType.ATS) {
            throw new NotSupportedJobTypeException();
        }
        List<Job> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());
        alreadyPublished.add(new Job(jobName, jobType));
        jobs.put(employerName, alreadyPublished);
    }

    public List<Job> getEmployerPublishedJobs(String employerName) {
        return jobs.get(employerName);
    }

}
