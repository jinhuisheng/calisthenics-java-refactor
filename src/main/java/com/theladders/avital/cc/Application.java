package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Jobs jobs = new Jobs();
    private final JobApplications jobApplications = new JobApplications();

    public void save(JobSeeker jobSeeker, Job job) {
        jobs.save(jobSeeker, job);
    }

    public void apply(String resumeApplicantName, JobSeeker jobSeeker, Job job, Employer employer, LocalDate applicationTime)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobApplications.apply(resumeApplicantName, jobSeeker, job, employer, applicationTime);
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

    public List<AppliedJobApplication> getAppliedJobs(JobSeeker jobSeeker) {
        return jobApplications.getJobApplications(jobSeeker);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return jobApplications.findApplicants(jobName, from, to);
    }

    public String export(LocalDate applicationTime, ExportType exportType) {
        if (exportType == ExportType.csv) {
            return jobApplications.exportCsv(applicationTime);
        }
        return jobApplications.exportHtml(applicationTime);
    }

    public int getSuccessfulApplications(Employer employer, String jobName) {
        return jobApplications.getSuccessfulApplications(employer, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return jobApplications.getUnsuccessfulApplications(employerName, jobName);
    }

}
