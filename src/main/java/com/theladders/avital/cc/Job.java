package com.theladders.avital.cc;

import com.google.common.base.Objects;

public class Job {
    private final String jobName;
    private final String jobType;

    public String getJobName() {
        return jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public Job(String jobName, String jobType) {

        this.jobName = jobName;
        this.jobType = jobType;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobName='" + jobName + '\'' +
                ", jobType='" + jobType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equal(jobName, job.jobName) && Objects.equal(jobType, job.jobType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobName, jobType);
    }
}
