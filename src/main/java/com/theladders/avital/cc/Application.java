package com.theladders.avital.cc;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    private final HashMap<String, List<Job>> jobs = new HashMap<>();
    private final HashMap<String, List<JobApplication>> applied = new HashMap<>();
    private final List<JobApplication> failedApplications = new ArrayList<>();

    public void save(String employerName, Job job) {
        List<Job> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());
        alreadyPublished.add(job);
        jobs.put(employerName, alreadyPublished);
    }

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (jobApplication.getJobType().equals("JReq") && resumeApplicantName == null) {
            failedApplications.add(jobApplication);
            throw new RequiresResumeForJReqJobException();
        }

        if (jobApplication.getJobType().equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<JobApplication> saved = this.applied.getOrDefault(jobSeekerName, new ArrayList<>());
        saved.add(jobApplication);
        this.applied.put(jobSeekerName, saved);
    }

    public void publish(String employerName, Job job) throws NotSupportedJobTypeException {
        if (!job.getJobType().equals("JReq") && !job.getJobType().equals("ATS")) {
            throw new NotSupportedJobTypeException();
        }
        List<Job> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());
        alreadyPublished.add(job);
        jobs.put(employerName, alreadyPublished);
    }

    public List<Job> getJobs(String employerName) {
        return jobs.get(employerName);
    }

    public List<JobApplication> getAppliedJobs(String employerName) {
        return applied.get(employerName);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return findApplicants(condition(jobName, from, to));
    }

    private Predicate<JobApplication> condition(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return job -> job.getJobName().equals(jobName);
        }
        if (jobName == null && to == null) {
            return job -> !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (jobName == null && from == null) {
            return job -> !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (jobName == null) {
            return job -> !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (to != null) {
            return job -> job.getJobName().equals(jobName) && !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return job -> job.getJobName().equals(jobName) && !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private List<String> findApplicants(Predicate<JobApplication> predicate) {
        List<String> result = new ArrayList<String>() {
        };
        for (Entry<String, List<JobApplication>> set : this.applied.entrySet()) {
            String applicant = set.getKey();
            List<JobApplication> jobs = set.getValue();
            boolean hasAppliedToThisJob = jobs.stream().anyMatch(predicate);
            if (hasAppliedToThisJob) {
                result.add(applicant);
            }
        }
        return result;
    }

    public String export(LocalDate date) {
        StringBuilder result = new StringBuilder();
        for (Entry<String, List<JobApplication>> set : this.applied.entrySet()) {
            List<JobApplication> appliedOnDate = getJobApplicationsOnDate(date, set.getValue());
            for (JobApplication job : appliedOnDate) {
                result.append(MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>",
                        job.getEmployerName(), job.getJobName(), job.getJobType(), set.getKey(), job.getApplicationTime()));
            }
        }

        return "<!DOCTYPE html>"
                + "<body>"
                + "<table>"
                + "<thead>"
                + "<tr>"
                + "<th>Employer</th>"
                + "<th>Job</th>"
                + "<th>Job Type</th>"
                + "<th>Applicants</th>"
                + "<th>Date</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + result
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    private List<JobApplication> getJobApplicationsOnDate(LocalDate date, List<JobApplication> jobApplications) {
        return jobApplications.stream()
                .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .collect(Collectors.toList());
    }

    public String exportCsv(LocalDate date) {
        StringBuilder result = new StringBuilder("Employer,Job,Job Type,Applicants,Date" + "\n");
        for (Entry<String, List<JobApplication>> set : this.applied.entrySet()) {
            List<JobApplication> appliedOnDate = getJobApplicationsOnDate(date, set.getValue());
            for (JobApplication job : appliedOnDate) {
                result.append(MessageFormat.format("{0},{1},{2},{3},{4}\n",
                        job.getEmployerName(), job.getJobName(), job.getJobType(), set.getKey(), job.getApplicationTime()));
            }
        }
        return result.toString();
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return (int) this.applied.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(job -> job.getEmployerName().equals(employerName) && job.getJobName().equals(jobName))
                ).count();
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream()
                .filter(job -> job.getJobName().equals(jobName) && job.getEmployerName().equals(employerName))
                .count();
    }
}
