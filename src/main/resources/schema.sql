DROP TABLE IF EXISTS adresse;
DROP TABLE IF EXISTS adresse_staging;

CREATE TABLE IF NOT EXISTS adresse (
    id TEXT,
    id_fantoir TEXT,
    numero TEXT,
    rep TEXT,
    nom_voie TEXT,
    code_postal TEXT,
    code_insee TEXT,
    nom_commune TEXT,
    code_insee_ancienne_commune TEXT,
    nom_ancienne_commune TEXT,
    x REAL,
    y REAL,
    lon REAL,
    lat REAL,
    type_position TEXT,
    alias TEXT,
    nom_ld TEXT,
    libelle_acheminement TEXT,
    nom_afnor TEXT,
    source_position TEXT,
    source_nom_voie TEXT,
    certification_commune INTEGER,
    cad_parcelles TEXT,
    UNIQUE(id,type_position,x,y)

);

CREATE TABLE IF NOT EXISTS adresse_staging (

                                       id TEXT,
                                       id_fantoir TEXT,
                                       numero TEXT,
                                       rep TEXT,
                                       nom_voie TEXT,
                                       code_postal TEXT,
                                       code_insee TEXT,
                                       nom_commune TEXT,
                                       code_insee_ancienne_commune TEXT,
                                       nom_ancienne_commune TEXT,
                                       x REAL,
                                       y REAL,
                                       lon REAL,
                                       lat REAL,
                                       type_position TEXT,
                                       alias TEXT,
                                       nom_ld TEXT,
                                       libelle_acheminement TEXT,
                                       nom_afnor TEXT,
                                       source_position TEXT,
                                       source_nom_voie TEXT,
                                       certification_commune INTEGER,
                                       cad_parcelles TEXT

    );

DELETE FROM adresse_staging;

/*
CREATE INDEX IF NOT EXISTS idx_adresse_code_insee
    ON adresse(code_insee);
CREATE INDEX IF NOT EXISTS idx_staging_paging
    ON adresse_staging(code_insee, id);
CREATE INDEX IF NOT EXISTS idx_staging_key
    ON adresse_staging(id,type_position,x,y);
*/
