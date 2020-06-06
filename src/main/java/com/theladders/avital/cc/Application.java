package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Employers employers = new Employers();
    private final JobSeekers jobSeekers = new JobSeekers();

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobSeekers.apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    public void saveJobSeeker(String jobSeeker, String jobName, JobType jobType) {
        jobSeekers.saveJobSeeker(jobSeeker, jobName, jobType);
    }

    public void publishJob(String employerName, String jobName, JobType jobType) throws NotSupportedJobTypeException {
        employers.publishJob(employerName, jobName, jobType);
    }

    public List<JobApplication> getAppliedJobs(String employerName) {
        return jobSeekers.getEmployerAppliedJobs(employerName);
    }

    public List<Job> getJobSeekerJobs(String jobSeeker) {
        return jobSeekers.getJobSeekerJobs(jobSeeker);
    }

    public List<Job> getEmployerPublishedJobs(String employerName) {
        return employers.getEmployerPublishedJobs(employerName);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return jobSeekers.findApplicants(jobName, from, to);
    }

    public String exportCsv(LocalDate date) {
        return jobSeekers.exportCsv(date);
    }

    public String exportHtml(LocalDate date) {
        return jobSeekers.exportHtml(date);
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return jobSeekers.getSuccessfulApplications(employerName, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return jobSeekers.getUnsuccessfulApplications(employerName, jobName);
    }
}
