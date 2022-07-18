package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobApplications {
    final AppliedJobApplications appliedApplications = new AppliedJobApplications();
    private final List<FailedApplication> failedApplications = new ArrayList<>();

    void apply(String resumeApplicantName, JobSeeker jobSeeker, Job job, Employer employer, LocalDate applicationTime)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getJobType() == JobType.JReq && resumeApplicantName == null) {
            FailedApplication failedApplication = new FailedApplication(applicationTime, job, employer);
            failedApplications.add(failedApplication);
            throw new RequiresResumeForJReqJobException();
        }
        appliedApplications.apply(resumeApplicantName, jobSeeker, job, employer, applicationTime);
    }

    List<AppliedJobApplication> getJobApplications(JobSeeker jobSeeker) {
        return appliedApplications.getJobApplications(jobSeeker);
    }

    List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return appliedApplications.findApplicants(jobName, from, to);
    }

    public String exportCsv(LocalDate applicationTime) {
        return appliedApplications.exportCsv(applicationTime);
    }

    String exportHtml(LocalDate applicationTime) {
        return appliedApplications.exportHtml(applicationTime);
    }

    int getSuccessfulApplications(Employer employer, String jobName) {
        return appliedApplications.getSuccessfulApplications(employer, jobName);
    }

    int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream()
                .filter(job -> job.getPublishedJob().getJob().getJobName().equals(jobName) && job.getPublishedJob().getEmployer().getName().equals(employerName))
                .count();
    }
}
