package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<JobApplication>> temp_applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void save(String employerName, String jobName, String jobType) {
        List<List<String>> saved = jobs.getOrDefault(employerName, new ArrayList<>());
        saved.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(employerName, saved);
    }

    public void apply(String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
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
        List<JobApplication> saved_temp = this.temp_applied.getOrDefault(jobSeekerName, new ArrayList<>());
        saved_temp.add(new JobApplication(jobName, jobType, applicationTime, employerName));
        this.temp_applied.put(jobSeekerName, saved_temp);
    }

    public void publish(String employerName, String jobName, String jobType) throws NotSupportedJobTypeException {
        if (!jobType.equals("JReq") && !jobType.equals("ATS")) {
            throw new NotSupportedJobTypeException();
        }

        List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

        alreadyPublished.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(employerName, alreadyPublished);
    }

    public List<List<String>> getJobs(String employerName) {
        return jobs.get(employerName);
    }

    public List<JobApplication> getAppliedJobs(String employerName) {
        return temp_applied.get(employerName);
    }

    public List<String> findApplicants(String jobName, LocalDate from, LocalDate to) {
        Predicate<JobApplication> condition = condition(jobName, from, to);
        return findApplicants(condition);
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
        for (Entry<String, List<JobApplication>> set : this.temp_applied.entrySet()) {
            String applicant = set.getKey();
            List<JobApplication> jobs = set.getValue();
            boolean hasAppliedToThisJob = jobs.stream().anyMatch(predicate);
            if (hasAppliedToThisJob) {
                result.add(applicant);
            }
        }
        return result;
    }

    public String export(String type, LocalDate date) {
        if (type.equals("csv")) {
            return exportCsv(date);
        } else {
            return exportHtml(date);
        }
    }

    private String exportHtml(LocalDate date) {
        String content = "";
        for (Entry<String, List<JobApplication>> set : this.temp_applied.entrySet()) {
            List<JobApplication> jobs1 = set.getValue();
            List<JobApplication> appliedOnDate = jobs1.stream()
                    .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .collect(Collectors.toList());
            for (JobApplication job : appliedOnDate) {
                content = content.concat("<tr>" + "<td>" + job.getEmployerName() + "</td>" + "<td>" + job.getJobName() + "</td>" + "<td>" + job.getJobType() + "</td>" + "<td>" + set.getKey() + "</td>" + "<td>" + job.getApplicationTime() + "</td>" + "</tr>");
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
                + content
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    private String exportCsv(LocalDate date) {
        StringBuilder result = new StringBuilder("Employer,Job,Job Type,Applicants,Date" + "\n");
        for (Entry<String, List<JobApplication>> set : this.temp_applied.entrySet()) {
            List<JobApplication> appliedJobs = set.getValue();
            String appliedJobStr = appliedJobs.stream()
                    .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .map(job -> job.getEmployerName() + "," + job.getJobName() + "," + job.getJobType() + "," + set.getKey() + "," + job.getApplicationTime() + "\n")
                    .collect(Collectors.joining());
            result.append(appliedJobStr);
        }
        return result.toString();
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        for (Entry<String, List<JobApplication>> set : this.temp_applied.entrySet()) {
            List<JobApplication> jobs = set.getValue();
            result += jobs.stream().anyMatch(job -> job.getEmployerName().equals(employerName) && job.getJobName().equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }
}
