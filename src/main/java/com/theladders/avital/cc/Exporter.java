package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class Exporter {
    public static String exportHtml(HashMap<String, List<JobApplication>> jobApplications, LocalDate date) {
        String content = getHtmlContent(jobApplications, date);
        return exportHtml(content);
    }

    private static String getHtmlContent(HashMap<String, List<JobApplication>> jobApplications, LocalDate date) {
        return jobApplications.entrySet().stream().map(entry ->
                entry.getValue().stream()
                        .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .map(job -> "<tr>" + "<td>" + job.getEmployerName() + "</td>" + "<td>" + job.getJobName() + "</td>" + "<td>" + job.getJobType() + "</td>" + "<td>" + entry.getKey() + "</td>" + "<td>" + job.getApplicationTime() + "</td>" + "</tr>")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }


    private static String exportHtml(String content) {
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

    public static String exportCsv(HashMap<String, List<JobApplication>> jobApplications, LocalDate date) {
        String content = getCsvContent(jobApplications, date);
        return exportCsv(content);
    }

    private static String getCsvContent(HashMap<String, List<JobApplication>> jobApplications, LocalDate date) {
        return jobApplications.entrySet().stream().map(entry ->
                entry.getValue().stream()
                        .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .map(job -> job.getEmployerName() + "," + job.getJobName() + "," + job.getJobType() + "," + entry.getKey() + "," + job.getApplicationTime() + "\n")
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }


    private static String exportCsv(String content) {
        String startStr = "Employer,Job,Job Type,Applicants,Date" + "\n";
        return startStr + content;
    }

}
