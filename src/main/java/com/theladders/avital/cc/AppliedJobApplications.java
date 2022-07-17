package com.theladders.avital.cc;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AppliedJobApplications {
    private final HashMap<String, List<JobApplication>> appliedApplications = new HashMap<>();

    void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws InvalidResumeException {
        if (jobApplication.getJobType() == JobType.JReq && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<JobApplication> saved = appliedApplications.getOrDefault(jobSeekerName, new ArrayList<>());
        saved.add(jobApplication);
        appliedApplications.put(jobSeekerName, saved);
    }

    List<JobApplication> getJobApplications(String jobSeekerName) {
        return appliedApplications.get(jobSeekerName);
    }

    int getSuccessfulApplications(String employerName, String jobName) {
        return (int) appliedApplications.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(job -> job.getEmployerName().equals(employerName) && job.getJobName().equals(jobName))
                ).count();
    }

    List<String> findApplicants(Predicate<JobApplication> predicate) {
        List<String> result = new ArrayList<String>() {
        };
        for (Map.Entry<String, List<JobApplication>> set : appliedApplications.entrySet()) {
            String applicant = set.getKey();
            List<JobApplication> jobs = set.getValue();
            boolean hasAppliedToThisJob = jobs.stream().anyMatch(predicate);
            if (hasAppliedToThisJob) {
                result.add(applicant);
            }
        }
        return result;
    }

    String exportCsv(LocalDate applicationTime, JobApplications jobApplications) {
        String header = "Employer,Job,Job Type,Applicants,Date" + "\n";
        StringBuilder result = new StringBuilder(header);
        for (Map.Entry<String, List<JobApplication>> set : appliedApplications.entrySet()) {
            List<JobApplication> appliedOnDate = jobApplications.appliedApplications.getJobApplicationsOnDate(applicationTime, set.getValue());
            String jobSeekerName = set.getKey();
            for (JobApplication job : appliedOnDate) {
                String content = getCsvContentLine(jobSeekerName, job);
                result.append(content);
            }
        }
        return result.toString();
    }

    private String getCsvContentLine(String key, JobApplication job) {
        return MessageFormat.format("{0},{1},{2},{3},{4}\n",
                job.getEmployerName(), job.getJobName(), job.getJobType().name(), key, job.getApplicationTime());
    }

    String exportHtml(LocalDate applicationTime, JobApplications jobApplications) {
        StringBuilder result = new StringBuilder();
        String header = "<!DOCTYPE html>"
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
                + "<tbody>";
        for (Map.Entry<String, List<JobApplication>> set : appliedApplications.entrySet()) {
            List<JobApplication> appliedOnDate = jobApplications.appliedApplications.getJobApplicationsOnDate(applicationTime, set.getValue());
            String jobSeekerName = set.getKey();
            for (JobApplication job : appliedOnDate) {
                String content = getHtmlContentLine(jobSeekerName, job);
                result.append(content);
            }
        }

        String footer = "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
        return header + result + footer;
    }

    private String getHtmlContentLine(String key, JobApplication job) {
        return MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>",
                job.getEmployerName(), job.getJobName(), job.getJobType().name(), key, job.getApplicationTime());
    }

    List<JobApplication> getJobApplicationsOnDate(LocalDate date, List<JobApplication> jobApplications) {
        return jobApplications.stream()
                .filter(job -> job.getApplicationTime().equals(date))
                .collect(Collectors.toList());
    }

    Predicate<JobApplication> queryCondition(String jobName, LocalDate from, LocalDate to) {
        Predicate<JobApplication> predicate = job -> true;
        if (from != null) {
            predicate = predicate.and(job -> job.isEqualOrAfter(from));
        }
        if (to != null) {
            predicate = predicate.and(job -> job.isEqualOrBefore(to));
        }
        if (jobName != null) {
            predicate = predicate.and(job -> job.getJobName().equals(jobName));
        }
        return predicate;
    }

    List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return findApplicants(queryCondition(jobName, from, to));
    }
}
