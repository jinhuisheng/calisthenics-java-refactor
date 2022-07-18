package com.theladders.avital.cc;

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
