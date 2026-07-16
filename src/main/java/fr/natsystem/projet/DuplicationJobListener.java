package fr.natsystem.projet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class DuplicationJobListener implements JobExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
    }

    private final Set<String> csvKeys = new HashSet<>();
    private final Map<String, HelloWorldBatchConfig.Adresse> existingAdresses = new HashMap<>();
    private final Map<String, HelloWorldBatchConfig.Adresse> dbAdresses = new HashMap<>();

    @Override
    public void beforeJob(JobExecution jobExecution) {
        csvKeys.clear();
        existingAdresses.clear();
        jdbcTemplate.query(
                """
                SELECT *
                FROM adresse
                """,
                rs -> {
                    HelloWorldBatchConfig.Adresse adresse = new HelloWorldBatchConfig.Adresse(
                            rs.getString("id"),
                            rs.getString("id_fantoir"),
                            rs.getString("numero"),
                            rs.getString("rep"),
                            rs.getString("nom_voie"),
                            rs.getString("code_postal"),
                            rs.getString("code_insee"),
                            rs.getString("nom_commune"),
                            rs.getString("code_insee_ancienne_commune"),
                            rs.getString("nom_ancienne_commune"),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("lon"),
                            rs.getDouble("lat"),
                            rs.getString("type_position"),
                            rs.getString("alias"),
                            rs.getString("nom_ld"),
                            rs.getString("libelle_acheminement"),
                            rs.getString("nom_afnor"),
                            rs.getString("source_position"),
                            rs.getString("source_nom_voie"),
                            rs.getInt("certification_commune"),
                            rs.getString("cad_parcelles")
                    );

                    existingAdresses.put(
                            buildKey(
                                    adresse.id(),
                                    adresse.type_position(),
                                    adresse.x(),
                                    adresse.y()
                            ),
                            adresse
                    );
                    dbAdresses.put(
                            buildKey(
                                    adresse.id(),
                                    adresse.type_position(),
                                    adresse.x(),
                                    adresse.y()
                            ),
                            adresse
                    );

                });

        log.info("{} clés chargées pour la déduplication", existingAdresses.size());
    }

    private String buildKey(
            String id,
            String typePosition,
            Double x,
            Double y) {

        return id + "|" + typePosition + "|" + x + "|" + y;
    }
}
