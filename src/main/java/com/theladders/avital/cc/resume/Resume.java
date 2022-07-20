package com.theladders.avital.cc.resume;

public class Resume {

    private final String applicantName;

    public Resume(String applicantName) {

        this.applicantName = applicantName;
    }

    public boolean isMatched(String jobSeeker) {
        return applicantName.equals(jobSeeker);
    }

    public boolean isExist() {
        return applicantName == null;
    }
}
