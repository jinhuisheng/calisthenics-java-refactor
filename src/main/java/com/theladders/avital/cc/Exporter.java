package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huisheng.jin
 * @date 2020/6/5.
 */
public class Exporter {
    public static String exportHtml(Set<Map.Entry<String, List<JobApplication>>> data, LocalDate date) {
        String content = getHtmlContent(data, date);
        return exportHtml(content);
    }

    private static String getHtmlContent(Set<Map.Entry<String, List<JobApplication>>> data, LocalDate date) {
        return data
                .stream()
                .map(entry ->
                        entry.getValue().stream()
                                .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                                .map(job -> "<tr>" + "<td>" + job.getEmployerName() + "</td>" + "<td>" + job.getJobName() + "</td>" + "<td>" + job.getJobType().name() + "</td>" + "<td>" + entry.getKey() + "</td>" + "<td>" + job.getApplicationTime() + "</td>" + "</tr>")
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

    public static String exportCsv(Set<Map.Entry<String, List<JobApplication>>> data, LocalDate date) {
        String content = getCsvContent(data, date);
        return exportCsv(content);
    }

    private static String getCsvContent(Set<Map.Entry<String, List<JobApplication>>> data, LocalDate date) {
        return data
                .stream()
                .map(entry ->
                        entry.getValue().stream()
                                .filter(job -> job.getApplicationTime().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                                .map(job -> job.getEmployerName() + "," + job.getJobName() + "," + job.getJobType().name() + "," + entry.getKey() + "," + job.getApplicationTime() + "\n")
                                .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }


    private static String exportCsv(String content) {
        String startStr = "Employer,Job,Job Type,Applicants,Date" + "\n";
        return startStr + content;
    }

}
