package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Employers employers = new Employers();
    private final JobSeekers jobSeekers = new JobSeekers();
    private final JobApplications jobApplications = new JobApplications();

    public void apply(String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobApplications.apply(employerName, jobName, jobType, jobSeekerName, resumeApplicantName, applicationTime);
    }

    public void saveJobSeeker(String jobSeeker, String jobName, String jobType) {
        jobSeekers.saveJobSeeker(jobSeeker, jobName, jobType);
    }

    public void publishJob(String employerName, String jobName, String jobType) throws NotSupportedJobTypeException {
        if (!jobType.equals("JReq") && !jobType.equals("ATS")) {
            throw new NotSupportedJobTypeException();
        }
        employers.publishJob(employerName, jobName, jobType);
    }

    public List<JobApplication> getAppliedJobs(String employerName) {
        return jobApplications.getAppliedJobs(employerName);
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

    public String export(String type, LocalDate date) {
        return jobApplications.export(type, date);
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return jobApplications.getSuccessfulApplications(employerName, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return jobApplications.getUnsuccessfulApplications(employerName, jobName);
    }
}
