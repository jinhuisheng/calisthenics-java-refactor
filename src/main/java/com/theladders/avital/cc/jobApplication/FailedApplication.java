package com.theladders.avital.cc.jobApplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.PublishedJob;
import com.theladders.avital.cc.job.Job;

import java.time.LocalDate;

public class FailedApplication {

    private final LocalDate applicationTime;

    private final PublishedJob publishedJob;

    public FailedApplication(LocalDate applicationTime, Job job, Employer employer) {
        this.applicationTime = applicationTime;
        this.publishedJob = new PublishedJob(job, employer);
    }

    public PublishedJob getPublishedJob() {
        return publishedJob;
    }

}
