package fr.natsystem.projet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.infrastructure.item.validator.Validator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdresseValidator
        implements Validator<HelloWorldBatchConfig.Adresse> {
    /**
     * à adapter selon le critère choisi !!!
     * Utilisé pour vérifier le format de l'id
     * l'id doit commencer par 79 et avoir 3 chiffre ensuite je ne gère pas les caractères
     * @param adresse object to be validated
     * @throws ValidationException
     */
    @Override
    public void validate(HelloWorldBatchConfig.Adresse adresse)
            throws ValidationException {
        if(!adresse.id().matches("^79\\d{3}.*"))
        {
            throw new ValidationException("Erreur de format dans l'id");
        }

    }
}
