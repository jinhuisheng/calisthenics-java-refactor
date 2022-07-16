package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;

public class JobApplication {
    private final String jobName;

    private final LocalDate applicationTime;

    private final String employerName;
    private final JobType jobType;

    public LocalDate getApplicationTime() {
        return applicationTime;
    }

    public String getJobName() {
        return jobName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public JobType getJobType() {
        return jobType;
    }

    public JobApplication(String jobName, LocalDate applicationTime, String employerName, JobType jobType) {
        this.jobName = jobName;
        this.employerName = employerName;
        this.jobType = jobType;
        this.applicationTime = applicationTime;
    }

    @Override
    public String toString() {
        return "JobApplication{" +
                "jobName='" + jobName + '\'' +
                ", applicationTime_temp=" + applicationTime +
                ", employerName='" + employerName + '\'' +
                ", jobType=" + jobType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equal(jobName, that.jobName) && Objects.equal(applicationTime, that.applicationTime) && Objects.equal(employerName, that.employerName) && jobType == that.jobType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobName, applicationTime, employerName, jobType);
    }

    boolean isEqualOrAfter(LocalDate from) {
        return !from.isAfter(getApplicationTime());
    }

    boolean isEqualOrBefore(LocalDate to) {
        return !to.isBefore(getApplicationTime());
    }
}
