package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;

public class AppliedJobApplication {
    private final Job job;

    private final LocalDate applicationTime;

    private final Employer employer;

    public Employer getEmployer() {
        return employer;
    }

    public Job getJob() {
        return job;
    }

    public LocalDate getApplicationTime() {
        return applicationTime;
    }

    public AppliedJobApplication(String jobName, LocalDate applicationTime, String employerName, JobType jobType) {
        this.applicationTime = applicationTime;
        this.job = new Job(jobName, jobType);
        this.employer = new Employer(employerName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppliedJobApplication that = (AppliedJobApplication) o;
        return Objects.equal(job, that.job) && Objects.equal(applicationTime, that.applicationTime) && Objects.equal(employer, that.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(job, applicationTime, employer);
    }

    @Override
    public String toString() {
        return "AppliedJobApplication{" +
                "job=" + job +
                ", applicationTime=" + applicationTime +
                ", employer=" + employer +
                '}';
    }

    boolean isEqualOrAfter(LocalDate from) {
        return !from.isAfter(getApplicationTime());
    }

    boolean isEqualOrBefore(LocalDate to) {
        return !to.isBefore(getApplicationTime());
    }
}
