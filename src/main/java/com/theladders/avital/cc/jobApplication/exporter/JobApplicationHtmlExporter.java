package com.theladders.avital.cc.jobApplication.exporter;

import com.theladders.avital.cc.jobApplication.AppliedJobApplication;

import java.text.MessageFormat;
import java.util.List;

public class JobApplicationHtmlExporter {

    public static final String HEADER = "<!DOCTYPE html>"
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
    public static final String FOOTER = "</tbody>"
            + "</table>"
            + "</body>"
            + "</html>";

    public String export(List<AppliedJobApplication> appliedJobApplications) {
        StringBuilder result = new StringBuilder();
        result.append(HEADER);
        for (AppliedJobApplication appliedJobApplication : appliedJobApplications) {
            addContentLine(result, appliedJobApplication);
        }
        result.append(FOOTER);
        return result.toString();
    }

    private void addContentLine(StringBuilder result, AppliedJobApplication appliedJobApplication) {
        String contentLine = format(appliedJobApplication);
        result.append(contentLine);
    }

    String format(AppliedJobApplication application) {
        return MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>",
                application.getEmployerName(),
                application.getJobName(),
                application.getJobType().name(),
                application.getJobSeekerName(),
                application.getApplicationTime()
        );
    }
}
