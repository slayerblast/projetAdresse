package fr.natsystem.projet;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BilanJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution je) {
        log.info("Job [{}] demarre",
                je.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution je) {
        log.info("Job {} : {}", je.getJobInstance()
                .getJobName(), je.getStatus());
    }

}



