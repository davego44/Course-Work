--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 9.6.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: project; Type: SCHEMA; Schema: -; Owner: dnftpvcumulkrj
--

CREATE SCHEMA project;


ALTER SCHEMA project OWNER TO dnftpvcumulkrj;

--
-- Name: teamact; Type: SCHEMA; Schema: -; Owner: dnftpvcumulkrj
--

CREATE SCHEMA teamact;


ALTER SCHEMA teamact OWNER TO dnftpvcumulkrj;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = project, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE account (
    id integer NOT NULL,
    type_id integer NOT NULL,
    number bigint NOT NULL,
    amount numeric(16,2) NOT NULL
);


ALTER TABLE account OWNER TO dnftpvcumulkrj;

--
-- Name: account_id_seq; Type: SEQUENCE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE account_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: account_id_seq; Type: SEQUENCE OWNED BY; Schema: project; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE account_id_seq OWNED BY account.id;


--
-- Name: account_type; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE account_type (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE account_type OWNER TO dnftpvcumulkrj;

--
-- Name: account_type_id_seq; Type: SEQUENCE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE account_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE account_type_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: account_type_id_seq; Type: SEQUENCE OWNED BY; Schema: project; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE account_type_id_seq OWNED BY account_type.id;


--
-- Name: history; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE history (
    account_id integer NOT NULL,
    history_entry_id integer NOT NULL
);


ALTER TABLE history OWNER TO dnftpvcumulkrj;

--
-- Name: history_entry; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE history_entry (
    id integer NOT NULL,
    type_id integer NOT NULL,
    info text NOT NULL,
    "time" timestamp without time zone NOT NULL,
    amount numeric(16,2) NOT NULL
);


ALTER TABLE history_entry OWNER TO dnftpvcumulkrj;

--
-- Name: history_entry_id_seq; Type: SEQUENCE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE history_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE history_entry_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: history_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: project; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE history_entry_id_seq OWNED BY history_entry.id;


--
-- Name: history_type; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE history_type (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE history_type OWNER TO dnftpvcumulkrj;

--
-- Name: history_type_id_seq; Type: SEQUENCE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE history_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE history_type_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: history_type_id_seq; Type: SEQUENCE OWNED BY; Schema: project; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE history_type_id_seq OWNED BY history_type.id;


--
-- Name: user; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE "user" (
    id integer NOT NULL,
    username character varying(30) NOT NULL,
    password character varying(30) NOT NULL,
    first_name character varying(30) NOT NULL,
    middle_name character varying(30),
    last_name character varying(30) NOT NULL,
    email character varying(35) NOT NULL,
    phone character varying(12) NOT NULL,
    address character varying(200) NOT NULL,
    ssn character varying(9) NOT NULL,
    birthdate date NOT NULL,
    drivers_lic character varying(30) NOT NULL
);


ALTER TABLE "user" OWNER TO dnftpvcumulkrj;

--
-- Name: user_account; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE user_account (
    user_id integer NOT NULL,
    account_id integer NOT NULL
);


ALTER TABLE user_account OWNER TO dnftpvcumulkrj;

--
-- Name: user_id_seq; Type: SEQUENCE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: project; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE user_id_seq OWNED BY "user".id;


--
-- Name: user_member; Type: TABLE; Schema: project; Owner: dnftpvcumulkrj
--

CREATE TABLE user_member (
    user_id integer NOT NULL,
    member_id integer NOT NULL
);


ALTER TABLE user_member OWNER TO dnftpvcumulkrj;

SET search_path = public, pg_catalog;

--
-- Name: conf_date; Type: TABLE; Schema: public; Owner: dnftpvcumulkrj
--

CREATE TABLE conf_date (
    id integer NOT NULL,
    month character varying(10),
    year integer,
    session character varying(10)
);


ALTER TABLE conf_date OWNER TO dnftpvcumulkrj;

--
-- Name: notes; Type: TABLE; Schema: public; Owner: dnftpvcumulkrj
--

CREATE TABLE notes (
    id integer NOT NULL,
    note text,
    conf_date_id integer,
    speaker_id integer
);


ALTER TABLE notes OWNER TO dnftpvcumulkrj;

--
-- Name: speakers; Type: TABLE; Schema: public; Owner: dnftpvcumulkrj
--

CREATE TABLE speakers (
    id integer NOT NULL,
    first character varying(80),
    middle character varying(80),
    last character varying(80)
);


ALTER TABLE speakers OWNER TO dnftpvcumulkrj;

SET search_path = teamact, pg_catalog;

--
-- Name: scripture_topics; Type: TABLE; Schema: teamact; Owner: dnftpvcumulkrj
--

CREATE TABLE scripture_topics (
    topic_id integer NOT NULL,
    scripture_id integer NOT NULL
);


ALTER TABLE scripture_topics OWNER TO dnftpvcumulkrj;

--
-- Name: scriptures; Type: TABLE; Schema: teamact; Owner: dnftpvcumulkrj
--

CREATE TABLE scriptures (
    id integer NOT NULL,
    book text,
    chapter integer,
    verse integer,
    content character varying(256)
);


ALTER TABLE scriptures OWNER TO dnftpvcumulkrj;

--
-- Name: scriptures_id_seq; Type: SEQUENCE; Schema: teamact; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE scriptures_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE scriptures_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: scriptures_id_seq; Type: SEQUENCE OWNED BY; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE scriptures_id_seq OWNED BY scriptures.id;


--
-- Name: topics; Type: TABLE; Schema: teamact; Owner: dnftpvcumulkrj
--

CREATE TABLE topics (
    id integer NOT NULL,
    name character varying(100)
);


ALTER TABLE topics OWNER TO dnftpvcumulkrj;

--
-- Name: topics_id_seq; Type: SEQUENCE; Schema: teamact; Owner: dnftpvcumulkrj
--

CREATE SEQUENCE topics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE topics_id_seq OWNER TO dnftpvcumulkrj;

--
-- Name: topics_id_seq; Type: SEQUENCE OWNED BY; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER SEQUENCE topics_id_seq OWNED BY topics.id;


SET search_path = project, pg_catalog;

--
-- Name: account id; Type: DEFAULT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY account ALTER COLUMN id SET DEFAULT nextval('account_id_seq'::regclass);


--
-- Name: account_type id; Type: DEFAULT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY account_type ALTER COLUMN id SET DEFAULT nextval('account_type_id_seq'::regclass);


--
-- Name: history_entry id; Type: DEFAULT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history_entry ALTER COLUMN id SET DEFAULT nextval('history_entry_id_seq'::regclass);


--
-- Name: history_type id; Type: DEFAULT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history_type ALTER COLUMN id SET DEFAULT nextval('history_type_id_seq'::regclass);


--
-- Name: user id; Type: DEFAULT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY "user" ALTER COLUMN id SET DEFAULT nextval('user_id_seq'::regclass);


SET search_path = teamact, pg_catalog;

--
-- Name: scriptures id; Type: DEFAULT; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY scriptures ALTER COLUMN id SET DEFAULT nextval('scriptures_id_seq'::regclass);


--
-- Name: topics id; Type: DEFAULT; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY topics ALTER COLUMN id SET DEFAULT nextval('topics_id_seq'::regclass);


SET search_path = project, pg_catalog;

--
-- Data for Name: account; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY account (id, type_id, number, amount) FROM stdin;
1	2	282736473	698.37
2	2	282736486	201.24
3	2	282736437	149.44
4	1	282736472	409.85
\.


--
-- Name: account_id_seq; Type: SEQUENCE SET; Schema: project; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('account_id_seq', 4, true);


--
-- Data for Name: account_type; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY account_type (id, name) FROM stdin;
1	Checking
2	Savings
\.


--
-- Name: account_type_id_seq; Type: SEQUENCE SET; Schema: project; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('account_type_id_seq', 2, true);


--
-- Data for Name: history; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY history (account_id, history_entry_id) FROM stdin;
4	2
4	3
1	4
4	5
4	6
2	7
4	8
3	9
3	10
4	11
\.


--
-- Data for Name: history_entry; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY history_entry (id, type_id, info, "time", amount) FROM stdin;
2	1	Restaurant	2017-10-05 06:46:00	24.99
3	1	Mall Store	2017-06-15 02:46:00	59.98
4	1	Transaction	2017-10-20 06:21:23.874658	4.00
5	2	Transaction	2017-10-20 06:21:23.874658	4.00
6	1	Transaction	2017-10-20 06:21:59.640606	15.29
7	2	Transaction	2017-10-20 06:21:59.640606	15.29
8	1	Transaction	2017-10-20 06:22:12.136658	68.23
9	2	Transaction	2017-10-20 06:22:12.136658	68.23
10	1	Transaction	2017-10-20 06:22:23.018382	4.00
11	2	Transaction	2017-10-20 06:22:23.018382	4.00
\.


--
-- Name: history_entry_id_seq; Type: SEQUENCE SET; Schema: project; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('history_entry_id_seq', 11, true);


--
-- Data for Name: history_type; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY history_type (id, name) FROM stdin;
1	Charge
2	Credit
\.


--
-- Name: history_type_id_seq; Type: SEQUENCE SET; Schema: project; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('history_type_id_seq', 2, true);


--
-- Data for Name: user; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY "user" (id, username, password, first_name, middle_name, last_name, email, phone, address, ssn, birthdate, drivers_lic) FROM stdin;
2	graderSon	password	Robert	Edward	Smith	smithRobert@gmail.com	9458745548	846 Happy Lane Rexburg, ID 83440	23456781	1999-10-03	L2392HJ1
3	graderDaughter	password	Emily	Nicole	Smith	smithEmily@gmail.com	945873427	846 Happy Lane Rexburg, ID 83440	34567812	1998-02-18	B2102001
4	lobo	password	David		Lobaccaro	lobo@gmail.com	4345654465	8392 Nothing Lane, Creek TX 95682	123121545	1998-05-08	KD8293
1	grader	password	Bob	James	Smith	smithBob@gmail.com	9165486470	846 Happy Lane Rexburg, ID 83440	123456789	1972-08-14	HS288361
\.


--
-- Data for Name: user_account; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY user_account (user_id, account_id) FROM stdin;
1	4
1	1
2	2
3	3
\.


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: project; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('user_id_seq', 4, true);


--
-- Data for Name: user_member; Type: TABLE DATA; Schema: project; Owner: dnftpvcumulkrj
--

COPY user_member (user_id, member_id) FROM stdin;
1	2
1	3
\.


SET search_path = public, pg_catalog;

--
-- Data for Name: conf_date; Type: TABLE DATA; Schema: public; Owner: dnftpvcumulkrj
--

COPY conf_date (id, month, year, session) FROM stdin;
1	October	2017	Priesthood
\.


--
-- Data for Name: notes; Type: TABLE DATA; Schema: public; Owner: dnftpvcumulkrj
--

COPY notes (id, note, conf_date_id, speaker_id) FROM stdin;
1	Awesome!	1	1
2	Awesome2!	1	1
3	Awesome3!	1	2
4	Awesome4!	1	2
5	Awesome5!	1	3
6	Awesome6!	1	3
\.


--
-- Data for Name: speakers; Type: TABLE DATA; Schema: public; Owner: dnftpvcumulkrj
--

COPY speakers (id, first, middle, last) FROM stdin;
1	Thomas	S.	Monson
2	D.	Todd	Christofferson
3	Henry	B.	Eyring
\.


SET search_path = teamact, pg_catalog;

--
-- Data for Name: scripture_topics; Type: TABLE DATA; Schema: teamact; Owner: dnftpvcumulkrj
--

COPY scripture_topics (topic_id, scripture_id) FROM stdin;
2	15
1	17
2	17
1	18
2	18
3	18
1	19
1	20
3	20
3	21
\.


--
-- Data for Name: scriptures; Type: TABLE DATA; Schema: teamact; Owner: dnftpvcumulkrj
--

COPY scriptures (id, book, chapter, verse, content) FROM stdin;
1	John	1	5	And the light shineth in darkness; and the darkness comprehended it not.
2	Doctrine and Covenants	88	49	The light shineth in darkness, and the darkness comprehendeth it not; nevertheless, the day shall come when you shall comprehend even God, being quickened in him and by him.
3	Doctrine and Covenants	93	28	He that keepeth his commandments receiveth truth and light, until he is glorified in truth and knoweth all things.
4	Mosiah	16	9	He is the light and the life of the world; yea, a light that is endless, that can never be darkened; yea, and also a life which is endless, that there can be no more death.
5	book	1	1	hello
6	Hebrews	11	4	nothing
7	Hebrews	11	4	nothing
8	bab	1	2	asda
9	book	1	2	asd
10	as	1	2	sadsa
11	asda	1	2	asd
12	asdas	1	2	asd
13	asda1	111	1	asd
14	zxc	1	2	asd
15	asda	1	2	asd
16	Dsa	2	4	asd
17	fd	2	3	asd
18	asda	2	3	asd
19	Hebrews	11	14	But without faith it is impossible to please him: for he that cometh to God must believe that he is, and that he is a rewarder of them that diligently seek him.
20	1 Corinthians	13	13	And now abideth faith, hope, charity, these three; but the greatest of these is charity.
21	Moroni	7	47	But charity is the pure love of Christ, and it endureth forever; and whoso is found possessed of it at the last day, it shall be well with him.
\.


--
-- Name: scriptures_id_seq; Type: SEQUENCE SET; Schema: teamact; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('scriptures_id_seq', 21, true);


--
-- Data for Name: topics; Type: TABLE DATA; Schema: teamact; Owner: dnftpvcumulkrj
--

COPY topics (id, name) FROM stdin;
1	Faith
2	Sacrifice
3	Charity
\.


--
-- Name: topics_id_seq; Type: SEQUENCE SET; Schema: teamact; Owner: dnftpvcumulkrj
--

SELECT pg_catalog.setval('topics_id_seq', 3, true);


SET search_path = project, pg_catalog;

--
-- Name: account account_number_key; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_number_key UNIQUE (number);


--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- Name: account_type account_type_pkey; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY account_type
    ADD CONSTRAINT account_type_pkey PRIMARY KEY (id);


--
-- Name: history_entry history_entry_pkey; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history_entry
    ADD CONSTRAINT history_entry_pkey PRIMARY KEY (id);


--
-- Name: history_type history_type_pkey; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history_type
    ADD CONSTRAINT history_type_pkey PRIMARY KEY (id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: user user_username_key; Type: CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_username_key UNIQUE (username);


SET search_path = public, pg_catalog;

--
-- Name: conf_date conf_date_pkey; Type: CONSTRAINT; Schema: public; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY conf_date
    ADD CONSTRAINT conf_date_pkey PRIMARY KEY (id);


--
-- Name: notes notes_pkey; Type: CONSTRAINT; Schema: public; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY notes
    ADD CONSTRAINT notes_pkey PRIMARY KEY (id);


--
-- Name: speakers speakers_pkey; Type: CONSTRAINT; Schema: public; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY speakers
    ADD CONSTRAINT speakers_pkey PRIMARY KEY (id);


SET search_path = teamact, pg_catalog;

--
-- Name: scriptures scriptures_pkey; Type: CONSTRAINT; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY scriptures
    ADD CONSTRAINT scriptures_pkey PRIMARY KEY (id);


--
-- Name: topics topics_pkey; Type: CONSTRAINT; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (id);


SET search_path = project, pg_catalog;

--
-- Name: account account_type_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_type_id_fkey FOREIGN KEY (type_id) REFERENCES account_type(id);


--
-- Name: history history_account_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history
    ADD CONSTRAINT history_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- Name: history_entry history_entry_type_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history_entry
    ADD CONSTRAINT history_entry_type_id_fkey FOREIGN KEY (type_id) REFERENCES history_type(id);


--
-- Name: history history_history_entry_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY history
    ADD CONSTRAINT history_history_entry_id_fkey FOREIGN KEY (history_entry_id) REFERENCES history_entry(id);


--
-- Name: user_account user_account_account_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_account_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- Name: user_account user_account_user_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_account_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: user_member user_member_member_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY user_member
    ADD CONSTRAINT user_member_member_id_fkey FOREIGN KEY (member_id) REFERENCES "user"(id);


--
-- Name: user_member user_member_user_id_fkey; Type: FK CONSTRAINT; Schema: project; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY user_member
    ADD CONSTRAINT user_member_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);


SET search_path = public, pg_catalog;

--
-- Name: notes notes_conf_date_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY notes
    ADD CONSTRAINT notes_conf_date_id_fkey FOREIGN KEY (conf_date_id) REFERENCES conf_date(id);


--
-- Name: notes notes_speaker_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY notes
    ADD CONSTRAINT notes_speaker_id_fkey FOREIGN KEY (speaker_id) REFERENCES speakers(id);


SET search_path = teamact, pg_catalog;

--
-- Name: scripture_topics scripture_topics_scripture_id_fkey; Type: FK CONSTRAINT; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY scripture_topics
    ADD CONSTRAINT scripture_topics_scripture_id_fkey FOREIGN KEY (scripture_id) REFERENCES scriptures(id);


--
-- Name: scripture_topics scripture_topics_topic_id_fkey; Type: FK CONSTRAINT; Schema: teamact; Owner: dnftpvcumulkrj
--

ALTER TABLE ONLY scripture_topics
    ADD CONSTRAINT scripture_topics_topic_id_fkey FOREIGN KEY (topic_id) REFERENCES topics(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: dnftpvcumulkrj
--

REVOKE ALL ON SCHEMA public FROM postgres;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO dnftpvcumulkrj;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: plpgsql; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON LANGUAGE plpgsql TO dnftpvcumulkrj;


--
-- PostgreSQL database dump complete
--

