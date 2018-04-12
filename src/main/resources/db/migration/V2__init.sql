CREATE TABLE public.levels (
  id serial PRIMARY KEY NOT NULL,
  level INTEGER UNIQUE,
  level_name character varying(45) UNIQUE
);

CREATE TABLE public.user_results (
  id serial PRIMARY KEY NOT NULL,
  level_id INTEGER REFERENCES public.levels(id),
  user_id INTEGER REFERENCES public.user(id),
  score INTEGER DEFAULT 0
);