CREATE TABLE public.user (
  id serial PRIMARY KEY NOT NULL,
  username character varying(45) NOT NULL,
  email character varying(45) NOT NULL,
  password character varying(45) NOT NULL,
  score INTEGER DEFAULT 0,
  image varchar(50) DEFAULT 'no_avatar.png'
);