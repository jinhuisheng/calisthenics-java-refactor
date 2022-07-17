package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobApplications {
    final AppliedJobApplications appliedApplications = new AppliedJobApplications();
    private final List<JobApplication> failedApplications = new ArrayList<>();

    void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (jobApplication.getJobType() == JobType.JReq && resumeApplicantName == null) {
            failedApplications.add(jobApplication);
            throw new RequiresResumeForJReqJobException();
        }
        appliedApplications.apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    List<JobApplication> getJobApplications(String jobSeekerName) {
        return appliedApplications.getJobApplications(jobSeekerName);
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

    int getSuccessfulApplications(String employerName, String jobName) {
        return appliedApplications.getSuccessfulApplications(employerName, jobName);
    }

    int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream()
                .filter(job -> job.getJobName().equals(jobName) && job.getEmployerName().equals(employerName))
                .count();
    }
}
