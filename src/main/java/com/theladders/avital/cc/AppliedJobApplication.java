package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;

public class AppliedJobApplication {

    private final PublishedJob publishedJob;

    private final LocalDate applicationTime;

    private final JobSeeker jobSeeker;

    public PublishedJob getPublishedJob() {
        return publishedJob;
    }

    public LocalDate getApplicationTime() {
        return applicationTime;
    }

    public AppliedJobApplication(LocalDate applicationTime, JobSeeker jobSeeker, PublishedJob publishedJob) {
        this.applicationTime = applicationTime;
        this.publishedJob = publishedJob;
        this.jobSeeker = jobSeeker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppliedJobApplication that = (AppliedJobApplication) o;
        return Objects.equal(publishedJob, that.publishedJob) && Objects.equal(applicationTime, that.applicationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publishedJob, applicationTime);
    }

    @Override
    public String toString() {
        return "AppliedJobApplication{" +
                "publishedJob=" + publishedJob +
                ", applicationTime=" + applicationTime +
                '}';
    }

    boolean isEqualOrAfter(LocalDate from) {
        return !from.isAfter(getApplicationTime());
    }

    boolean isEqualOrBefore(LocalDate to) {
        return !to.isBefore(getApplicationTime());
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }
}
