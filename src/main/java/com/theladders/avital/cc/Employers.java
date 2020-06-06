package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class Employers {
    private final List<Employer> employers = new ArrayList<>();

    public void publishJob(String employerName, String jobName, JobType jobType) throws NotSupportedJobTypeException {
        if (jobType != JobType.JReq && jobType != JobType.ATS) {
            throw new NotSupportedJobTypeException();
        }
        publish(employerName, jobName, jobType);
    }

    private void publish(String employerName, String jobName, JobType jobType) {
        Employer employer = getEmployer(employerName);
        employer.publishJob(new Job(jobName, jobType));
        employers.add(employer);
    }

    private Employer getEmployer(String employerName) {
        return employers.stream()
                .filter(employer -> employer.getName().equals(employerName))
                .findFirst()
                .orElse(new Employer(employerName));
    }

    public List<Job> getEmployerPublishedJobs(String employerName) {
        return employers.stream()
                .filter(employer -> employer.getName().equals(employerName))
                .map(Employer::getPublishedJobs)
                .findFirst()
                .orElseGet(ArrayList::new);
    }

}
