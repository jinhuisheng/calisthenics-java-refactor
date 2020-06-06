package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobApplication {
    private String jobName;
    private final String applicationTime;
    private final String employerName;
    private JobType jobType;

    public String getJobName() {
        return jobName;
    }

    public String getApplicationTime() {
        return applicationTime;
    }

    public String getEmployerName() {
        return employerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equal(jobName, that.jobName) &&
                Objects.equal(applicationTime, that.applicationTime) &&
                Objects.equal(employerName, that.employerName) &&
                jobType == that.jobType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobName, applicationTime, employerName, jobType);
    }

    public JobApplication(String jobName, JobType jobType, LocalDate applicationTime, String employerName) {
        this(jobName, applicationTime, employerName, jobType);
    }


    public JobApplication(String jobName, LocalDate applicationTime, String employerName, JobType jobType) {
        this.jobName = jobName;
        this.applicationTime = applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.employerName = employerName;
        this.jobType = jobType;
    }

    public JobType getJobType() {
        return jobType;
    }
}
