package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuppressionObsoleteTasklet implements Tasklet {

    private final DuplicationJobListener duplicationJobListener;
    private final BilanJobListener bilanJobListener;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext) {
        log.info("adresse en base: {}",duplicationJobListener.getDbAdresses().size());
        log.info("adresse en base csv: {}",duplicationJobListener.getCsvKeys().size());
        Set<String> obsoleteKeys =
                new HashSet<>(
                        duplicationJobListener
                                .getDbAdresses()
                                .keySet());
        obsoleteKeys.removeAll(
                duplicationJobListener.getCsvKeys());
        log.info("adresse en base obsolete : {}",obsoleteKeys.size());
        for (String key : obsoleteKeys) {

            HelloWorldBatchConfig.Adresse adresse =
                    duplicationJobListener
                            .getDbAdresses()
                            .get(key);

            jdbcTemplate.update(
                    """
                    DELETE FROM adresse
                    WHERE id = ?
                      AND type_position = ?
                      AND x = ?
                      AND y = ?
                    """,
                    adresse.id(),
                    adresse.type_position(),
                    adresse.x(),
                    adresse.y());
            bilanJobListener.setObsolete(bilanJobListener.getObsolete()+1);
        }

        return RepeatStatus.FINISHED;
    }
}