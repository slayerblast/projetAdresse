package fr.natsystem.projet;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class StepProgessListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution s) {
        log.info("Step [{}] demarre", s.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution s) {

        return s.getExitStatus();
    }

}
