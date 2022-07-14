package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JobApplication {
    private final String jobName;
    private final String applicationTime;
    private final String employerName;
    private final JobType jobType;

    public String getJobName() {
        return jobName;
    }

    public String getApplicationTime() {
        return applicationTime;
    }

    public String getEmployerName() {
        return employerName;
    }

    public JobType getJobType() {
        return jobType;
    }

    public JobApplication(String jobName, LocalDate applicationTime, String employerName, JobType jobType) {
        this.jobName = jobName;
        this.applicationTime = applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.employerName = employerName;
        this.jobType = jobType;
    }

    @Override
    public String toString() {
        return "JobApplication{" +
                "jobName='" + jobName + '\'' +
                ", applicationTime='" + applicationTime + '\'' +
                ", employerName='" + employerName + '\'' +
                ", jobType_temp=" + jobType +
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
}
