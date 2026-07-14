package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DuplicateRulesProcessor implements ItemProcessor <HelloWorldBatchConfig.Adresse, HelloWorldBatchConfig.Adresse> {
    private final DuplicationJobListener duplicationJobListener;

    @Override
    public HelloWorldBatchConfig.@Nullable Adresse process(HelloWorldBatchConfig.Adresse item) throws Exception {

        String key =   item.id()
                + "|"
                + item.type_position()
                + "|"
                + item.x()
                + "|"
                + item.y();

    if(!duplicationJobListener.getExistingKeys().add(key))
    {

        return null;
    }
        return item;
    }

    private record BanKey(String id,Double x, Double y, String type_position){}
}
