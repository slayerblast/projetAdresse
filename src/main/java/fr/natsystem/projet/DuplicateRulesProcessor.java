package fr.natsystem.projet;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.HashSet;
import java.util.Set;

public class DuplicateRulesProcessor implements ItemProcessor <HelloWorldBatchConfig.Adresse, HelloWorldBatchConfig.Adresse> {

   private final Set<BanKey> processed = new HashSet<>();

    @Override
    public HelloWorldBatchConfig.@Nullable Adresse process(HelloWorldBatchConfig.Adresse item) throws Exception {

        //On ignore si le format de l'id n'est pas valid
        if(!isValidId(item.id()))
        {
            return null;
        }
    BanKey key = new BanKey(
            item.id(),
            item.x(),
            item.y(),
            item.lon(),
            item.lat(),
            item.type_position()
    );
    if(!processed.add(key))
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

    private record BanKey(String id,Double x, Double y, Double lon, Double lat, String type_position){}
}
