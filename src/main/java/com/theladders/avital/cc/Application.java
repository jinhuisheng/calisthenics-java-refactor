package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Employers employers = new Employers();
    private final JobSeekers jobSeekers = new JobSeekers();
    private final JobApplications jobApplications = new JobApplications();

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobApplications.apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    public void saveJobSeeker(String jobSeeker, String jobName, JobType jobType) {
        jobSeekers.saveJobSeeker(jobSeeker, jobName, jobType);
    }

    public void publishJob(String employerName, String jobName, JobType jobType) throws NotSupportedJobTypeException {
        employers.publishJob(employerName, jobName, jobType);
    }

    public List<JobApplication> getAppliedJobs(String jobSeekerName) {
        return jobApplications.getAppliedJobs(jobSeekerName);
    }

    public List<Job> getJobSeekerJobs(String jobSeeker) {
        return jobSeekers.getJobSeekerJobs(jobSeeker);
    }

    public List<Job> getEmployerPublishedJobs(String employerName) {
        return employers.getEmployerPublishedJobs(employerName);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return jobApplications.findApplicants(jobName, from, to);
    }

    public String exportCsv(LocalDate date) {
        return jobApplications.exportCsv(date);
    }

    public String exportHtml(LocalDate date) {
        return jobApplications.exportHtml(date);
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return jobApplications.getSuccessfulApplications(employerName, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return jobApplications.getUnsuccessfulApplications(employerName, jobName);
    }
}
