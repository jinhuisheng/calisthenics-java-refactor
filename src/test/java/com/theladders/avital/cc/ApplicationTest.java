package com.theladders.avital.cc;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationTest {
    Application application;

    private JobApplication createJobApplication(String jobName, String jobType, String employerName, String applicationTime) {
        return new JobApplication(jobName, jobType, applicationTime, employerName);
    }

    private Job createNewJob(final String jobName, final String jobType) {
        return new Job(jobName, jobType);
    }

    @Before
    public void setUp() {
        application = new Application();
    }

    @Test
    public void employers_should_be_able_to_publish_a_job() throws NotSupportedJobTypeException {
        String employerName = "";
        String jobName = "高级前端开发";
        application.publish(employerName, new Job(jobName, "JReq"));
        List<Job> jobs = application.getJobs(employerName);
        List<Job> expected = Arrays.asList(
                createNewJob("高级前端开发", "JReq"));
        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_only_be_able_to_see_jobs_published_by_them() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String employerTencent = "Tencent";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.publish(employerTencent, new Job(juniorJavaDevJob, "JReq"));
        List<Job> jobs = application.getJobs(employerAlibaba);
        List<Job> expected = Arrays.asList(
                createNewJob("高级Java开发", "JReq"));
        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_be_able_to_publish_ATS_jobs() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));

        List<Job> jobs = application.getJobs(employerAlibaba);
        List<Job> expected = Arrays.asList(
                createNewJob("高级Java开发", "ATS"));
        assertThat(jobs, is(expected));
    }

    @Test(expected = NotSupportedJobTypeException.class)
    public void employers_should_not_be_able_to_publish_jobs_that_are_neither_ATS_nor_JReq() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "RJeq"));
    }

    @Test
    public void jobseekers_should_be_able_to_save_jobs_published_by_employers_for_later_review() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String jobName = "高级Java开发";
        application.publish(employerAlibaba, new Job(jobName, "JReq"));
        application.save(jobSeekerName, new Job(jobName, "JReq"));
        List<Job> savedJobs = application.getJobSeekSavedJobs(jobSeekerName);
        List<Job> expected = Arrays.asList(createNewJob("高级Java开发", "JReq"));
        assertThat(savedJobs, is(expected));
    }

    @Test
    public void jobseekers_should_be_able_to_apply_for_an_ATS_job_some_employer_published_without_a_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.apply(jobSeekerName, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("2020-01-01"), employerAlibaba));
        application.apply(jobSeekerName, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("2020-01-01"), employerAlibaba));
        List<JobApplication> appliedJobs = application.getAppliedJobs(jobSeekerName);
        assertThat(appliedJobs, is(Arrays.asList(
                createJobApplication("Java开发", "ATS", "Alibaba", "2020-01-01"),
                createJobApplication("高级Java开发", "ATS", "Alibaba", "2020-01-01")
        )));
    }

    @Test(expected = RequiresResumeForJReqJobException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_without_a_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.apply(jobSeekerName, null, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.now(), employerAlibaba));
    }

    @Test(expected = InvalidResumeException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_with_someone_else_s_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String resumeApplicantName = "Jacky Chen";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.apply(jobSeekerName, resumeApplicantName, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.now(), employerAlibaba));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_of_a_job() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.now(), employerAlibaba));
        application.apply(jobSeekerLam, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.now(), employerAlibaba));
        List<String> applicants = application.findApplicants(seniorJavaDevJob, null, null);

        List<String> expected = new ArrayList<String>() {{
            add("Lam");
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_application_date() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        List<String> applicants = application.findApplicants(null, LocalDate.parse("1999-12-20"), null);

        List<String> expected = new ArrayList<String>() {{
            add("Ho");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period_when_period_end_is_given_while_period_start_is_not() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        List<String> applicants = application.findApplicants(null, null, LocalDate.parse("1999-01-01"));

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        List<String> applicants = application.findApplicants(null, LocalDate.parse("1997-07-01"), LocalDate.parse("1999-12-20"));

        List<String> expected = new ArrayList<String>() {{
            add("Ho");
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_start_is_given_while_period_end_is_not() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String resumeApplicantName = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.apply(jobSeekerJacky, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerJacky, resumeApplicantName, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));

        List<String> applicants = application.findApplicants(seniorJavaDevJob, LocalDate.parse("1999-12-20"), null);

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_end_is_given_while_period_start_is_not() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.apply(jobSeekerJacky, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));

        List<String> applicants = application.findApplicants(juniorJavaDevJob, null, LocalDate.parse("1999-01-01"));

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerWong = "Wong";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.apply(jobSeekerWong, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerJacky, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1998-01-01"), employerAlibaba));
        application.apply(jobSeekerLam, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));

        List<String> applicants = application.findApplicants(juniorJavaDevJob, LocalDate.parse("1997-01-01"), LocalDate.parse("1999-01-01"));

        List<String> expected = new ArrayList<String>() {{
            add("Ho");
            add("Jacky");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void should_generator_csv_reports_of_all_jobseekers_on_a_given_date() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jackyResume = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String lamResume = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.apply(jobSeekerJacky, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerJacky, jackyResume, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerLam, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerLam, lamResume, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.parse("1999-12-20"), employerAlibaba));

        String csv = application.exportCsv(LocalDate.parse("1999-12-20"));
        String expected = "Employer,Job,Job Type,Applicants,Date" + "\n" + "Alibaba,Java开发,ATS,Ho,1999-12-20" + "\n" + "Alibaba,Java开发,ATS,Lam,1999-12-20" + "\n" + "Alibaba,高级Java开发,JReq,Lam,1999-12-20" + "\n" + "Alibaba,高级Java开发,JReq,Jacky,1999-12-20" + "\n";

        assertThat(csv, is(expected));
    }

    @Test
    public void should_generator_html_reports_of_all_jobseekers_on_a_given_date() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jackyResume = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String lamResume = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.apply(jobSeekerJacky, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1997-07-01"), employerAlibaba));
        application.apply(jobSeekerJacky, jackyResume, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerLam, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.parse("1999-12-20"), employerAlibaba));
        application.apply(jobSeekerLam, lamResume, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.parse("1999-12-20"), employerAlibaba));

        String csv = application.exportHtml(LocalDate.parse("1999-12-20"));
        String expected = "<!DOCTYPE html>"
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
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>Java开发</td>"
                + "<td>ATS</td>"
                + "<td>Ho</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>Java开发</td>"
                + "<td>ATS</td>"
                + "<td>Lam</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>高级Java开发</td>"
                + "<td>JReq</td>"
                + "<td>Lam</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Alibaba</td>"
                + "<td>高级Java开发</td>"
                + "<td>JReq</td>"
                + "<td>Jacky</td>"
                + "<td>1999-12-20</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";

        assertThat(csv, is(expected));
    }

    @Test
    public void should_be_able_to_see_successful_application_of_a_job_for_an_employer() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String employerTencent = "Tencent";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "ATS"));
        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        application.publish(employerTencent, new Job(juniorJavaDevJob, "ATS"));
        application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.now(), employerAlibaba));
        application.apply(jobSeekerLam, null, new JobApplication(seniorJavaDevJob, "ATS", LocalDate.now(), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.now(), employerAlibaba));
        application.apply(jobSeekerHo, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.now(), employerTencent));

        assertThat(application.getSuccessfulApplications(employerAlibaba, seniorJavaDevJob), is(2));
        assertThat(application.getSuccessfulApplications(employerAlibaba, juniorJavaDevJob), is(1));
    }

    @Test
    public void should_be_able_to_see_unsuccessful_applications_of_a_job_for_an_employer() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(employerAlibaba, new Job(seniorJavaDevJob, "JReq"));
        application.publish(employerAlibaba, new Job(juniorJavaDevJob, "ATS"));
        try {
            application.apply(jobSeekerJacky, null, new JobApplication(seniorJavaDevJob, "JReq", LocalDate.now(), employerAlibaba));
        } catch (RequiresResumeForJReqJobException ignored) {
        }
        application.apply(jobSeekerLam, null, new JobApplication(juniorJavaDevJob, "ATS", LocalDate.now(), employerAlibaba));

        assertThat(application.getUnsuccessfulApplications(employerAlibaba, seniorJavaDevJob), is(1));
        assertThat(application.getUnsuccessfulApplications(employerAlibaba, juniorJavaDevJob), is(0));
    }
}
