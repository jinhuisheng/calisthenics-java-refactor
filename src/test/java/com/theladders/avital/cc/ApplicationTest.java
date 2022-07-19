package com.theladders.avital.cc;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationTest {
    Application application;

    @Before
    public void setUp() {
        application = new Application();
    }

    @Test
    public void employers_should_be_able_to_publish_a_job() throws NotSupportedJobTypeException {
        String employerName = "";
        String jobName = "高级前端开发";
        Employer employer = new Employer(employerName);
        Job job = new Job(jobName, JobType.JReq);

        application.publish(employer, job);
        List<Job> jobs = application.getJobs(employer);
        assertThat(jobs, is(Arrays.asList(job)));
    }

    @Test
    public void employers_should_only_be_able_to_see_jobs_published_by_them() throws NotSupportedJobTypeException {
        Employer alibaba = new Employer("Alibaba");
        Job seniorJob = new Job("高级Java开发", JobType.JReq);
        Employer tencent = new Employer("Tencent");
        Job juniorJob = new Job("Java开发", JobType.JReq);

        application.publish(alibaba, seniorJob);
        application.publish(tencent, juniorJob);
        List<Job> jobs = application.getJobs(alibaba);
        assertThat(jobs, is(Arrays.asList(seniorJob)));
    }

    @Test
    public void employers_should_be_able_to_publish_ATS_jobs() throws NotSupportedJobTypeException {
        Employer alibaba = new Employer("Alibaba");
        Job job = new Job("高级Java开发", JobType.ATS);
        application.publish(alibaba, job);
        List<Job> jobs = application.getJobs(alibaba);

        assertThat(jobs, is(Arrays.asList(job)));
    }

    @Test(expected = NotSupportedJobTypeException.class)
    public void employers_should_not_be_able_to_publish_jobs_that_are_neither_ATS_nor_JReq() throws NotSupportedJobTypeException {
        Employer alibaba = new Employer("Alibaba");
        Job job = new Job("高级Java开发", JobType.RJeq);
        application.publish(alibaba, job);
    }

    @Test
    public void jobseekers_should_be_able_to_save_jobs_published_by_employers_for_later_review() throws NotSupportedJobTypeException {
        JobSeeker jobSeeker = new JobSeeker("Jacky");
        Job job = new Job("高级Java开发", JobType.JReq);
        Employer alibaba = new Employer("Alibaba");
        application.publish(alibaba, job);
        application.save(jobSeeker, job);
        List<Job> savedJobs = application.getJobSeekSavedJobs(jobSeeker);
        assertThat(savedJobs, is(Arrays.asList(job)));
    }

    @Test
    public void jobseekers_should_be_able_to_apply_for_an_ATS_job_some_employer_published_without_a_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String jobSeekerName = "Jacky";

        Employer alibaba = new Employer("Alibaba");
        JobSeeker jobSeeker = new JobSeeker(jobSeekerName);
        Job seniorJob = new Job("高级Java开发", JobType.ATS);
        Job juniorJob = new Job("Java开发", JobType.ATS);
        LocalDate appliedTime = LocalDate.parse("2020-01-01");

        application.publish(alibaba, seniorJob);
        application.publish(alibaba, juniorJob);
        application.apply(alibaba, juniorJob, jobSeeker, appliedTime, null);
        application.apply(alibaba, seniorJob, jobSeeker, appliedTime, null);
        List<AppliedJobApplication> appliedJobs = application.getAppliedJobs(jobSeeker);
        assertThat(appliedJobs, is(Arrays.asList(
                new AppliedJobApplication(appliedTime, jobSeeker, new PublishedJob(juniorJob, alibaba)),
                new AppliedJobApplication(appliedTime, jobSeeker, new PublishedJob(seniorJob, alibaba))
        )));
    }

    @Test(expected = RequiresResumeForJReqJobException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_without_a_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        Job seniorJob = new Job("高级Java开发", JobType.JReq);
        Employer alibaba = new Employer("Alibaba");
        JobSeeker jobSeeker = new JobSeeker("Jacky");

        application.publish(alibaba, seniorJob);
        application.apply(alibaba, seniorJob, jobSeeker, LocalDate.now(), null);
    }

    @Test(expected = InvalidResumeException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_with_someone_else_s_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String resumeApplicantName = "Jacky Chen";

        Employer alibaba = new Employer("Alibaba");
        JobSeeker jobSeeker = new JobSeeker("Jacky");
        Job job = new Job("高级Java开发", JobType.JReq);

        application.publish(alibaba, job);
        application.apply(alibaba, job, jobSeeker, LocalDate.now(), resumeApplicantName);
    }

    @Test
    public void employers_should_be_able_to_find_applicants_of_a_job() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker lam = new JobSeeker(jobSeekerLam);
        Job job = new Job(seniorJavaDevJob, JobType.ATS);
        LocalDate applicationTime = LocalDate.now();

        application.publish(alibaba, job);
        application.apply(alibaba, job, jacky, applicationTime, null);
        application.apply(alibaba, job, lam, applicationTime, null);
        List<String> applicants = application.findApplicants(seniorJavaDevJob, null, null);

        assertThat(applicants, is(Arrays.asList("Jacky", "Lam")));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_application_date() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        Job job = new Job(seniorJavaDevJob, JobType.ATS);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);

        application.publish(alibaba, job);
        application.apply(alibaba, job, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, job, ho, LocalDate.parse("1999-12-20"), null);

        List<String> applicants = application.findApplicants(null, LocalDate.parse("1999-12-20"), null);

        assertThat(applicants, is(Arrays.asList("Ho")));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period_when_period_end_is_given_while_period_start_is_not() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        Employer employer = new Employer(employerAlibaba);
        Job job = new Job(seniorJavaDevJob, JobType.ATS);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        Employer alibaba = new Employer(employerAlibaba);

        application.publish(employer, job);
        application.apply(alibaba, job, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, job, ho, LocalDate.parse("1999-12-20"), null);
        List<String> applicants = application.findApplicants(null, null, LocalDate.parse("1999-01-01"));

        assertThat(applicants, is(Arrays.asList("Jacky")));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_period() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        Job job = new Job(seniorJavaDevJob, JobType.ATS);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publish(alibaba, job);
        application.apply(alibaba, job, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, job, ho, LocalDate.parse("1999-12-20"), null);
        List<String> applicants = application.findApplicants(null, LocalDate.parse("1997-07-01"), LocalDate.parse("1999-12-20"));

        assertThat(applicants, is(Arrays.asList("Ho", "Jacky")));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_start_is_given_while_period_end_is_not() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String resumeApplicantName = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        Job astJob = new Job(juniorJavaDevJob, JobType.ATS);
        Job jReqJob = new Job(seniorJavaDevJob, JobType.JReq);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publish(alibaba, astJob);
        application.publish(alibaba, jReqJob);
        application.apply(alibaba, astJob, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, jReqJob, jacky, LocalDate.parse("1999-12-20"), resumeApplicantName);
        application.apply(alibaba, astJob, ho, LocalDate.parse("1999-12-20"), null);

        List<String> applicants = application.findApplicants(seniorJavaDevJob, LocalDate.parse("1999-12-20"), null);
        assertThat(applicants, is(Arrays.asList("Jacky")));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_job_name_and_period_when_period_end_is_given_while_period_start_is_not() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        Job seniorJob = new Job(seniorJavaDevJob, JobType.ATS);
        Job juniorJob = new Job(juniorJavaDevJob, JobType.ATS);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);

        application.publish(alibaba, seniorJob);
        application.publish(alibaba, juniorJob);
        application.apply(alibaba, juniorJob, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, seniorJob, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, juniorJob, ho, LocalDate.parse("1999-12-20"), null);

        List<String> applicants = application.findApplicants(juniorJavaDevJob, null, LocalDate.parse("1999-01-01"));
        assertThat(applicants, is(Arrays.asList("Jacky")));
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

        Employer alibaba = new Employer(employerAlibaba);
        Job seniorJob = new Job(seniorJavaDevJob, JobType.ATS);
        Job juniorJob = new Job(juniorJavaDevJob, JobType.ATS);
        JobSeeker wong = new JobSeeker(jobSeekerWong);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publish(alibaba, seniorJob);
        application.publish(alibaba, juniorJob);
        application.apply(alibaba, seniorJob, wong, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, juniorJob, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, juniorJob, ho, LocalDate.parse("1998-01-01"), null);
        application.apply(alibaba, juniorJob, lam, LocalDate.parse("1999-12-20"), null);

        List<String> applicants = application.findApplicants(juniorJavaDevJob, LocalDate.parse("1997-01-01"), LocalDate.parse("1999-01-01"));
        assertThat(applicants, is(Arrays.asList("Ho", "Jacky")));
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

        Employer alibaba = new Employer(employerAlibaba);
        Job juniorJob = new Job(juniorJavaDevJob, JobType.ATS);
        Job seniorJob = new Job(seniorJavaDevJob, JobType.JReq);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publish(alibaba, juniorJob);
        application.publish(alibaba, seniorJob);
        application.apply(alibaba, juniorJob, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, seniorJob, jacky, LocalDate.parse("1999-12-20"), jackyResume);
        application.apply(alibaba, juniorJob, ho, LocalDate.parse("1999-12-20"), null);
        application.apply(alibaba, juniorJob, lam, LocalDate.parse("1999-12-20"), null);
        application.apply(alibaba, seniorJob, lam, LocalDate.parse("1999-12-20"), lamResume);

        String csv = application.export(LocalDate.parse("1999-12-20"), ExportType.csv);
        String expected = "Employer,Job,Job Type,Applicants,Date" + "\n" +
                "Alibaba,Java开发,ATS,Ho,1999-12-20" + "\n" +
                "Alibaba,高级Java开发,JReq,Jacky,1999-12-20" + "\n" +
                "Alibaba,Java开发,ATS,Lam,1999-12-20" + "\n" +
                "Alibaba,高级Java开发,JReq,Lam,1999-12-20" + "\n";
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

        Employer alibaba = new Employer(employerAlibaba);
        Job juniorJob = new Job(juniorJavaDevJob, JobType.ATS);
        Job seniorJob = new Job(seniorJavaDevJob, JobType.JReq);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publish(alibaba, juniorJob);
        application.publish(alibaba, seniorJob);
        application.apply(alibaba, juniorJob, jacky, LocalDate.parse("1997-07-01"), null);
        application.apply(alibaba, seniorJob, jacky, LocalDate.parse("1999-12-20"), jackyResume);
        application.apply(alibaba, juniorJob, ho, LocalDate.parse("1999-12-20"), null);
        application.apply(alibaba, juniorJob, lam, LocalDate.parse("1999-12-20"), null);
        application.apply(alibaba, seniorJob, lam, LocalDate.parse("1999-12-20"), lamResume);

        String csv = application.export(LocalDate.parse("1999-12-20"), ExportType.html);
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
                + "<td>高级Java开发</td>"
                + "<td>JReq</td>"
                + "<td>Jacky</td>"
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

        Employer alibaba = new Employer(employerAlibaba);
        Employer tecent = new Employer(employerTencent);
        Job seniorJob = new Job(seniorJavaDevJob, JobType.ATS);
        Job juniorJob = new Job(juniorJavaDevJob, JobType.ATS);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker lam = new JobSeeker(jobSeekerLam);
        JobSeeker ho = new JobSeeker(jobSeekerHo);
        LocalDate applicationTime = LocalDate.now();

        application.publish(alibaba, seniorJob);
        application.publish(alibaba, juniorJob);
        application.publish(tecent, juniorJob);
        application.apply(alibaba, seniorJob, jacky, applicationTime, null);
        application.apply(alibaba, seniorJob, lam, applicationTime, null);
        application.apply(alibaba, juniorJob, ho, applicationTime, null);
        application.apply(tecent, juniorJob, ho, applicationTime, null);

        assertThat(application.getSuccessfulApplications(alibaba, seniorJavaDevJob), is(2));
        assertThat(application.getSuccessfulApplications(alibaba, juniorJavaDevJob), is(1));
    }

    @Test
    public void should_be_able_to_see_unsuccessful_applications_of_a_job_for_an_employer() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        Employer alibaba = new Employer(employerAlibaba);
        Job seniorJob = new Job(seniorJavaDevJob, JobType.JReq);
        Job juniorJob = new Job(juniorJavaDevJob, JobType.ATS);
        JobSeeker jacky = new JobSeeker(jobSeekerJacky);
        JobSeeker lam = new JobSeeker(jobSeekerLam);

        application.publish(alibaba, seniorJob);
        application.publish(alibaba, juniorJob);
        LocalDate applicationTime = LocalDate.now();
        try {
            application.apply(alibaba, seniorJob, jacky, applicationTime, null);
        } catch (RequiresResumeForJReqJobException ignored) {
        }
        application.apply(alibaba, juniorJob, lam, applicationTime, null);

        assertThat(application.getUnsuccessfulApplications(employerAlibaba, seniorJavaDevJob), is(1));
        assertThat(application.getUnsuccessfulApplications(employerAlibaba, juniorJavaDevJob), is(0));
    }
}
