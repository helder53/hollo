package project.hollo.Batch.Schedule;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class Schedule {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("StockJob")
    private Job stockJob;

    @Autowired
    @Qualifier("ReservationPaymentJob")
    private Job reservationPaymentJob;

    // 평일, 아침 9시부터 저녁 6시까지 1시간마다 실행
    @Scheduled(cron = "0 0 9-18 * * MON-FRI")
    public void ScheduledJobRunner() throws Exception{
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", "StockJob_" + System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(stockJob, params);
    }

    // 평일, 1분마다 실행
    @Scheduled(cron = "0 0/1 9-18 * * MON-FRI")
    public void ReservationJobRunner() throws Exception{
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", "ReservationPaymentJob_" + System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(reservationPaymentJob, params);
    }

}
