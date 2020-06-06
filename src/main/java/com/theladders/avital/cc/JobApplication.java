package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobApplication {
    private final String applicationTime;
    private final String employerName;
    private final Job job;

    public String getJobName() {
        return this.job.getJobName();
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
        return Objects.equal(job.getJobName(), that.job.getJobName()) &&
                Objects.equal(applicationTime, that.applicationTime) &&
                Objects.equal(employerName, that.employerName) &&
                job.getJobType() == that.job.getJobType();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(job.getJobName(), applicationTime, employerName, job.getJobType());
    }

    public JobApplication(String employerName, LocalDate applicationTime, Job job) {
        this.applicationTime = applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.employerName = employerName;
        this.job = job;
    }

    public JobType getJobType() {
        return this.job.getJobType();
    }
}
