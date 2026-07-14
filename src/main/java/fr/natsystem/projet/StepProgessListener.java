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
    private final AdresseSkipListener skipListener;
    private final DuplicateRulesProcessor drProcessor;


    @Override
    public void beforeStep(StepExecution s) {
        log.info("Step [{}] demarre", s.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution s) {
        /*
        long doublons = s.getReadCount() - s.getWriteCount() - drProcessor.getInvalidAdresseSize();

        //
        try (FileWriter writer = new FileWriter("src/main/resources/bilan/bilan.txt")) {

            writer.write("=== BILAN IMPORT ===\n");
            writer.write("ReadCount  : " + s.getReadCount() + "\n");
            writer.write("WriteCount : " + s.getWriteCount() + "\n");
            writer.write("Ligne en double : " + doublons + "\n");

            writer.write("SkipCount  : " + s.getSkipCount() + "\n");

            writer.write("\nIds rejeté :\n");
            for (String id : drProcessor.getInvalidAdresse()) {
                writer.write(id + "\n");
            }

            writer.write("\nIds skippés :\n");
            for (String id : skipListener.getSkippedIds()) {
                writer.write(id + "\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    */
        return s.getExitStatus();
    }

}
