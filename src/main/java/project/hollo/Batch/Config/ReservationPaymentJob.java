package project.hollo.Batch.Config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import project.hollo.Bank.Reservation.Reservation;
import project.hollo.Bank.Reservation.ReservationRepository;
import project.hollo.Bank.Transactions.TransactionRepository;
import project.hollo.Bank.UserAccount.AccountRepository;
import project.hollo.Batch.Chunk.RSVPaymentProcessor;
import project.hollo.Batch.Chunk.RSVPaymentWriter;

@Configuration
public class ReservationPaymentJob {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ReservationRepository reservationRepository;


    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean(name = "ReservationPaymentJob")
    public Job ReservationPaymentJob(
            JobRepository jobRepository,
            PlatformTransactionManager manager
    ){
        return new JobBuilder("ReservationPaymentJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(ReservationPayment(jobRepository, manager))
                .build();
    }

    @Bean
    public Step ReservationPayment(JobRepository jobRepository, PlatformTransactionManager manager){
        return new StepBuilder("ReservationPaymentStep", jobRepository)
                .<Reservation, Reservation>chunk(10, manager)
                .reader(ReservationReader())
                .processor(PaymentProcessor())
                .writer(PaymentWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Reservation> ReservationReader(){
        return new JpaPagingItemReaderBuilder<Reservation>()
                .name("ReservationReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select rv from Reservation rv order by sendAt")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Reservation, Reservation> PaymentProcessor(){
        return new RSVPaymentProcessor(
                transactionRepository,
                accountRepository,
                reservationRepository
        );
    }

    @Bean
    @StepScope
    public ItemWriter<Reservation> PaymentWriter(){
        return new RSVPaymentWriter(reservationRepository);
    }
}
