package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JobApplication {
    private final String jobName;
    private final String jobType;
    private final String applicationTime;
    private final String employerName;

    @Override
    public String toString() {
        return "JobApplication{" +
                "jobName='" + jobName + '\'' +
                ", jobType='" + jobType + '\'' +
                ", applicationTime='" + applicationTime + '\'' +
                ", employerName='" + employerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equal(jobName, that.jobName) && Objects.equal(jobType, that.jobType) && Objects.equal(applicationTime, that.applicationTime) && Objects.equal(employerName, that.employerName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobName, jobType, applicationTime, employerName);
    }

    public JobApplication(String jobName, String jobType, LocalDate applicationTime, String employerName) {
        this.jobName = jobName;
        this.jobType = jobType;
        this.applicationTime = applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.employerName = employerName;
    }

    public JobApplication(String jobName, String jobType, String applicationTime, String employerName) {

        this.jobName = jobName;
        this.jobType = jobType;
        this.applicationTime = applicationTime;
        this.employerName = employerName;
    }
}
