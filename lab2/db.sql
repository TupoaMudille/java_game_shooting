CREATE TABLE IF NOT EXISTS public.player_scores
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    name character varying COLLATE pg_catalog."default" NOT NULL,
    hits integer NOT NULL,
    aces integer NOT NULL,
    CONSTRAINT player_scores_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.player_scores
    OWNER to postgres;