package com.theladders.avital.cc.jobApplication;

import com.google.common.base.Objects;
import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.jobseeker.JobSeeker;
import com.theladders.avital.cc.job.PublishedJob;
import com.theladders.avital.cc.job.JobType;

import java.time.LocalDate;

public class AppliedJobApplication {

    private final PublishedJob publishedJob;

    private final ApplicationInfo applicationInfo;

    public AppliedJobApplication(LocalDate applicationTime, JobSeeker jobSeeker, PublishedJob publishedJob) {
        this.publishedJob = publishedJob;
        this.applicationInfo = new ApplicationInfo(jobSeeker, applicationTime);
    }

    @Override
    public String toString() {
        return "AppliedJobApplication{" +
                "publishedJob=" + publishedJob +
                ", applicationInfo=" + applicationInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppliedJobApplication that = (AppliedJobApplication) o;
        return Objects.equal(publishedJob, that.publishedJob) && Objects.equal(applicationInfo, that.applicationInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publishedJob, applicationInfo);
    }

    boolean isEqualOrAfter(LocalDate from) {
        return applicationInfo.isEqualOrAfter(from);
    }

    boolean isEqualOrBefore(LocalDate to) {
        return applicationInfo.isEqualOrBefore(to);
    }

    public String getEmployerName() {
        return publishedJob.getEmployerName();
    }

    public String getJobName() {
        return publishedJob.getJobName();
    }

    public String getJobSeekerName() {
        return applicationInfo.getJobSeekerName();
    }

    public LocalDate getApplicationTime() {
        return applicationInfo.getApplicationTime();
    }

    public JobType getJobType() {
        return publishedJob.getJobType();
    }

    boolean isMatched(JobSeeker jobSeeker) {
        return applicationInfo.getJobSeeker().equals(jobSeeker);
    }

    boolean isMatched(Employer employer) {
        return publishedJob.getEmployer().equals(employer);
    }

    boolean isMatched(String jobName) {
        return publishedJob.getJobName().equals(jobName);
    }

    public String getJobTypeName() {
        return publishedJob.getJobTypeName();
    }

}
