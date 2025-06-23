package ru.slisarenko.pxelsoftware.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import ru.slisarenko.pxelsoftware.scheduler.DepositJob;

import javax.sql.DataSource;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Slf4j
@Configuration
public class SchedulerConfiguration {
    private ApplicationContext applicationContext;
    public SchedulerConfiguration(ApplicationContext applicationContext, DataSource dataSource) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    @QuartzDataSource
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }

    /*@Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job, DataSource quartzDataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        schedulerFactory.setDataSource(quartzDataSource);
        return schedulerFactory;
    }*/
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(DepositJob.class)
                .storeDurably()
                .withIdentity("jDepositUpdate")
                .withDescription("Update Deposit Job")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("tDepositUpdate")
                .withDescription("Deposit Update trigger")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(30))
                .build();
    }

    @Bean
    public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory)
            throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
        return scheduler;
    }
}
