package com.theladders.avital.cc;

import com.google.common.base.Objects;

import java.time.LocalDate;

public class AppliedJobApplication {

    private final PublishedJob publishedJob;

    private final ApplicationInfo applicationInfo;

    public PublishedJob getPublishedJob() {
        return publishedJob;
    }

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
        return !from.isAfter(this.applicationInfo.getApplicationTime());
    }

    boolean isEqualOrBefore(LocalDate to) {
        return !to.isBefore(this.applicationInfo.getApplicationTime());
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
