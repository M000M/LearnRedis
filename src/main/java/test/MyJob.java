package test;

import org.apache.zookeeper.data.Id;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class MyJob implements Job {
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Time + " + LocalDateTime.now(ZoneId.of("GMT")));
    }
}
