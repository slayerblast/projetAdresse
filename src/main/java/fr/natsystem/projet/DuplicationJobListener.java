package fr.natsystem.projet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class DuplicationJobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

}
