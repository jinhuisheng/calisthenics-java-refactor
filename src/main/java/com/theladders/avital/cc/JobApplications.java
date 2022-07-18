package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobApplications {
    final AppliedJobApplications appliedApplications = new AppliedJobApplications();
    private final List<JobApplication> failedApplications = new ArrayList<>();

    void apply(String resumeApplicantName, JobSeeker jobSeeker, Job job, Employer employer, LocalDate applicationTime)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getJobType() == JobType.JReq && resumeApplicantName == null) {
            JobApplication jobApplication = new JobApplication(job.getJobName(), applicationTime, employer.getName(), job.getJobType());
            failedApplications.add(jobApplication);
            throw new RequiresResumeForJReqJobException();
        }
        appliedApplications.apply(resumeApplicantName, jobSeeker, job, employer, applicationTime);
    }

    List<AppliedJobApplication> getJobApplications(String jobSeekerName) {
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
