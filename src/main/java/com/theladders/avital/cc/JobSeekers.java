package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobSeekers {
    private final List<JobSeeker> jobSeekers = new ArrayList<>();
    private final JobApplications jobApplications = new JobApplications();
    private final List<JobApplication> failedApplications = new ArrayList<>();

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
        temp_apply(jobSeekerName, resumeApplicantName, jobApplication);
    }

    public void temp_apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        checkLegal(jobSeekerName, resumeApplicantName, jobApplication);
        saveApply(jobSeekerName, jobApplication);
    }

    private void saveApply(String jobSeekerName, JobApplication jobApplication) {
        JobSeeker jobSeeker = getJobSeeker(jobSeekerName);
        jobSeeker.saveApply(jobApplication);
        if (!jobSeekers.contains(jobSeeker)) {
            jobSeekers.add(jobSeeker);
        }
    }

    private void checkLegal(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (jobApplication.getJobType() == JobType.JReq && resumeApplicantName == null) {
            failedApplications.add(jobApplication);
            throw new RequiresResumeForJReqJobException();
        }
        if (jobApplication.getJobType() == JobType.JReq && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
    }


    public List<JobApplication> getAppliedJobs(String jobSeekerName) {
        return getJobSeeker(jobSeekerName).getJobApplications();
    }

    public List<String> findAppliedJobSeekers(String jobName, LocalDate from, LocalDate to) {
        Predicate<JobSeeker> filterCondition = filter(jobName, from, to);
        return this.jobSeekers.stream()
                .filter(filterCondition)
                .map(JobSeeker::getName)
                .collect(Collectors.toList());
    }

    private Predicate<JobSeeker> filter(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return jobSeeker -> jobSeeker.getJobApplications().stream().anyMatch(job -> job.getJobName().equals(jobName));
        }
        if (jobName == null && to == null) {
            return jobSeeker -> jobSeeker.getJobApplications().stream().anyMatch(job ->
                    !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        if (jobName == null && from == null) {
            return jobSeeker -> jobSeeker.getJobApplications().stream().anyMatch(job ->
                    !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        if (jobName == null) {
            return jobSeeker -> jobSeeker.getJobApplications().stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        if (to != null) {
            return jobSeeker -> jobSeeker.getJobApplications().stream().anyMatch(job -> job.getJobName().equals(jobName) && !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        return jobSeeker -> jobSeeker.getJobApplications().stream().anyMatch(job -> job.getJobName().equals(jobName) && !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
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
