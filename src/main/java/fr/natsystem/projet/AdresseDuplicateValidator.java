package fr.natsystem.projet;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.infrastructure.item.validator.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdresseDuplicateValidator
        implements Validator<HelloWorldBatchConfig.Adresse> {

    private final DuplicationJobListener duplicationJobListener;

    @Override
    public void validate(HelloWorldBatchConfig.Adresse adresse)
            throws ValidationException {

        String key =
                adresse.id()
                        + "|"
                        + adresse.type_position()
                        + "|"
                        + adresse.x()
                        + "|"
                        + adresse.y();

        if (duplicationJobListener.getExistingKeys().contains(key)) {
            throw new ValidationException("Adresse en doublon");
        }

        duplicationJobListener.getExistingKeys().add(key);
    }
}
