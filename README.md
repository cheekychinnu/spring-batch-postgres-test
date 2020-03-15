**Installation steps** 

* Install postgres windows 
* initdb
```C:\Users\vino\Downloads\pgsql\bin>initdb -D "C:\Users\vino\Downloads\pgsql\datadir"
The files belonging to this database system will be owned by user "vino".
This user must also own the server process.

The database cluster will be initialized with locale "English_United States.1252".
The default database encoding has accordingly been set to "WIN1252".
The default text search configuration will be set to "english".

Data page checksums are disabled.

creating directory C:/Users/vino/Downloads/pgsql/datadir ... ok
creating subdirectories ... ok
selecting dynamic shared memory implementation ... windows
selecting default max_connections ... 100
selecting default shared_buffers ... 128MB
selecting default time zone ... Asia/Calcutta
creating configuration files ... ok
running bootstrap script ... ok
performing post-bootstrap initialization ... ok
syncing data to disk ... ok

initdb: warning: enabling "trust" authentication for local connections
You can change this by editing pg_hba.conf or using the option -A, or
--auth-local and --auth-host, the next time you run initdb.

Success. You can now start the database server using:

    pg_ctl -D ^"C^:^\Users^\vino^\Downloads^\pgsql^\datadir^" -l logfile start
```
* Start postgres server	
```
C:\Users\vino\Downloads\pgsql\bin>pg_ctl -D "C:\Users\vino\Downloads\pgsql\datadir" start
```

* Use client (or use dbeaver) and give permissions to user "postgres" and assign password too.
```
C:\Users\vino\Downloads\pgsql\bin>psql -d postgres
psql (12.2)
WARNING: Console code page (437) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

postgres=# CREATE USER postgres SUPERUSER;
CREATE ROLE
postgres=# CREATE DATABASE postgres WITH OWNER postgres;
ERROR:  database "postgres" already exists
postgres=# ALTER USER postgres WITH PASSWORD 'admin';
ALTER ROLE
```

**Note**

Try changing the datatype from `TIMESTAMP` to `TIMESTAMPTZ` at V1_1__batch.sql - for table `dataset_watermark` and `BATCH_JOB_EXECUTION_PARAMS`. Run the cleanup statements at resources/database-helper.sql before running the test with the datatype changed.

Input can be fed from swagger-ui at http://localhost:9098/swagger-ui.html#!. Watch out for the logs in the console.

This is the expectation : (which works when you use `TIMESTAMPTZ`)

        assertKt(convertToUTCDate("20200308 06:05:00")); // is 20200308 01:05:00 EST
        assertKt(convertToUTCDate("20200308 07:05:00")); // is 20200308 03:05:00 EDT
        assertKt(convertToUTCDate("20201101 05:05:00")); // is 20201101 01:05:00 EDT
        assertKt(convertToUTCDate("20201101 06:05:00")); // is 20201101 01:05:00 EST

But if you use `TIMESTAMP`

       assertKt(convertToUTCDate("20201101 05:05:00")); // is 20201101 01:05:00 EDT   --> this will come as EST when hit watermark API
 
Also this is how you can change the timezone of the postgres server :
```
C:\Users\vino\Downloads\pgsql\bin>psql -d postgres
postgres=# ALTER DATABASE postgres SET timezone TO 'UTC';
ALTER DATABASE
postgres=#  SELECT pg_reload_conf();
 pg_reload_conf
----------------
 t
(1 row)


postgres=# show timezone;
 TimeZone
----------
 UTC
(1 row)

postgres=# SELECT * FROM pg_timezone_names; // check this out for other timezone names that you can set using the ALTER query mentioned 
```