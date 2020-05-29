package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        if (command.equals("publish")) {
            if (!jobType.equals("JReq") && !jobType.equals("ATS")) {
                throw new NotSupportedJobTypeException();
            }

            List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

            alreadyPublished.add(new ArrayList<String>() {{
                add(jobName);
                add(jobType);
            }});
            jobs.put(employerName, alreadyPublished);
        } else if (command.equals("save")) {
            List<List<String>> saved = jobs.getOrDefault(employerName, new ArrayList<>());

            saved.add(new ArrayList<String>() {{
                add(jobName);
                add(jobType);
            }});
            jobs.put(employerName, saved);
        } else if (command.equals("apply")) {
            if (jobType.equals("JReq") && resumeApplicantName == null) {
                List<String> failedApplication = new ArrayList<String>() {{
                    add(jobName);
                    add(jobType);
                    add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    add(employerName);
                }};
                failedApplications.add(failedApplication);
                throw new RequiresResumeForJReqJobException();
            }

            if (jobType.equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
                throw new InvalidResumeException();
            }
            List<List<String>> saved = this.applied.getOrDefault(jobSeekerName, new ArrayList<>());

            saved.add(new ArrayList<String>() {{
                add(jobName);
                add(jobType);
                add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                add(employerName);
            }});
            applied.put(jobSeekerName, saved);
        }
    }

    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals("applied")) {
            return applied.get(employerName);
        }

        return jobs.get(employerName);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        Predicate<Entry<String, List<List<String>>>> predicate = getEntryPredicate(jobName, from, to);
        return this.applied.entrySet().stream()
                .filter(predicate)
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    private Predicate<Entry<String, List<List<String>>>> getEntryPredicate(String jobName, LocalDate from, LocalDate to) {
        Predicate<Entry<String, List<List<String>>>> predicate;
        if (from == null && to == null) {
            predicate = entry -> entry.getValue().stream().anyMatch(job -> job.get(0).equals(jobName));
        } else if (jobName == null && to == null) {
            predicate = entry -> entry.getValue().stream().anyMatch(job ->
                    !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        } else if (jobName == null && from == null) {
            predicate = entry -> entry.getValue().stream().anyMatch(job ->
                    !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        } else if (jobName == null) {
            predicate = entry -> entry.getValue().stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        } else if (to != null) {
            predicate = entry -> entry.getValue().stream().anyMatch(job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        } else {
            predicate = entry -> entry.getValue().stream().anyMatch(job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        return predicate;
    }

    public String export(String type, LocalDate date) {
        if (type.equals("csv")) {
            return exportCsv(date);
        } else {
            return exportHtml(date);
        }
    }

    private String exportHtml(LocalDate date) {
        String content = getHtmlContent(date);
        return exportHtml(content);
    }

    private String getHtmlContent(LocalDate date) {
        return this.applied.entrySet().stream().map(entry ->
                entry.getValue().stream()
                        .filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .map(job -> "<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + entry.getKey() + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }

    private String exportHtml(String content) {
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
                + content
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    private String exportCsv(LocalDate date) {
        String content = getCsvContent(date);
        return exportCsv(content);
    }

    private String getCsvContent(LocalDate date) {
        return this.applied.entrySet().stream().map(entry ->
                entry.getValue().stream()
                        .filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .map(job -> job.get(3) + "," + job.get(0) + "," + job.get(1) + "," + entry.getKey() + "," + job.get(2) + "\n")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }

    private String exportCsv(String content) {
        String startStr = "Employer,Job,Job Type,Applicants,Date" + "\n";
        return startStr + content;
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return (int) this.applied.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(job -> job.get(3).equals(employerName) && job.get(0).equals(jobName)))
                .count();
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }
}
