package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BilanJobListener implements JobExecutionListener {
    private final DuplicateRulesProcessor drProcessor;
    private final AdresseSkipListener skipListener;

    @Override
    public void beforeJob(JobExecution je) {
        log.info("Job [{}] demarre",
                je.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution je) {
        log.info("Job {} : {}", je.getJobInstance()
                .getJobName(), je.getStatus());

        StepExecution importSet = je.getStepExecutions().stream()
                .filter(s-> s.getStepName().equals("importAdresseStep"))
                .findFirst().orElse(null);
        if(importSet!=null){
            long doublons = importSet.getReadCount() - importSet.getWriteCount() - skipListener.getRejetesIds().size();
            try (FileWriter writer = new FileWriter("src/main/resources/bilan/bilan.txt")) {

                writer.write("=== BILAN IMPORT ===\n");
                writer.write("ReadCount  : " + importSet.getReadCount() + "\n");
                writer.write("WriteCount : " + importSet.getWriteCount() + "\n");
                writer.write("Ligne en double : " + doublons + "\n");

                writer.write("SkipCount  : " + importSet.getSkipCount() + "\n");

                writer.write("\nIds rejeté :\n");
                for (String id : skipListener.getRejetesIds()) {
                    writer.write(id + "\n");
                }

                writer.write("\nIds skippés :\n");
                for (String id : skipListener.getSkippedIds()) {
                    writer.write(id + "\n");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}



