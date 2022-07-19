package com.theladders.avital.cc.jobApplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.InvalidResumeException;
import com.theladders.avital.cc.jobApplication.exporter.JobApplicationCsvExporter;
import com.theladders.avital.cc.jobApplication.exporter.JobApplicationHtmlExporter;
import com.theladders.avital.cc.jobseeker.JobSeeker;
import com.theladders.avital.cc.RequiresResumeForJReqJobException;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.JobType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobApplications {
    final AppliedJobApplications appliedApplications = new AppliedJobApplications();
    private final List<FailedApplication> failedApplications = new ArrayList<>();

    public void apply(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime, String resumeApplicantName)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getJobType() == JobType.JReq && resumeApplicantName == null) {
            addFailedApplications(job, employer, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }
        appliedApplications.apply(employer, job, jobSeeker, applicationTime, resumeApplicantName);
    }

    private void addFailedApplications(Job job, Employer employer, LocalDate applicationTime) {
        FailedApplication failedApplication = new FailedApplication(applicationTime, job, employer);
        failedApplications.add(failedApplication);
    }

    public List<AppliedJobApplication> getJobApplications(JobSeeker jobSeeker) {
        return appliedApplications.getJobApplications(jobSeeker);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return appliedApplications.findApplicants(jobName, from, to);
    }

    public String exportCsv(LocalDate applicationTime) {
        List<AppliedJobApplication> jobApplications = appliedApplications.getAppliedJobApplications(applicationTime);
        return new JobApplicationCsvExporter().export(jobApplications);
    }

    public String exportHtml(LocalDate applicationTime) {
        List<AppliedJobApplication> jobApplications = appliedApplications.getAppliedJobApplications(applicationTime);
        return new JobApplicationHtmlExporter().export(jobApplications);
    }

    public int getSuccessfulApplications(Employer employer, String jobName) {
        return appliedApplications.getSuccessfulApplications(employer, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream()
                .filter(job -> job.getPublishedJob().getJob().getJobName().equals(jobName)
                        && job.getPublishedJob().getEmployer().getName().equals(employerName))
                .count();
    }
}
