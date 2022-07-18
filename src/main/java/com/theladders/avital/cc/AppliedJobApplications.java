package com.theladders.avital.cc;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AppliedJobApplications {
    private final List<AppliedJobApplication> appliedApplications = new ArrayList<>();

    void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws InvalidResumeException {
        if (jobApplication.getJobType() == JobType.JReq && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        AppliedJobApplication appliedJobApplication = new AppliedJobApplication(jobApplication.getJobName(), jobApplication.getApplicationTime(), jobApplication.getEmployerName(), jobApplication.getJobType(), new JobSeeker(jobSeekerName));
        appliedApplications.add(appliedJobApplication);
    }

    List<AppliedJobApplication> getJobApplications(String jobSeekerName) {
        return appliedApplications.stream().filter(temp_appliedApplication -> temp_appliedApplication.getJobSeeker().equals(new JobSeeker(jobSeekerName))).collect(Collectors.toList());
    }

    int getSuccessfulApplications(String employerName, String jobName) {
        return (int) appliedApplications.stream()
                .filter(job -> job.getPublishedJob().getEmployer().getName().equals(employerName) && job.getPublishedJob().getJob().getJobName().equals(jobName))
                .count();
    }

    List<String> findApplicants(Predicate<AppliedJobApplication> predicate) {
        return appliedApplications.stream()
                .filter(predicate)
                .map(appliedJobApplication -> appliedJobApplication.getJobSeeker().getName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    String exportCsv(LocalDate applicationTime) {
        String header = "Employer,Job,Job Type,Applicants,Date" + "\n";
        StringBuilder result = new StringBuilder(header);
        appliedApplications.stream()
                .filter(job -> job.getApplicationTime().equals(applicationTime))
                .collect(Collectors.groupingBy(appliedJobApplication -> appliedJobApplication.getJobSeeker().getName())).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(item -> item.getValue().stream())
                .forEach(appliedJobApplication -> {
                    String csvContentLine = getCsvContentLine(appliedJobApplication);
                    result.append(csvContentLine);
                });
        return result.toString();
    }

    private String getCsvContentLine(AppliedJobApplication job) {
        return MessageFormat.format("{0},{1},{2},{3},{4}\n",
                job.getPublishedJob().getEmployer().getName(), job.getPublishedJob().getJob().getJobName(), job.getPublishedJob().getJob().getJobType().name(), job.getJobSeeker().getName(), job.getApplicationTime());
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
        appliedApplications.stream()
                .filter(job -> job.getApplicationTime().equals(applicationTime))
                .collect(Collectors.groupingBy(appliedJobApplication -> appliedJobApplication.getJobSeeker().getName())).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(item -> item.getValue().stream())
                .forEach(appliedJobApplication -> result.append(getHtmlContentLine(appliedJobApplication)));
        String footer = "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
        return header + result + footer;
    }

    private String getHtmlContentLine(AppliedJobApplication job) {
        return MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>",
                job.getPublishedJob().getEmployer().getName(), job.getPublishedJob().getJob().getJobName(), job.getPublishedJob().getJob().getJobType().name(), job.getJobSeeker().getName(), job.getApplicationTime());
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
            predicate = predicate.and(job -> job.getPublishedJob().getJob().getJobName().equals(jobName));
        }
        return predicate;
    }

    List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        Predicate<AppliedJobApplication> condition = queryCondition(jobName, from, to);
        return findApplicants(condition);
    }
}
