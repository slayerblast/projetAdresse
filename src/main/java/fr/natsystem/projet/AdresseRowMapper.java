package fr.natsystem.projet;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AdresseRowMapper implements RowMapper<HelloWorldBatchConfig.Adresse> {

    @Override
    public HelloWorldBatchConfig.Adresse mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        return new HelloWorldBatchConfig.Adresse(
                rs.getString("id"),
                rs.getString("id_fantoir"),
                rs.getString("numero"),
                rs.getString("rep"),
                rs.getString("nom_voie"),
                rs.getString("code_postal"),
                rs.getString("code_insee"),
                rs.getString("nom_commune"),
                rs.getString("code_insee_ancienne_commune"),
                rs.getString("nom_ancienne_commune"),
                rs.getDouble("x"),
                rs.getDouble("y"),
                rs.getDouble("lon"),
                rs.getDouble("lat"),
                rs.getString("type_position"),
                rs.getString("alias"),
                rs.getString("nom_ld"),
                rs.getString("libelle_acheminement"),
                rs.getString("nom_afnor"),
                rs.getString("source_position"),
                rs.getString("source_nom_voie"),
                rs.getInt("certification_commune"),
                rs.getString("cad_parcelles")
        );
    }
}