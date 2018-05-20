CREATE TABLE public.user (
  id serial PRIMARY KEY NOT NULL,
  username character varying(45) NOT NULL,
  email character varying(45) NOT NULL,
  password character varying(1000) NOT NULL,
  score INTEGER DEFAULT 0,
  image varchar(50) DEFAULT 'no_avatar.png'
);

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