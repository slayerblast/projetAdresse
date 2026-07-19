package fr.natsystem.projet.batch.processor;

import fr.natsystem.projet.batch.listener.BilanJobListener;
import fr.natsystem.projet.model.Adresse;
import fr.natsystem.projet.services.AdresseCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DuplicateRulesProcessor
        implements ItemProcessor<Adresse, Adresse> {

    private final AdresseCacheService adresseCacheService;
    private final BilanJobListener bilanJobListener;
    
    @Override
    public @Nullable Adresse process(
            Adresse item) {

        // Si on change de commune, on recharge le cache
        if (!item.code_insee().equals(adresseCacheService.getCurrentCodeInsee())) {
            adresseCacheService.load(item.code_insee());
        }

        String key = buildKey(item);

        Adresse existing = adresseCacheService.get(key);

        if (existing == null) {
            adresseCacheService.put(key, item);
            return item; // nouvelle adresse

        } else if (existing.equals(item)) {
            bilanJobListener.setDoublonPur(
                    bilanJobListener.getDoublonPur() + 1);
            return null; // doublon exact

        } else if (item.isBetterThan(existing)) {
            adresseCacheService.put(key, item);
            bilanJobListener.setDoublon(bilanJobListener.getDoublon() + 1);
            return item; // meilleure version
        } else {
            bilanJobListener.setDoublon(bilanJobListener.getDoublon() + 1);
            return null;
        }
    }

    private String buildKey( Adresse adresse) {

        return adresse.id()
                + "|"
                + adresse.type_position()
                + "|"
                + adresse.x()
                + "|"
                + adresse.y();
    }
}
