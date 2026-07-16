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
        String codeInsee = adresse.code_insee();

        if (!codeInsee.matches("^(?:0[1-9]|[1-8]\\d|9[0-5]|2A|2B)\\d{3}$")) {
            throw new ValidationException("Code INSEE invalide");
        }

        if (!adresse.id().startsWith(codeInsee)) {
            throw new ValidationException(
                    "L'id ne commence pas par le code INSEE : " + codeInsee);
        }

    }
}
