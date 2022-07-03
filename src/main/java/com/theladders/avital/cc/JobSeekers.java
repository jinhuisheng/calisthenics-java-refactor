package com.theladders.avital.cc;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JobSeekers {
    private final HashMap<String, List<JobApplication>> jobSeekerApplications = new HashMap<>();
    private final List<JobApplication> failedApplications = new ArrayList<>();

    void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (jobApplication.getJobType().equals("JReq") && resumeApplicantName == null) {
            failedApplications.add(jobApplication);
            throw new RequiresResumeForJReqJobException();
        }

        if (jobApplication.getJobType().equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<JobApplication> saved = jobSeekerApplications.getOrDefault(jobSeekerName, new ArrayList<>());
        saved.add(jobApplication);
        jobSeekerApplications.put(jobSeekerName, saved);
    }

    List<JobApplication> getJobApplications(String jobSeekerName) {
        return jobSeekerApplications.get(jobSeekerName);
    }

    List<String> findApplicants(Predicate<JobApplication> predicate) {
        List<String> result = new ArrayList<String>() {
        };
        for (Map.Entry<String, List<JobApplication>> set : jobSeekerApplications.entrySet()) {
            String applicant = set.getKey();
            List<JobApplication> jobs = set.getValue();
            boolean hasAppliedToThisJob = jobs.stream().anyMatch(predicate);
            if (hasAppliedToThisJob) {
                result.add(applicant);
            }
        }
        return result;
    }

    Predicate<JobApplication> queryCondition(String jobName, LocalDate from, LocalDate to) {
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

    List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return findApplicants(queryCondition(jobName, from, to));
    }

    List<JobApplication> getJobApplicationsOnDate(LocalDate date, List<JobApplication> jobApplications) {
        return jobApplications.stream()
                .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .collect(Collectors.toList());
    }

    public String exportCsv(LocalDate date) {
        StringBuilder result = new StringBuilder("Employer,Job,Job Type,Applicants,Date" + "\n");
        for (Map.Entry<String, List<JobApplication>> set : jobSeekerApplications.entrySet()) {
            List<JobApplication> appliedOnDate = getJobApplicationsOnDate(date, set.getValue());
            for (JobApplication job : appliedOnDate) {
                result.append(MessageFormat.format("{0},{1},{2},{3},{4}\n",
                        job.getEmployerName(), job.getJobName(), job.getJobType(), set.getKey(), job.getApplicationTime()));
            }
        }
        return result.toString();
    }

    String exportHtml(LocalDate date) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, List<JobApplication>> set : jobSeekerApplications.entrySet()) {
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

    int getSuccessfulApplications(String employerName, String jobName) {
        return (int) jobSeekerApplications.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(job -> job.getEmployerName().equals(employerName) && job.getJobName().equals(jobName))
                ).count();
    }

    int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream()
                .filter(job -> job.getJobName().equals(jobName) && job.getEmployerName().equals(employerName))
                .count();
    }
}
