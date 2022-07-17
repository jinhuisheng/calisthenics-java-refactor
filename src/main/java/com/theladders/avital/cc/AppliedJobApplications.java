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
    private final HashMap<String, List<AppliedJobApplication>> appliedApplications = new HashMap<>();

    void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws InvalidResumeException {
        if (jobApplication.getJobType() == JobType.JReq && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<AppliedJobApplication> saved = appliedApplications.getOrDefault(jobSeekerName, new ArrayList<>());
        AppliedJobApplication appliedJobApplication = new AppliedJobApplication(jobApplication.getJobName(), jobApplication.getApplicationTime(), jobApplication.getEmployerName(), jobApplication.getJobType());
        saved.add(appliedJobApplication);
        appliedApplications.put(jobSeekerName, saved);
    }

    List<AppliedJobApplication> getJobApplications(String jobSeekerName) {
        return appliedApplications.get(jobSeekerName);
    }

    int getSuccessfulApplications(String employerName, String jobName) {
        return (int) appliedApplications.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(job -> job.getEmployer().getName().equals(employerName) && job.getJob().getJobName().equals(jobName))
                ).count();
    }

    List<String> findApplicants(Predicate<AppliedJobApplication> predicate) {
        List<String> result = new ArrayList<String>() {
        };
        for (Map.Entry<String, List<AppliedJobApplication>> set : appliedApplications.entrySet()) {
            String applicant = set.getKey();
            List<AppliedJobApplication> jobs = set.getValue();
            boolean hasAppliedToThisJob = jobs.stream().anyMatch(predicate);
            if (hasAppliedToThisJob) {
                result.add(applicant);
            }
        }
        return result;
    }

    String exportCsv(LocalDate applicationTime) {
        String header = "Employer,Job,Job Type,Applicants,Date" + "\n";
        StringBuilder result = new StringBuilder(header);
        for (Map.Entry<String, List<AppliedJobApplication>> set : appliedApplications.entrySet()) {
            List<AppliedJobApplication> appliedOnDate = getJobApplicationsOnDate(applicationTime, set.getValue());
            String jobSeekerName = set.getKey();
            for (AppliedJobApplication job : appliedOnDate) {
                String content = getCsvContentLine(jobSeekerName, job);
                result.append(content);
            }
        }
        return result.toString();
    }

    private String getCsvContentLine(String key, AppliedJobApplication job) {
        return MessageFormat.format("{0},{1},{2},{3},{4}\n",
                job.getEmployer().getName(), job.getJob().getJobName(), job.getJob().getJobType().name(), key, job.getApplicationTime());
    }

    String exportHtml(LocalDate applicationTime) {
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
        for (Map.Entry<String, List<AppliedJobApplication>> set : appliedApplications.entrySet()) {
            List<AppliedJobApplication> appliedOnDate = getJobApplicationsOnDate(applicationTime, set.getValue());
            String jobSeekerName = set.getKey();
            for (AppliedJobApplication job : appliedOnDate) {
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

    private String getHtmlContentLine(String key, AppliedJobApplication job) {
        return MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>",
                job.getEmployer().getName(), job.getJob().getJobName(), job.getJob().getJobType().name(), key, job.getApplicationTime());
    }

    List<AppliedJobApplication> getJobApplicationsOnDate(LocalDate date, List<AppliedJobApplication> jobApplications) {
        return jobApplications.stream()
                .filter(job -> job.getApplicationTime().equals(date))
                .collect(Collectors.toList());
    }

    Predicate<AppliedJobApplication> queryCondition(String jobName, LocalDate from, LocalDate to) {
        Predicate<AppliedJobApplication> predicate = job -> true;
        if (from != null) {
            predicate = predicate.and(job -> job.isEqualOrAfter(from));
        }
        if (to != null) {
            predicate = predicate.and(job -> job.isEqualOrBefore(to));
        }
        if (jobName != null) {
            predicate = predicate.and(job -> job.getJob().getJobName().equals(jobName));
        }
        return predicate;
    }

    List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        return findApplicants(queryCondition(jobName, from, to));
    }
}
