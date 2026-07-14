package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class DuplicateRulesProcessor implements ItemProcessor <HelloWorldBatchConfig.Adresse, HelloWorldBatchConfig.Adresse> {
    private final DuplicationJobListener duplicationJobListener;
    private final Set<BanKey> processed = new HashSet<>();
    private final List<String> invalidAdresse = new ArrayList<>();

    @Override
    public HelloWorldBatchConfig.@Nullable Adresse process(HelloWorldBatchConfig.Adresse item) throws Exception {

        //On ignore si le format de l'id n'est pas valid
        if(!isValidId(item.id()))
        {
            invalidAdresse.add(item.id());
            return null;
        }
        String key =   item.id()
                + "|"
                + item.type_position()
                + "|"
                + item.x()
                + "|"
                + item.y();

        if (!duplicationJobListener.getExistingKeys().add(key)) {
            return null;
        }

    if(!duplicationJobListener.getExistingKeys().add(key))
    {

        return null;
    }
        return item;
    }


    /**
     * fonction pour vérifier le format de l'id
     * à adapter selon la source en entrée!!!
     * l'id doit commencer par 79 et avoir 3 chiffre ensuite je ne gère pas les caractères
     * @param id
     * @return
     */
    private boolean isValidId(String id){
        return id != null && id.matches("^79\\d{3}.*");
    }

    public List<String> getInvalidAdresse() {
        return invalidAdresse;
    }

    public int getInvalidAdresseSize() {
        return invalidAdresse.size();
    }

    private record BanKey(String id,Double x, Double y, String type_position){}
}
