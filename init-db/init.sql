CREATE EXTENSION pgagent;

CREATE TABLE Users (
    USER_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    USERNAME VARCHAR(100) UNIQUE NOT NULL,
    EMAIL VARCHAR(100) UNIQUE NOT NULL,
    US_PASSWORD VARCHAR(60) NOT NULL,
    CREATED_AT TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'America/Sao_Paulo'),
    UPDATED_AT TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'America/Sao_Paulo')
);

CREATE TABLE Devices (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    MODEL VARCHAR(100) NOT NULL,
    FIRMWARE_VERSION VARCHAR(50),
    IP INET NOT NULL,
    SERIAL_NUMBER VARCHAR(100) UNIQUE,
    CREATED_AT TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'America/Sao_Paulo'),
    UPDATED_AT TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'America/Sao_Paulo'),
    USER_ID UUID REFERENCES Users(USER_ID) ON DELETE SET NULL
);

CREATE INDEX idx_devices_ip ON Devices (IP);

CREATE TABLE Status (
    STATUS_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    DEVICE_ID UUID NOT NULL REFERENCES Devices(ID) ON DELETE CASCADE,
    STATUS VARCHAR(10) NOT NULL,
    LAST_PING TIMESTAMPTZ NOT NULL
);


DO $$
DECLARE
    jid integer;
    scid integer;
BEGIN
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'update_status_job'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'update_status'::text, true, 's'::character(1),
    ''::text, 'DB_CAMWATCH'::name, 'f'::character(1),
    'UPDATE public.status 
SET status = ''offline'' 
WHERE last_ping < (now() AT TIME ZONE ''America/Sao_Paulo'' - INTERVAL ''2 minutes'');'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'job_schedule'::text, ''::text, true,
    '2024-08-01 00:00:00-03'::timestamp with time zone, '2099-12-30 00:00:00-03'::timestamp with time zone,
    -- Minutes
    '{t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t}'::bool[]::boolean[],
    -- Hours
    '{t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t}'::bool[]::boolean[],
    -- Week days
    '{t,t,t,t,t,t,t}'::bool[]::boolean[],
    -- Month days
    '{t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t}'::bool[]::boolean[],
    -- Months
    '{t,t,t,t,t,t,t,t,t,t,t,t}'::bool[]::boolean[]
) RETURNING jscid INTO scid;
END
$$;
