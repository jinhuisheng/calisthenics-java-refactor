package com.theladders.avital.cc.jobApplication.exporter;

import com.theladders.avital.cc.jobApplication.AppliedJobApplication;

import java.text.MessageFormat;
import java.util.List;

public class JobApplicationCsvExporter {

    public static final String HEADER = "Employer,Job,Job Type,Applicants,Date" + "\n";

    public String export(List<AppliedJobApplication> appliedJobApplications) {
        StringBuilder result = new StringBuilder(HEADER);
        appliedJobApplications.forEach(appliedJobApplication -> addContent(result, appliedJobApplication));
        return result.toString();
    }

    private void addContent(StringBuilder result, AppliedJobApplication appliedJobApplication) {
        String contentLine = format(appliedJobApplication);
        result.append(contentLine);
    }

    String format(AppliedJobApplication jobApplication) {
        return MessageFormat.format("{0},{1},{2},{3},{4}\n",
                jobApplication.getEmployerName(),
                jobApplication.getJobName(),
                jobApplication.getJobType().name(),
                jobApplication.getJobSeekerName(),
                jobApplication.getApplicationTime()
        );
    }
}
