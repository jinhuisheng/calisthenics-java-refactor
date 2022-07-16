package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final Employers employers = new Employers();
    private final HashMap<String, List<Job>> jobSeekerSavedJobs = new HashMap<>();
    final JobSeekers jobSeekers = new JobSeekers();

    public void save(String jobSeeker, Job job) {
        List<Job> alreadyPublished = jobSeekerSavedJobs.getOrDefault(jobSeeker, new ArrayList<>());
        alreadyPublished.add(job);
        jobSeekerSavedJobs.put(jobSeeker, alreadyPublished);
    }

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobSeekers.apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    public void publish(String employerName, Job job) throws NotSupportedJobTypeException {
        employers.publish(employerName, job);
    }

    public List<Job> getJobs(String employerName) {
        return employers.getJobs(employerName);
    }

    public List<Job> getJobSeekSavedJobs(String jobSeeker) {
        return jobSeekerSavedJobs.get(jobSeeker);
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
