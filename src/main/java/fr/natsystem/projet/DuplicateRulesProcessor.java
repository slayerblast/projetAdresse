package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DuplicateRulesProcessor implements ItemProcessor<HelloWorldBatchConfig.Adresse, HelloWorldBatchConfig.Adresse> {
    private final DuplicationJobListener duplicationJobListener;
    private final BilanJobListener bilanJobListener;

    @Override
    public HelloWorldBatchConfig.@Nullable Adresse process(HelloWorldBatchConfig.Adresse item) throws Exception {

        String key = item.id()
                + "|"
                + item.type_position()
                + "|"
                + item.x()
                + "|"
                + item.y();

        HelloWorldBatchConfig.Adresse existing = duplicationJobListener.getExistingAdresses().get(key);
        duplicationJobListener.getCsvKeys().add(key);
        if (existing == null) {
            duplicationJobListener.getExistingAdresses().put(key, item);
            return item; // insert
        } else if (existing.equals(item)) {
            bilanJobListener.setDoublonPur(bilanJobListener.getDoublonPur() + 1);
            return null; // doublon pur
        } else if(item.isBetterThan(existing)) {
            duplicationJobListener.getExistingAdresses().put(key, item);
            bilanJobListener.setDoublon(bilanJobListener.getDoublon() + 1);
            return item; //doublon partiel
        }
        else {
            bilanJobListener.setDoublon(bilanJobListener.getDoublon() + 1);
            return null; //doublon partiel mais rejeté
        }
    }

}
