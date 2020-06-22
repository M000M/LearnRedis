package test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws SchedulerException {

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        JobBuilder jobDetailBuilder = JobBuilder.newJob(MyJob.class);
        jobDetailBuilder.withIdentity("jobName", "jobGroupName");
        JobDetail jobDetail = jobDetailBuilder.build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("triggerName", "triggerGroupName")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?"))
                .build();

        SimpleTrigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("triggerName", "triggerGroupName")
                .startNow()
                .withSchedule(SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInSeconds(1)
                    .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger1);
        scheduler.start();
    }
}
