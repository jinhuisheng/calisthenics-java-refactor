package com.theladders.avital.cc.jobApplication;

import com.theladders.avital.cc.InvalidResumeException;
import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.JobType;
import com.theladders.avital.cc.job.PublishedJob;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AppliedJobApplications {
    private final List<AppliedJobApplication> appliedApplications = new ArrayList<>();

    void apply(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime, String resumeApplicantName) throws InvalidResumeException {
        if (job.getJobType() == JobType.JReq && !resumeApplicantName.equals(jobSeeker.getName())) {
            throw new InvalidResumeException();
        }
        addApplication(jobSeeker, job, employer, applicationTime);
    }

    private void addApplication(JobSeeker jobSeeker, Job job, Employer employer, LocalDate applicationTime) {
        AppliedJobApplication appliedJobApplication = new AppliedJobApplication(applicationTime, jobSeeker, new PublishedJob(job, employer));
        appliedApplications.add(appliedJobApplication);
    }

    List<AppliedJobApplication> getJobApplications(JobSeeker jobSeeker) {
        return appliedApplications.stream()
                .filter(jobApplication -> jobApplication.isMatched(jobSeeker))
                .collect(Collectors.toList());
    }

    int getSuccessfulApplications(Employer employer, String jobName) {
        return (int) appliedApplications.stream()
                .filter(jobApplication -> jobApplication.isMatched(employer)
                        && jobApplication.isMatched(jobName))
                .count();
    }

    List<String> findApplicants(Predicate<AppliedJobApplication> predicate) {
        return appliedApplications.stream()
                .filter(predicate)
                .map(AppliedJobApplication::getJobSeekerName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<AppliedJobApplication> getJobApplications(LocalDate applicationTime) {
        return appliedApplications.stream()
                .filter(jobApplication -> jobApplication.getApplicationTime().equals(applicationTime))
                .sorted(Comparator.comparing(AppliedJobApplication::getJobSeekerName))
                .collect(Collectors.toList());
    }

    List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        Predicate<AppliedJobApplication> condition = getCondition(jobName, from, to);
        return findApplicants(condition);
    }

    Predicate<AppliedJobApplication> getCondition(String jobName, LocalDate from, LocalDate to) {
        Predicate<AppliedJobApplication> predicate = job -> true;
        if (from != null) {
            predicate = predicate.and(job -> job.isEqualOrAfter(from));
        }
        if (to != null) {
            predicate = predicate.and(job -> job.isEqualOrBefore(to));
        }
        if (jobName != null) {
            predicate = predicate.and(job -> job.isMatched(jobName));
        }
        return predicate;
    }
}
