package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Jobs jobs = new Jobs();
    private final JobSeekers jobSeekers = new JobSeekers();

    public void save(JobSeeker jobSeeker, Job job) {
        jobs.save(jobSeeker, job);
    }

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobSeekers.apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    public void publish(Employer employer, Job job) throws NotSupportedJobTypeException {
        jobs.publish(job, employer);
    }

    public List<Job> getJobs(Employer employer) {
        return jobs.getJobs(employer);
    }

    public List<Job> getJobSeekSavedJobs(JobSeeker jobSeeker) {
        return jobs.getJobs(jobSeeker);
    }

    public List<JobApplication> getAppliedJobs(String jobSeekerName) {
        return jobSeekers.getJobApplications(jobSeekerName);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return jobSeekers.findApplicants(jobName, from, to);
    }

    public String export(LocalDate applicationTime, ExportType exportType) {
        if (exportType == ExportType.csv) {
            return jobSeekers.exportCsv(applicationTime);
        }
        return jobSeekers.exportHtml(applicationTime);
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return jobSeekers.getSuccessfulApplications(employerName, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return jobSeekers.getUnsuccessfulApplications(employerName, jobName);
    }

}
