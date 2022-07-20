package com.theladders.avital.cc.jobApplication;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.InvalidResumeException;
import com.theladders.avital.cc.jobApplication.exporter.JobApplicationCsvExporter;
import com.theladders.avital.cc.jobApplication.exporter.JobApplicationHtmlExporter;
import com.theladders.avital.cc.jobseeker.JobSeeker;
import com.theladders.avital.cc.RequiresResumeForJReqJobException;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.JobType;
import com.theladders.avital.cc.resume.Resume;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobApplications {
    final AppliedJobApplications appliedApplications = new AppliedJobApplications();
    private final List<FailedApplication> failedApplications = new ArrayList<>();

    public void apply(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime, Resume resume)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        checkResumeValid(employer, job, jobSeeker, applicationTime, resume);
        appliedApplications.apply(employer, job, jobSeeker, applicationTime);
    }

    private void checkResumeValid(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime, Resume resume)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.isMatched(JobType.JReq) && resume.isExist()) {
            addFailedApplications(job, employer, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }
        if (job.isMatched(JobType.JReq) && !resume.isMatched(jobSeeker.getName())) {
            throw new InvalidResumeException();
        }
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
        List<AppliedJobApplication> jobApplications = appliedApplications.getJobApplications(applicationTime);
        return new JobApplicationCsvExporter().export(jobApplications);
    }

    public String exportHtml(LocalDate applicationTime) {
        List<AppliedJobApplication> jobApplications = appliedApplications.getJobApplications(applicationTime);
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
