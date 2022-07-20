package com.theladders.avital.cc.job;

import com.google.common.base.Objects;
import com.theladders.avital.cc.employer.Employer;

public class PublishedJob {
    private final Job job;

    private final Employer employer;

    @Override
    public String toString() {
        return "PublishedJob{" +
                "job=" + job +
                ", employer=" + employer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublishedJob that = (PublishedJob) o;
        return Objects.equal(job, that.job) && Objects.equal(employer, that.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(job, employer);
    }

    public Job getJob() {
        return job;
    }

    public JobType getJobType() {
        return job.getJobType();
    }

    public String getJobName() {
        return job.getJobName();
    }

    public Employer getEmployer() {
        return employer;
    }

    public String getEmployerName() {
        return employer.getName();
    }

    public PublishedJob(Job job, Employer employer) {
        this.job = job;
        this.employer = employer;
    }

    public String getJobTypeName() {
        return job.getJobType().name();
    }
}
