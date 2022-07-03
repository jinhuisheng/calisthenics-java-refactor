package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Application {
    private final HashMap<String, List<Job>> employerPublishedJobs = new HashMap<>();
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
        if (!job.getJobType().equals("JReq") && !job.getJobType().equals("ATS")) {
            throw new NotSupportedJobTypeException();
        }
        List<Job> alreadyPublished = employerPublishedJobs.getOrDefault(employerName, new ArrayList<>());
        alreadyPublished.add(job);
        employerPublishedJobs.put(employerName, alreadyPublished);
    }

    public List<Job> getJobs(String employerName) {
        return employerPublishedJobs.get(employerName);
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

    public String exportHtml(LocalDate date) {
        return jobSeekers.exportHtml(date);
    }
    public String exportCsv(LocalDate date) {
        return jobSeekers.exportCsv(date);
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return jobSeekers.getSuccessfulApplications(employerName, jobName);
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return jobSeekers.getUnsuccessfulApplications(employerName, jobName);
    }

}
