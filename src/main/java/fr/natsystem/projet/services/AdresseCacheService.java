package fr.natsystem.projet.services;

import fr.natsystem.projet.batch.mapper.AdresseRowMapper;

import fr.natsystem.projet.model.Adresse;
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
    private final Map<String, Adresse> cache = new HashMap<>();

    private final AdresseRowMapper rowMapper;
    private String currentCodeInsee;
    private final JdbcTemplate jdbcTemplate;

    public void load(String codeInsee) {

        cache.clear();

        currentCodeInsee = codeInsee;

        // charger uniquement cette commune
        List<Adresse> adresses =
                jdbcTemplate.query(
                        """
                        SELECT *
                        FROM adresse
                        WHERE code_insee = ?
                        """,
                        rowMapper,
                        codeInsee
                );


        for (Adresse adresse : adresses) {

            cache.put(
                    buildKey(adresse),
                    adresse
            );
        }
    }


    public Adresse get(String key) {
        return cache.get(key);
    }


    public void put(String key, Adresse adresse) {
        cache.put(key, adresse);
    }


    public void clear() {
        cache.clear();
    }
    private String buildKey(
            Adresse adresse) {

        return adresse.id()
                + "|"
                + adresse.type_position()
                + "|"
                + adresse.x()
                + "|"
                + adresse.y();
    }

}
