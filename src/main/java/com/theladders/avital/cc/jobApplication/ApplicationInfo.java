package com.theladders.avital.cc.jobApplication;

import com.google.common.base.Objects;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;

public class ApplicationInfo {
    private final JobSeeker jobSeeker;
    private final LocalDate applicationTime;

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public String getJobSeekerName() {
        return jobSeeker.getName();
    }

    public LocalDate getApplicationTime() {
        return applicationTime;
    }

    public ApplicationInfo(JobSeeker jobSeeker, LocalDate applicationTime) {

        this.jobSeeker = jobSeeker;
        this.applicationTime = applicationTime;
    }

    @Override
    public String toString() {
        return "ApplicationInfo{" +
                "jobSeeker=" + jobSeeker +
                ", applicationTime=" + applicationTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationInfo that = (ApplicationInfo) o;
        return Objects.equal(jobSeeker, that.jobSeeker) && Objects.equal(applicationTime, that.applicationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobSeeker, applicationTime);
    }

    boolean isEqualOrAfter(LocalDate from) {
        return !from.isAfter(getApplicationTime());
    }

    boolean isEqualOrBefore(LocalDate to) {
        return !to.isBefore(getApplicationTime());
    }
}
