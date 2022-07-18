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

    @Before
    public void setUp() {
        application = new Application();
    }

    @Test
    public void employers_should_be_able_to_publish_a_job() throws NotSupportedJobTypeException {
        String employerName = "";
        String jobName = "高级前端开发";
        application.publish(new Employer(employerName), new Job(jobName, JobType.JReq));
        List<Job> jobs = application.getJobs(new Employer(employerName));
        List<Job> expected = Arrays.asList(
                new Job("高级前端开发", JobType.JReq));
        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_only_be_able_to_see_jobs_published_by_them() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String employerTencent = "Tencent";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";
        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.publish(new Employer(employerTencent), new Job(juniorJavaDevJob, JobType.JReq));
        List<Job> jobs = application.getJobs(new Employer(employerAlibaba));
        List<Job> expected = Arrays.asList(
                new Job("高级Java开发", JobType.JReq));
        assertThat(jobs, is(expected));
    }

    @Test
    public void employers_should_be_able_to_publish_ATS_jobs() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));

        List<Job> jobs = application.getJobs(new Employer(employerAlibaba));
        List<Job> expected = Arrays.asList(
                new Job("高级Java开发", JobType.ATS));
        assertThat(jobs, is(expected));
    }

    @Test(expected = NotSupportedJobTypeException.class)
    public void employers_should_not_be_able_to_publish_jobs_that_are_neither_ATS_nor_JReq() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.RJeq));
    }

    @Test
    public void jobseekers_should_be_able_to_save_jobs_published_by_employers_for_later_review() throws NotSupportedJobTypeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String jobName = "高级Java开发";
        application.publish(new Employer(employerAlibaba), new Job(jobName, JobType.JReq));
        application.save(new JobSeeker(jobSeekerName), new Job(jobName, JobType.JReq));
        List<Job> savedJobs = application.getJobSeekSavedJobs(new JobSeeker(jobSeekerName));
        List<Job> expected = Arrays.asList(new Job("高级Java开发", JobType.JReq));
        assertThat(savedJobs, is(expected));
    }

    @Test
    public void jobseekers_should_be_able_to_apply_for_an_ATS_job_some_employer_published_without_a_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String juniorJavaDevJob = "Java开发";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(juniorJavaDevJob, LocalDate.parse("2020-01-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerName), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("2020-01-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerName), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        List<AppliedJobApplication> appliedJobs = application.getAppliedJobs(jobSeekerName);
        assertThat(appliedJobs, is(Arrays.asList(
                new AppliedJobApplication(LocalDate.parse("2020-01-01"), new JobSeeker(jobSeekerName), new PublishedJob(new Job("Java开发", JobType.ATS), new Employer("Alibaba"))),
                new AppliedJobApplication(LocalDate.parse("2020-01-01"), new JobSeeker(jobSeekerName), new PublishedJob(new Job("高级Java开发", JobType.ATS), new Employer("Alibaba")))
        )));
    }

    @Test(expected = RequiresResumeForJReqJobException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_without_a_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.JReq);
        application.apply(null, new JobSeeker(jobSeekerName), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
    }

    @Test(expected = InvalidResumeException.class)
    public void jobseekers_should_not_be_able_to_apply_for_an_JReq_job_some_employer_published_with_someone_else_s_resume() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerName = "Jacky";
        String seniorJavaDevJob = "高级Java开发";
        String resumeApplicantName = "Jacky Chen";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.JReq);
        application.apply(resumeApplicantName, new JobSeeker(jobSeekerName), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
    }

    @Test
    public void employers_should_be_able_to_find_applicants_of_a_job() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerLam = "Lam";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerLam), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        List<String> applicants = application.findApplicants(seniorJavaDevJob, null, null);

        List<String> expected = new ArrayList<String>() {{
            add("Jacky");
            add("Lam");
        }};

        assertThat(applicants, is(expected));
    }

    @Test
    public void employers_should_be_able_to_find_applicants_to_a_job_by_application_date() throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        String employerAlibaba = "Alibaba";
        String jobSeekerJacky = "Jacky";
        String jobSeekerHo = "Ho";
        String seniorJavaDevJob = "高级Java开发";

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
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

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
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

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
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

        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        final JobApplication jobApplication = new JobApplication(juniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.JReq);
        application.apply(resumeApplicantName, new JobSeeker(jobSeekerJacky), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        final JobApplication jobApplication2 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication2.getJobName(), jobApplication2.getJobType()), new Employer(jobApplication2.getEmployerName()), jobApplication2.getApplicationTime());

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

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(juniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        final JobApplication jobApplication2 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication2.getJobName(), jobApplication2.getJobType()), new Employer(jobApplication2.getEmployerName()), jobApplication2.getApplicationTime());

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

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerWong), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        final JobApplication jobApplication2 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1998-01-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication2.getJobName(), jobApplication2.getJobType()), new Employer(jobApplication2.getEmployerName()), jobApplication2.getApplicationTime());
        final JobApplication jobApplication3 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerLam), new Job(jobApplication3.getJobName(), jobApplication3.getJobType()), new Employer(jobApplication3.getEmployerName()), jobApplication3.getApplicationTime());

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

        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        final JobApplication jobApplication = new JobApplication(juniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.JReq);
        application.apply(jackyResume, new JobSeeker(jobSeekerJacky), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        final JobApplication jobApplication2 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication2.getJobName(), jobApplication2.getJobType()), new Employer(jobApplication2.getEmployerName()), jobApplication2.getApplicationTime());
        final JobApplication jobApplication3 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerLam), new Job(jobApplication3.getJobName(), jobApplication3.getJobType()), new Employer(jobApplication3.getEmployerName()), jobApplication3.getApplicationTime());
        final JobApplication jobApplication4 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.JReq);
        application.apply(lamResume, new JobSeeker(jobSeekerLam), new Job(jobApplication4.getJobName(), jobApplication4.getJobType()), new Employer(jobApplication4.getEmployerName()), jobApplication4.getApplicationTime());

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

        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        final JobApplication jobApplication = new JobApplication(juniorJavaDevJob, LocalDate.parse("1997-07-01"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.JReq);
        application.apply(jackyResume, new JobSeeker(jobSeekerJacky), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        final JobApplication jobApplication2 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication2.getJobName(), jobApplication2.getJobType()), new Employer(jobApplication2.getEmployerName()), jobApplication2.getApplicationTime());
        final JobApplication jobApplication3 = new JobApplication(juniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerLam), new Job(jobApplication3.getJobName(), jobApplication3.getJobType()), new Employer(jobApplication3.getEmployerName()), jobApplication3.getApplicationTime());
        final JobApplication jobApplication4 = new JobApplication(seniorJavaDevJob, LocalDate.parse("1999-12-20"), employerAlibaba, JobType.JReq);
        application.apply(lamResume, new JobSeeker(jobSeekerLam), new Job(jobApplication4.getJobName(), jobApplication4.getJobType()), new Employer(jobApplication4.getEmployerName()), jobApplication4.getApplicationTime());

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

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        application.publish(new Employer(employerTencent), new Job(juniorJavaDevJob, JobType.ATS));
        final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        final JobApplication jobApplication1 = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerLam), new Job(jobApplication1.getJobName(), jobApplication1.getJobType()), new Employer(jobApplication1.getEmployerName()), jobApplication1.getApplicationTime());
        final JobApplication jobApplication2 = new JobApplication(juniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication2.getJobName(), jobApplication2.getJobType()), new Employer(jobApplication2.getEmployerName()), jobApplication2.getApplicationTime());
        final JobApplication jobApplication3 = new JobApplication(juniorJavaDevJob, LocalDate.now(), employerTencent, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerHo), new Job(jobApplication3.getJobName(), jobApplication3.getJobType()), new Employer(jobApplication3.getEmployerName()), jobApplication3.getApplicationTime());

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

        application.publish(new Employer(employerAlibaba), new Job(seniorJavaDevJob, JobType.JReq));
        application.publish(new Employer(employerAlibaba), new Job(juniorJavaDevJob, JobType.ATS));
        try {
            final JobApplication jobApplication = new JobApplication(seniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.JReq);
            application.apply(null, new JobSeeker(jobSeekerJacky), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());
        } catch (RequiresResumeForJReqJobException ignored) {
        }
        final JobApplication jobApplication = new JobApplication(juniorJavaDevJob, LocalDate.now(), employerAlibaba, JobType.ATS);
        application.apply(null, new JobSeeker(jobSeekerLam), new Job(jobApplication.getJobName(), jobApplication.getJobType()), new Employer(jobApplication.getEmployerName()), jobApplication.getApplicationTime());

        assertThat(application.getUnsuccessfulApplications(employerAlibaba, seniorJavaDevJob), is(1));
        assertThat(application.getUnsuccessfulApplications(employerAlibaba, juniorJavaDevJob), is(0));
    }
}
