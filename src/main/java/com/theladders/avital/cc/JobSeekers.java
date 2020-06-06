package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobSeekers {
    private final HashMap<String, List<Job>> jobSeekers = new HashMap<>();
    private final JobApplications jobApplications = new JobApplications();

    public void saveJobSeeker(String jobSeeker, String jobName, JobType type) {
        List<Job> saved = jobSeekers.getOrDefault(jobSeeker, new ArrayList<>());
        saved.add(new Job(jobName, type));
        jobSeekers.put(jobSeeker, saved);
    }

    public List<Job> getJobSeekerJobs(String jobSeeker) {
        return jobSeekers.get(jobSeeker);
    }

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        jobApplications.apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    public List<JobApplication> getEmployerAppliedJobs(String employerName) {
        return jobApplications.getAppliedJobs(employerName);
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
