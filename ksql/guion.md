# KSQL

## Nociones generales. 

Motor de consultas sobre topics de Kafka
* Los datos tienen que estar semi-estructurados, y deben tener esquema registrado en 
Schema Registry
* Existen dos tipos de entidades, Streams y Tablas
* Las tablas (KTables) son mutables por id,
* Los streams (KStreams) son inmutables.
* Compatible (desde la versión de confluet platform 5.0.0) con AVRO, JSON, y CSV
* Existen clientes gráficos y de consola
* Los datos de las consultas se persisten en kafka

Las operaciones no son tan potentes como un motor SQL clásico. Algunas de las peculiaridades de las 
operaciones son:
* Sólo los datos descritos en el esquema son consultables
* Las operaciones de Join en muchos de los casos necesitan ventanas temporales sobre las que funcionar
* Las tablas solo se pueden unir mediante la clave, nunca mediante otro campo. 
* Para muchas operaciones sobre las tablas se utiliza el último campo del identificador como 
elemento de tabla
* Las tablas y steams están particionadas, las particiones son las mismas que los topics
sobre los que están persistidos. 
* Las operaciones de unión necesitan que el número de particiones de las tablas sean las mismas.


## Demo

### Visitas en tiempo real de una página web 

Levantar los servicios de kafka necesarios

Desde la carpeta de cp-platform

```bash
# Consola 1: 
# Arrancar Servicios
confluent start
# Cliente por consola
ksql
```

```bash
# Consola 2: Generador de visitas
ksql-datagen quickstart=pageviews format=delimited topic=pageviews maxInterl=500
```

```bash
# Consola 3: Generador de información de usuarios
ksql-datagen quickstart=users format=json topic=users maxInterval=100
```

### Operaciones sobre Topics

```
# Desde la consola 1
show topics; -- Muestra todos los topics
print "users"; -- Consumidor del topic

```

### Operaciones básicas sobre Streams

```
SHOW STREAMS; -- Muestra los streams del servidor ksql

CREATE STREAM pageviews_original (viewtime bigint, userid varchar, pageid varchar) WITH \
  (kafka_topic='pageviews', value_format='DELIMITED'); -- Crea un stream

DROP STREAM pageviews_original; -- Borra un stream
```


### Operaciones básicas sobre Tablas

```
SHOW TABLES; -- Muestra las tablas de ksql

CREATE TABLE users_original (registertime BIGINT, gender VARCHAR, regionid VARCHAR, userid VARCHAR) WITH \
  (kafka_topic='users', value_format='JSON', key = 'userid'); -- crea una tabla

DROP TABLE users_original

```

### Consultas sobre tablas y streams

```genericsql
-- Muestra las tres siguientes paginas visitadas
SELECT pageid FROM pageviews_original LIMIT 3;  

-- Nueva tabla con los datos enriquecidos de las visita
CREATE STREAM pageviews_enriched AS
  SELECT users_original.userid AS userid, pageid, regionid, gender
    FROM pageviews_original
    LEFT JOIN users_original
    ON pageviews_original.userid = users_original.userid; 

-- Filtrado de visitas por genero
CREATE STREAM pageviews_female AS
  SELECT * FROM pageviews_enriched
    WHERE gender = 'FEMALE';

-- Filtado por region
CREATE STREAM pageviews_female_like_89
    WITH (kafka_topic='pageviews_enriched_r8_r9') AS
  SELECT * FROM pageviews_female
    WHERE regionid LIKE '%_8' OR regionid LIKE '%_9';

-- visitas en la region por ventana de 30 segundos
CREATE TABLE pageviews_regions
      WITH (VALUE_FORMAT='avro') AS
    SELECT gender, regionid , COUNT(*) AS numusers
    FROM pageviews_enriched
      WINDOW TUMBLING (size 30 second)
    GROUP BY gender, regionid
    HAVING COUNT(*) > 1;

-- Visitas de usuarios de más de una incidencia
CREATE TABLE visits_users
      WITH (VALUE_FORMAT = 'avro') AS
    SELECT userid, pageid, COUNT(*) AS visits
    FROM pageviews_original
      WINDOW TUMBLING (size 1 minute)
    GROUP BY userid, pageid
    HAVING COUNT(*) > 1;

```

### Conslutas varias

```genericsql
-- Muestra las queries que están activas
SHOW QUERIES;

-- Consulta a una tabla
SELECT gender, regionid, numusers FROM pageviews_regions LIMIT 5;

-- Describir tablas
DESCRIBE EXTENDED PAGEVIEWS_REGIONS;
```

## Estrcuturas anidadas

### Entorno

```bash
# Arrancar concluent si no lo staba
# Carga de datos de pedidos
ksql-datagen quickstart=orders format=avro topic=orders
```

#### Extra

Nota consultar los esquemas en Schema Registry:
```bash
# Subjects
curl localhost:8081/subjects/orders-value/versions
# Esquemas
curl localhost:8081/schemas/ids/1
``` 

### Consultas

