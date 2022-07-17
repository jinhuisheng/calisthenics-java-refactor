package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;

public class AppliedJobApplication {
    private final Job job;

    private final LocalDate applicationTime;

    private final String employerName;

    public Job getJob() {
        return job;
    }

    public LocalDate getApplicationTime() {
        return applicationTime;
    }

    public String getEmployerName() {
        return employerName;
    }

    public AppliedJobApplication(String jobName, LocalDate applicationTime, String employerName, JobType jobType) {
        this.employerName = employerName;
        this.applicationTime = applicationTime;
        this.job = new Job(jobName, jobType);
    }

    @Override
    public String toString() {
        return "AppliedJobApplication{" +
                "job=" + job +
                ", applicationTime=" + applicationTime +
                ", employerName='" + employerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppliedJobApplication that = (AppliedJobApplication) o;
        return Objects.equal(job, that.job) && Objects.equal(applicationTime, that.applicationTime) && Objects.equal(employerName, that.employerName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(job, applicationTime, employerName);
    }

    boolean isEqualOrAfter(LocalDate from) {
        return !from.isAfter(getApplicationTime());
    }

    boolean isEqualOrBefore(LocalDate to) {
        return !to.isBefore(getApplicationTime());
    }
}
