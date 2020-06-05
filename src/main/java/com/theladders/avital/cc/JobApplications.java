package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class JobApplications {
    private final HashMap<String, List<JobApplication>> jobApplications = new HashMap<>();
    private final List<JobApplication> failedApplications = new ArrayList<>();

    public void apply(String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (jobType.equals("JReq") && resumeApplicantName == null) {
            failedApplications.add(new JobApplication(jobName, jobType, applicationTime, employerName));
            throw new RequiresResumeForJReqJobException();
        }
        if (jobType.equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<JobApplication> saved = this.jobApplications.getOrDefault(jobSeekerName, new ArrayList<>());
        saved.add(new JobApplication(jobName, jobType, applicationTime, employerName));
        jobApplications.put(jobSeekerName, saved);
    }

    public List<JobApplication> getAppliedJobs(String employerName) {
        return jobApplications.get(employerName);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        Predicate<Map.Entry<String, List<JobApplication>>> filterCondition = filter(jobName, from, to);
        return this.jobApplications.entrySet().stream()
                .filter(filterCondition)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Predicate<Map.Entry<String, List<JobApplication>>> filter(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return entry -> entry.getValue().stream().anyMatch(job -> job.getJobName().equals(jobName));
        }
        if (jobName == null && to == null) {
            return entry -> entry.getValue().stream().anyMatch(job ->
                    !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        if (jobName == null && from == null) {
            return entry -> entry.getValue().stream().anyMatch(job ->
                    !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        if (jobName == null) {
            return entry -> entry.getValue().stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        if (to != null) {
            return entry -> entry.getValue().stream().anyMatch(job -> job.getJobName().equals(jobName) && !to.isBefore(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        return entry -> entry.getValue().stream().anyMatch(job -> job.getJobName().equals(jobName) && !from.isAfter(LocalDate.parse(job.getApplicationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

    public String exportHtml(LocalDate date) {
        String content = getHtmlContent(date);
        return exportHtml(content);
    }

    private String getHtmlContent(LocalDate date) {
        return this.jobApplications.entrySet().stream().map(entry ->
                entry.getValue().stream()
                        .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .map(job -> "<tr>" + "<td>" + job.getEmployerName() + "</td>" + "<td>" + job.getJobName() + "</td>" + "<td>" + job.getJobType() + "</td>" + "<td>" + entry.getKey() + "</td>" + "<td>" + job.getApplicationTime() + "</td>" + "</tr>")
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

    public String exportCsv(LocalDate date) {
        String content = getCsvContent(date);
        return exportCsv(content);
    }

    private String getCsvContent(LocalDate date) {
        return this.jobApplications.entrySet().stream().map(entry ->
                entry.getValue().stream()
                        .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .map(job -> job.getEmployerName() + "," + job.getJobName() + "," + job.getJobType() + "," + entry.getKey() + "," + job.getApplicationTime() + "\n")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }


    private String exportCsv(String content) {
        String startStr = "Employer,Job,Job Type,Applicants,Date" + "\n";
        return startStr + content;
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        return (int) this.jobApplications.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(job -> job.getEmployerName().equals(employerName) && job.getJobName().equals(jobName)))
                .count();
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream()
                .filter(job -> job.getJobName().equals(jobName)
                        && job.getEmployerName().equals(employerName))
                .count();
    }


}
