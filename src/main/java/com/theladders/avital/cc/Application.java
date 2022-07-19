package com.theladders.avital.cc;

import com.theladders.avital.cc.employer.Employer;
import com.theladders.avital.cc.job.Job;
import com.theladders.avital.cc.job.Jobs;
import com.theladders.avital.cc.jobApplication.AppliedJobApplication;
import com.theladders.avital.cc.jobApplication.exporter.ExportType;
import com.theladders.avital.cc.jobApplication.JobApplications;
import com.theladders.avital.cc.jobseeker.JobSeeker;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Jobs jobs = new Jobs();
    private final JobApplications jobApplications = new JobApplications();

    public void save(JobSeeker jobSeeker, Job job) {
        jobs.save(jobSeeker, job);
    }

    public void apply(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime, String resumeApplicantName)
            throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobApplications.apply(employer, job, jobSeeker, applicationTime, resumeApplicantName);
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