```genericsql

CREATE STREAM ORDERS WITH (KAFKA_TOPIC='orders', VALUE_FORMAT='AVRO');


DESCRIBE ORDERS
-- Datos anidados
SELECT ORDERID, ADDRESS->CITY FROM ORDERS;
```


## Joins


### Entorno

```bash
# Asegurar que confluent esta arrancado

kafka-console-producer \
      --broker-list localhost:9092 \
      --topic new_orders \
      --property "parse.key=true" \
      --property "key.separator=:"<<EOF
1:{"order_id":1,"total_amount":10.50,"customer_name":"Bob Smith"}
2:{"order_id":2,"total_amount":3.32,"customer_name":"Sarah Black"}
3:{"order_id":3,"total_amount":21.00,"customer_name":"Emma Turner"}
EOF

$ <path-to-confluent>/bin/kafka-console-producer \
      --broker-list localhost:9092 \
      --topic shipments \
      --property "parse.key=true" \
      --property "key.separator=:"<<EOF
1:{"order_id":1,"shipment_id":42,"warehouse":"Nashville"}
3:{"order_id":3,"shipment_id":43,"warehouse":"Palo Alto"}
EOF

kafka-console-producer \
      --broker-list localhost:9092 \
      --topic warehouse_location2 \
      --property "parse.key=true" \
      --property "key.separator=:"<<EOF
1:{"warehouse_id":1,"city":"Leeds","country":"UK"}
2:{"warehouse_id":2,"city":"Sheffield","country":"UK"}
3:{"warehouse_id":3,"city":"Berlin","country":"Germany"}
EOF
      
kafka-console-producer \
    --broker-list localhost:9092 \
    --topic warehouse_size \
    --property "parse.key=true" \
    --property "key.separator=:"<<EOF
1:{"warehouse_id":1,"square_footage":16000}
2:{"warehouse_id":2,"square_footage":42000}
3:{"warehouse_id":3,"square_footage":94000}
EOF

```




### Join stream-stream

```genericsql
-- Nota: Para procesar los mensajes desde el principio de topic
SET 'auto.offset.reset' = 'earliest';

-- steams implicados
CREATE STREAM NEW_ORDERS (ORDER_ID INT, TOTAL_AMOUNT DOUBLE, CUSTOMER_NAME VARCHAR)
 WITH (KAFKA_TOPIC='new_orders', VALUE_FORMAT='JSON');
 
CREATE STREAM SHIPMENTS (ORDER_ID INT, SHIPMENT_ID INT, WAREHOUSE VARCHAR)
 WITH (KAFKA_TOPIC='shipments', VALUE_FORMAT='JSON');
 
SELECT ORDER_ID, TOTAL_AMOUNT, CUSTOMER_NAME FROM NEW_ORDERS LIMIT 3;

SELECT O.ORDER_ID, O.TOTAL_AMOUNT, O.CUSTOMER_NAME,
 S.SHIPMENT_ID, S.WAREHOUSE
 FROM NEW_ORDERS O
 INNER JOIN SHIPMENTS S
    WITHIN 1 HOURS
    ON O.ORDER_ID = S.ORDER_ID;
```

### Join table-table

```genericsql

CREATE TABLE WAREHOUSE_LOCATION (WAREHOUSE_ID INT, CITY VARCHAR, COUNTRY VARCHAR)
    WITH (KAFKA_TOPIC='warehouse_location',
          VALUE_FORMAT='JSON',
          KEY='WAREHOUSE_ID');

CREATE TABLE WAREHOUSE_SIZE (WAREHOUSE_ID INT, SQUARE_FOOTAGE DOUBLE)
    WITH (KAFKA_TOPIC='warehouse_size',
          VALUE_FORMAT='JSON',
          KEY='WAREHOUSE_ID');

SELECT WL.WAREHOUSE_ID, WL.CITY, WL.COUNTRY, WS.SQUARE_FOOTAGE
    FROM WAREHOUSE_LOCATION WL
      inner JOIN WAREHOUSE_SIZE WS
        ON WL.WAREHOUSE_ID=WS.WAREHOUSE_ID
    LIMIT 3;

```

## Insert into

### Generadores

```bash

# En consolas separadas
ksql-datagen quickstart=orders format=avro topic=orders_local

ksql-datagen quickstart=orders format=avro topic=orders_3rdparty
```

```genericsql
CREATE STREAM ORDERS_SRC_LOCAL
  WITH (KAFKA_TOPIC='orders_local', VALUE_FORMAT='AVRO');

CREATE STREAM ORDERS_SRC_3RDPARTY
  WITH (KAFKA_TOPIC='orders_3rdparty', VALUE_FORMAT='AVRO');

CREATE STREAM ALL_ORDERS AS SELECT 'LOCAL' AS SRC, * FROM ORDERS_SRC_LOCAL;

INSERT INTO ALL_ORDERS SELECT '3RD PARTY' AS SRC, * FROM ORDERS_SRC_3RDPARTY;

```