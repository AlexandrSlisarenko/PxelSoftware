package ru.slisarenko.pxelsoftware.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.quartz.*;
import ru.slisarenko.pxelsoftware.scheduler.UpdateDepositJob;
import ru.slisarenko.pxelsoftware.scheduler.UpdateStateDepositJob;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

import static ru.slisarenko.pxelsoftware.config.Constants.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {
    private final ApplicationContext applicationContext;
    private final QuartzProperties quartzProperties;
    private final AppConfigProperties appConfigProperties;

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public QuartzDataSourceScriptDatabaseInitializer quartzDataSourceInitializer(DataSource dataSource) {
        return new QuartzDataSourceScriptDatabaseInitializer(dataSource, this.quartzProperties);
    }

    @Bean(UPDATE_DEPOSIT_JOB)
    public JobDetailFactoryBean depositUpdateJob() {
        return createJobDetail(UpdateDepositJob.class, UPDATE_DEPOSIT_JOB);
    }

    @Bean(UPDATE_STATE_DEPOSIT_JOB)
    public JobDetailFactoryBean depositStateUpdateJob() {
        return createJobDetail(UpdateStateDepositJob.class, UPDATE_STATE_DEPOSIT_JOB);
    }

    @Bean(TRIGGER_UPDATE_DEPOSIT_JOB)
    public SimpleTriggerFactoryBean triggerUpdateJob(@Qualifier(UPDATE_DEPOSIT_JOB) JobDetail jobDetail) {
        var simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(jobDetail);
        simpleTriggerFactoryBean.setName(jobDetail.getKey().getName());
        simpleTriggerFactoryBean.setStartDelay(this.appConfigProperties.getStartUpdateDepositMilliseconds());
        simpleTriggerFactoryBean.setRepeatInterval(this.appConfigProperties.getIntervalUpdateDepositMilliseconds());
        return simpleTriggerFactoryBean;
    }

    @Bean(TRIGGER_UPDATE_STATE_DEPOSIT_JOB)
    public CronTriggerFactoryBean triggerUpdateStateJob(@Qualifier(UPDATE_STATE_DEPOSIT_JOB) JobDetail jobDetail) {
        return createCronTrigger(jobDetail);
    }



    @Bean
    @DependsOn("quartzDataSourceInitializer")
    public SchedulerFactoryBean mySchedulerFactoryBean(List<JobDetail> jobDetails,
                                                       List<Trigger> triggers,
                                                       DataSource dataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        var properties = new Properties();
        properties.putAll(this.quartzProperties.getProperties());
        schedulerFactory.setQuartzProperties(properties);

        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setAutoStartup(true);

        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(jobDetails.toArray(new JobDetail[0]));
        schedulerFactory.setTriggers(triggers.toArray(new Trigger[0]));
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);

        return schedulerFactory;
    }

    private <T extends Job> JobDetailFactoryBean createJobDetail(Class<T> jobClass, String jobName) {
        var jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(jobClass);
        jobDetailFactoryBean.setDescription(jobName);
        jobDetailFactoryBean.setDurability(true);
        return jobDetailFactoryBean;
    }

    private CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        cronTriggerFactoryBean.setCronExpression(this.appConfigProperties.getCron());
        cronTriggerFactoryBean.setStartDelay(this.appConfigProperties.getStartUpdateStateDepositMilliseconds());
        cronTriggerFactoryBean.setName(jobDetail.getDescription());
        return cronTriggerFactoryBean;
    }
}
