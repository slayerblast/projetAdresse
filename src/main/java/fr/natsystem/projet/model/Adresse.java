package fr.natsystem.projet.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;


public record Adresse(
        @NotBlank String id,
        String id_fantoir,
        @NotBlank String numero,
        String rep,
        @NotBlank String nom_voie,
        String code_postal,
        @NotBlank String code_insee,
        @NotBlank String nom_commune,
        String code_insee_ancienne_commune,
        String nom_ancienne_commune,
        @NotNull Double x,
        @NotNull Double y,
        @NotNull Double lon,
        @NotNull Double lat,
        @NotBlank String type_position,
        String alias,
        String nom_ld,
        String libelle_acheminement,
        String nom_afnor,
        @NotBlank String source_position,
        @NotBlank String source_nom_voie,
        @NotNull Integer certification_commune,
        String cad_parcelles
) {
    public boolean isBetterThan(Adresse other) {

        // Règle 1
        if (!Objects.equals(certification_commune, other.certification_commune)) {
            return certification_commune == 1;
        }

        // Règle 2
        if (!Objects.equals(source_nom_voie, other.source_nom_voie)) {
            boolean thisUnknown =
                    "inconnue".equalsIgnoreCase(source_nom_voie);

            boolean otherUnknown =
                    "inconnue".equalsIgnoreCase(other.source_nom_voie);

            if (thisUnknown != otherUnknown) {
                return !thisUnknown;
            }
        }

        // Règle 3
        if (!Objects.equals(source_position, other.source_position)) {
            boolean thisUnknown =
                    "inconnue".equalsIgnoreCase(source_position);

            boolean otherUnknown =
                    "inconnue".equalsIgnoreCase(other.source_position);

            if (thisUnknown != otherUnknown) {
                return !thisUnknown;
            }
        }
        return false;
    }
}