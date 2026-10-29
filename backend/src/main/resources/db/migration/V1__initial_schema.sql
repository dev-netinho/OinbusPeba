CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE identity_user_account (id uuid PRIMARY KEY,email varchar(254) NOT NULL UNIQUE,display_name varchar(80) NOT NULL,password_hash varchar(100) NOT NULL,role varchar(16) NOT NULL CHECK(role IN ('USER','ADMIN')),created_at timestamptz NOT NULL,version bigint NOT NULL DEFAULT 0);
CREATE TABLE identity_password_reset (id uuid PRIMARY KEY,user_id uuid NOT NULL REFERENCES identity_user_account(id) ON DELETE CASCADE,token_hash varchar(128) NOT NULL UNIQUE,expires_at timestamptz NOT NULL,used_at timestamptz);
CREATE TABLE personalization_favorite_journey (id uuid PRIMARY KEY,user_email varchar(254) NOT NULL,origin_id varchar(80) NOT NULL,destination_id varchar(80) NOT NULL,label varchar(100) NOT NULL,created_at timestamptz NOT NULL,UNIQUE(user_email,origin_id,destination_id));

CREATE TABLE transit_operator (id uuid PRIMARY KEY,name varchar(120) NOT NULL,provenance jsonb NOT NULL DEFAULT '{}');
CREATE TABLE transit_stop (id varchar(80) PRIMARY KEY,name varchar(120) NOT NULL,location geography(Point,4326) NOT NULL,accessible boolean,provenance jsonb NOT NULL,version bigint NOT NULL DEFAULT 0);
CREATE INDEX transit_stop_location_gix ON transit_stop USING gist(location);
CREATE TABLE transit_line (id varchar(80) PRIMARY KEY,operator_id uuid REFERENCES transit_operator(id),short_name varchar(20) NOT NULL,name varchar(120) NOT NULL,color char(7) NOT NULL,provenance jsonb NOT NULL,version bigint NOT NULL DEFAULT 0);
CREATE TABLE transit_pattern (id uuid PRIMARY KEY,line_id varchar(80) NOT NULL REFERENCES transit_line(id),direction_id smallint NOT NULL CHECK(direction_id IN (0,1)),headsign varchar(120) NOT NULL,geometry geometry(LineString,4326) NOT NULL,provenance jsonb NOT NULL);
CREATE INDEX transit_pattern_geometry_gix ON transit_pattern USING gist(geometry);
CREATE TABLE transit_pattern_stop (pattern_id uuid NOT NULL REFERENCES transit_pattern(id) ON DELETE CASCADE,stop_id varchar(80) NOT NULL REFERENCES transit_stop(id),stop_sequence int NOT NULL CHECK(stop_sequence>0),distance_m int NOT NULL CHECK(distance_m>=0),PRIMARY KEY(pattern_id,stop_sequence),UNIQUE(pattern_id,stop_id,stop_sequence));
CREATE TABLE transit_calendar (id uuid PRIMARY KEY,name varchar(80) NOT NULL,monday boolean NOT NULL,tuesday boolean NOT NULL,wednesday boolean NOT NULL,thursday boolean NOT NULL,friday boolean NOT NULL,saturday boolean NOT NULL,sunday boolean NOT NULL,start_date date NOT NULL,end_date date NOT NULL);
CREATE TABLE transit_trip (id uuid PRIMARY KEY,pattern_id uuid NOT NULL REFERENCES transit_pattern(id),calendar_id uuid NOT NULL REFERENCES transit_calendar(id),service_start_seconds int NOT NULL CHECK(service_start_seconds>=0),provenance jsonb NOT NULL);
CREATE TABLE transit_stop_time (trip_id uuid NOT NULL REFERENCES transit_trip(id) ON DELETE CASCADE,stop_sequence int NOT NULL,arrival_offset_seconds int NOT NULL CHECK(arrival_offset_seconds>=0),departure_offset_seconds int NOT NULL CHECK(departure_offset_seconds>=arrival_offset_seconds),PRIMARY KEY(trip_id,stop_sequence));
CREATE TABLE transit_notice (id uuid PRIMARY KEY,title varchar(120) NOT NULL,body text NOT NULL,severity varchar(16) NOT NULL,starts_at timestamptz NOT NULL,ends_at timestamptz,active boolean NOT NULL,version bigint NOT NULL DEFAULT 0);

CREATE TABLE operations_outbox_event (id uuid PRIMARY KEY,type varchar(120) NOT NULL,payload text NOT NULL,occurred_at timestamptz NOT NULL,published_at timestamptz,attempts int NOT NULL DEFAULT 0);
CREATE INDEX operations_outbox_pending_idx ON operations_outbox_event(occurred_at) WHERE published_at IS NULL;
CREATE TABLE operations_processed_event (consumer varchar(80) NOT NULL,event_id uuid NOT NULL,processed_at timestamptz NOT NULL,PRIMARY KEY(consumer,event_id));
CREATE TABLE operations_audit (id bigserial PRIMARY KEY,actor varchar(254) NOT NULL,action varchar(120) NOT NULL,resource varchar(160) NOT NULL,details jsonb NOT NULL DEFAULT '{}',occurred_at timestamptz NOT NULL DEFAULT now());
