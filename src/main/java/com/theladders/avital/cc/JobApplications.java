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

    public void apply(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        checkLegal(jobSeekerName, resumeApplicantName, jobApplication);
        saveApply(jobSeekerName, jobApplication);
    }

    private void saveApply(String jobSeekerName, JobApplication jobApplication) {
        List<JobApplication> saved = this.jobApplications.getOrDefault(jobSeekerName, new ArrayList<>());
        saved.add(jobApplication);
        jobApplications.put(jobSeekerName, saved);
    }

    private void checkLegal(String jobSeekerName, String resumeApplicantName, JobApplication jobApplication) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (jobApplication.getJobType().equals("JReq") && resumeApplicantName == null) {
            failedApplications.add(jobApplication);
            throw new RequiresResumeForJReqJobException();
        }
        if (jobApplication.getJobType().equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
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
        return Exporter.exportHtml(this.jobApplications, date);
    }

    public String exportCsv(LocalDate date) {
        return Exporter.exportCsv(this.jobApplications, date);
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
