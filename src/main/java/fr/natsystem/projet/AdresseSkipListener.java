package fr.natsystem.projet;

import org.springframework.batch.core.listener.SkipListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AdresseSkipListener implements SkipListener<HelloWorldBatchConfig.Adresse, HelloWorldBatchConfig.Adresse> {

    private final List<String> skippedIds = new ArrayList<>();

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skip lecture : {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(HelloWorldBatchConfig.Adresse adresse, Throwable t) {
        log.warn("Skip process id={}", adresse.id());
    }

    @Override
    public void onSkipInWrite(HelloWorldBatchConfig.Adresse adresse, Throwable t) {
        log.warn("Skip write id={}", adresse.id());
        skippedIds.add(adresse.id());

    }

    public List<String> getSkippedIds() {
        return skippedIds;
    }




}
