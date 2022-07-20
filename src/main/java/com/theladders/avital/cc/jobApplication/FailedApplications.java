package com.theladders.avital.cc.jobApplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FailedApplications {
    private final List<FailedApplication> failedApplications = new ArrayList<>();

    public void add(Job job, Employer employer, LocalDate applicationTime) {
        FailedApplication failedApplication = new FailedApplication(applicationTime, job, employer);
        failedApplications.add(failedApplication);
    }

    public int getUnsuccessfulApplications(Employer employer, Job job) {
        return (int) failedApplications.stream()
                .filter(failedApplication -> failedApplication.isMatched(job)
                        && failedApplication.isMatched(employer))
                .count();
    }

}
