package fr.natsystem.projet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class AdresseCacheService {
    private final Map<String, HelloWorldBatchConfig.Adresse> cache = new HashMap<>();

    private final AdresseRowMapper rowMapper;
    private String currentCodeInsee;
    private final JdbcTemplate jdbcTemplate;

    public void load(String codeInsee) {

        cache.clear();

        currentCodeInsee = codeInsee;

        // charger uniquement cette commune
        List<HelloWorldBatchConfig.Adresse> adresses =
                jdbcTemplate.query(
                        """
                        SELECT *
                        FROM adresse
                        WHERE code_insee = ?
                        """,
                        rowMapper,
                        codeInsee
                );


        for (HelloWorldBatchConfig.Adresse adresse : adresses) {

            cache.put(
                    buildKey(adresse),
                    adresse
            );
        }
    }


    public HelloWorldBatchConfig.Adresse get(String key) {
        return cache.get(key);
    }


    public void put(String key, HelloWorldBatchConfig.Adresse adresse) {
        cache.put(key, adresse);
    }


    public void clear() {
        cache.clear();
    }
    private String buildKey(
            HelloWorldBatchConfig.Adresse adresse) {

        return adresse.id()
                + "|"
                + adresse.type_position()
                + "|"
                + adresse.x()
                + "|"
                + adresse.y();
    }

}
