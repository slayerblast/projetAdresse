DROP TABLE IF EXISTS adresse;

CREATE TABLE IF NOT EXISTS adresse (
    id TEXT NOT NULL,
    id_fantoir TEXT,
    numero TEXT NOT NULL,
    rep TEXT,
    nom_voie TEXT NOT NULL,
    code_postal TEXT,
    code_insee TEXT NOT NULL,
    nom_commune TEXT NOT NULL,
    code_insee_ancienne_commune TEXT,
    nom_ancienne_commune TEXT,
    x REAL NOT NULL,
    y REAL NOT NULL,
    lon REAL NOT NULL,
    lat REAL NOT NULL,
    type_position TEXT NOT NULL,
    alias TEXT,
    nom_ld TEXT,
    libelle_acheminement TEXT,
    nom_afnor TEXT,
    source_position TEXT NOT NULL,
    source_nom_voie TEXT NOT NULL,
    certification_commune INTEGER NOT NULL,
    cad_parcelles TEXT

);