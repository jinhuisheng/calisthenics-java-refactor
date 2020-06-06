package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huisheng.jin
 * @date 2020/6/6.
 */
public class Employer {
    private String name;
    private List<Job> publishedJobs = new ArrayList<>();

    public Employer(String employerName) {
        this.name = employerName;
    }

    public String getName() {
        return name;
    }

    public void publishJob(Job job) {
        this.publishedJobs.add(job);
    }

    public List<Job> getPublishedJobs() {
        return publishedJobs;
    }
}
