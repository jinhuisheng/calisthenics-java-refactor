package com.theladders.avital.cc.job;

import com.google.common.base.Objects;

public class Job {
    private final String jobName;
    private final JobType jobType;

    public Job(String jobName, JobType jobType) {

        this.jobName = jobName;
        this.jobType = jobType;
    }

    public JobType getJobType() {
        return jobType;
    }

    public String getJobName() {
        return jobName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equal(jobName, job.jobName) && isMatched(job.jobType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobName, jobType);
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobName='" + jobName + '\'' +
                ", jobType=" + jobType +
                '}';
    }

    public boolean isMatched(JobType jobType) {
        return this.jobType == jobType;
    }
}
