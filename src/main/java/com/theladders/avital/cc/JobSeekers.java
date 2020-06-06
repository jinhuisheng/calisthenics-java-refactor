package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobSeekers {
    private final List<JobSeeker> jobSeekers = new ArrayList<>();
    private final JobApplications jobApplications = new JobApplications();

    public void saveJobSeeker(String jobSeekerName, String jobName, JobType jobType) {
        JobSeeker jobSeeker = getJobSeeker(jobSeekerName);
        jobSeeker.saveJob(new Job(jobName, jobType));
        jobSeekers.add(jobSeeker);
    }

    private JobSeeker getJobSeeker(String jobSeekerName) {
        return jobSeekers.stream()
                .filter(jobSeeker -> jobSeeker.getName().equals(jobSeekerName))
                .findFirst()
                .orElse(new JobSeeker(jobSeekerName));
    }

    public List<Job> getJobSeekerJobs(String jobSeekerName) {
        return jobSeekers.stream()
                .filter(seeker -> seeker.getName().equals(jobSeekerName))
                .map(JobSeeker::getSavedJobs)
                .findFirst()
                .orElseGet(ArrayList::new);
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
