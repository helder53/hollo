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
import project.hollo.Batch.Chunk.StockDetailProcessor;
import project.hollo.Batch.Chunk.StockDetailWriter;
import project.hollo.Batch.Chunk.StockPriceProcessor;
import project.hollo.Batch.Chunk.StockPriceWriter;
import project.hollo.Stock.Stock;
import project.hollo.Stock.StockDetail.StockDetail;
import project.hollo.Stock.StockDetail.StockDetailRepository;
import project.hollo.Stock.StockRepository;

@Configuration
public class StockJob {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockDetailRepository detailRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Bean(name = "StockJob")
    public Job StockJob(JobRepository jobRepository, PlatformTransactionManager manager){
        return new JobBuilder("StockJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(StockPriceStep(jobRepository, manager))
                .next(StockDetailStep(jobRepository, manager))
                .build();
    }

    // Step
    @Bean
    public Step StockPriceStep(JobRepository jobRepository, PlatformTransactionManager manager){
        return new StepBuilder("StockPriceChangeStep", jobRepository)
                .<Stock, Stock>chunk(10, manager)
                .reader(StockReader())
                .processor(StockPriceChangeProcessor())
                .writer(StockWriter())
                .build();
    }

    @Bean
    public Step StockDetailStep(JobRepository jobRepository, PlatformTransactionManager manager){
        return new StepBuilder("StockDetailSaveStep", jobRepository)
                .<Stock, StockDetail>chunk(10, manager)
                .reader(StockReader())
                .processor(InsertStockDetail())
                .writer(StockDetailSaveWriter())
                .build();
    }


    // Stock
    @Bean
    @StepScope
    public JpaPagingItemReader<Stock> StockReader(){
        return new JpaPagingItemReaderBuilder<Stock>()
                .name("StockReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select st from Stock st")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Stock, Stock> StockPriceChangeProcessor(){
        return new StockPriceProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<Stock> StockWriter(){
        return new StockPriceWriter(stockRepository);
    }


    // StockDetail
    @Bean
    @StepScope
    public ItemProcessor<Stock, StockDetail> InsertStockDetail(){
        return new StockDetailProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<StockDetail> StockDetailSaveWriter(){
        return new StockDetailWriter(detailRepository);
    }
}
