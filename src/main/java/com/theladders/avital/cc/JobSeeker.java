package com.theladders.avital.cc;

import com.google.common.base.Objects;

public class JobSeeker {
    private final String name;

    public JobSeeker(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "JobSeeker{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobSeeker jobSeeker = (JobSeeker) o;
        return Objects.equal(name, jobSeeker.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
