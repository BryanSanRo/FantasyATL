-- =========
-- TIPOS (opcional pero recomendado en PostgreSQL)
-- =========
DO $$ BEGIN
  CREATE TYPE resultado_estado AS ENUM ('ok','dns','dnf','dq','np','nm','ret');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
  CREATE TYPE tipo_pista AS ENUM ('AL','PC');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
  CREATE TYPE prueba_unidad AS ENUM ('s','m','pts');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- Si quieres controlar sector también como ENUM (opcional):
DO $$ BEGIN
  CREATE TYPE prueba_sector AS ENUM (
    'Velocidad','MedioFondo','Fondo','Vallas','Saltos',
    'Lanzamientos','Marcha','Combinadas','Relevos'
  );
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- =========
-- 1) ATLETAS
-- =========
CREATE TABLE IF NOT EXISTS atletas (
  id            bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nombre        text NOT NULL,
  apellido      text NOT NULL,
  sexo          char(1) NOT NULL CHECK (sexo IN ('H','M','X')),
  fecha_nac     date,
  nacionalidad  text
);

CREATE INDEX IF NOT EXISTS idx_atletas_apellido_nombre
  ON atletas (apellido, nombre);

-- =========
-- 2) LUGARES (igual que te la dejé antes)
-- =========
CREATE TABLE IF NOT EXISTS lugares (
  id      bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nombre  text NOT NULL,
  ciudad  text,
  pais    text,
  UNIQUE (nombre, ciudad, pais)
);

-- =========
-- 3) COMPETICIONES
-- =========
CREATE TABLE IF NOT EXISTS competiciones (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nombre      text NOT NULL,
  fecha       date NOT NULL,
  lugar_id    bigint NOT NULL REFERENCES lugares(id),
  tipo_pista  tipo_pista NOT NULL,  -- 'AL' o 'PC'
  tipo        text
);

CREATE INDEX IF NOT EXISTS idx_competiciones_fecha
  ON competiciones (fecha);

CREATE INDEX IF NOT EXISTS idx_competiciones_lugar
  ON competiciones (lugar_id);

-- =========
-- 4) PRUEBAS
-- =========
CREATE TABLE IF NOT EXISTS pruebas (
  id      bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  codigo  text NOT NULL UNIQUE,
  nombre  text NOT NULL,
  sector  prueba_sector NOT NULL,
  unidad  prueba_unidad NOT NULL
);

-- =========
-- 5) RESULTADOS
-- =========
CREATE TABLE IF NOT EXISTS resultados (
  id              bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

  atleta_id       bigint NOT NULL REFERENCES atletas(id),
  competicion_id  bigint NOT NULL REFERENCES competiciones(id),
  prueba_id       bigint NOT NULL REFERENCES pruebas(id),

  estado          resultado_estado NOT NULL DEFAULT 'ok',

  -- Para tiempos/distancias mejor NUMERIC que double (evitas “flotantes raros”)
  -- 3 decimales suele cubrir: 7.623s, 5.432m, etc.
  marca_num       numeric(10,3),

  posicion        integer,

  record_personal boolean NOT NULL DEFAULT false,
  record_mundial  boolean NOT NULL DEFAULT false
);

CREATE INDEX IF NOT EXISTS idx_resultados_atleta
  ON resultados(atleta_id);

CREATE INDEX IF NOT EXISTS idx_resultados_competicion
  ON resultados(competicion_id);

CREATE INDEX IF NOT EXISTS idx_resultados_prueba
  ON resultados(prueba_id);

-- Opcional (muy recomendable): evita duplicar el mismo atleta en la misma prueba+competición
-- (si quieres permitir varias rondas/series, NO lo pongas o añade columna "ronda" y métela en el unique)
-- CREATE UNIQUE INDEX IF NOT EXISTS ux_resultados_unico
--   ON resultados(atleta_id, competicion_id, prueba_id);