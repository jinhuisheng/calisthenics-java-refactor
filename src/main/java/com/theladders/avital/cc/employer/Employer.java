package com.theladders.avital.cc.employer;

import com.google.common.base.Objects;

public class Employer {
    private final String name;

    public Employer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Employer{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return Objects.equal(name, employer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
