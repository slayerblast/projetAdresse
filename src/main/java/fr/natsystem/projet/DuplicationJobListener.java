package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DuplicationJobListener implements JobExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    private final Set<String> existingKeys = new HashSet<>();

    public Set<String> getExistingKeys() {
        return existingKeys;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {

        existingKeys.clear();

        existingKeys.addAll(
                jdbcTemplate.query(
                        """
                        SELECT id, type_position, x, y
                        FROM adresse
                        """,
                        (rs, rowNum) -> buildKey(
                                rs.getString("id"),
                                rs.getString("type_position"),
                                rs.getDouble("x"),
                                rs.getDouble("y")
                        )
                )
        );

        log.info("{} clés chargées pour la déduplication", existingKeys.size());
    }

    private String buildKey(
            String id,
            String typePosition,
            Double x,
            Double y) {

        return id + "|" + typePosition + "|" + x + "|" + y;
    }
}
