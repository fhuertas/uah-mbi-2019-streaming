# Kafka Connect

## Preparar el entorno

```bash

sudo apt install sqlite jq

confluent start

# Drivers de confluent. 
confluent-hub install confluentinc/kafka-connect-jdbc:5.2.1

confluent list connectors
```

## Crear una base de datos

```sql
sqlite /tmp/test.db

create table cuentas(
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
    name VARCHAR(255)
    edad INTEGER);

INSERT INTO cuentas(nombre, edad) VALUES ('juan', '33');

INSERT INTO cuentas(nombre, edad) VALUES ('antonio', '22');
```

## Crear source

Se puede crear mediante la interfaz web o mediante el cliente de confluent indicandle un JSON

### Carga desde ingerfaz web

En la UI -> http://localhost:9021 -> Kafka connect -> Bring data in -> Add connector. Parametros:

* Connector class: **JdbcSourceConnector**
* Name: **sqlite-source**
* Common: 
  * Tasks max: **1**
* Database
  * JDBC Url: **jdbc:sqlite:/tmp/test.db**
* Mode
  * Table loading Mode: **incrementing**
  * Incrementing Column Name: **id**
* Connector
  * Topic Prefix: **sqlite-test-**

Comprobar que los topics se han creado:

```bash
kafka-topics --list --zookeeper localhost
# Buscar que empiezen por "sqlite-test-"
kafka-console-consumer --bootstrap-server localhost:9092 \
            --topic sqlite-test-cuentas --from-beginning
```
EXTRA:
* Insertar nuevos datos y observar como aparecen en el topic
* Obtener los esquemas de Schema Registry
 
### Carga desede Json

```bash
confluent load <path/to/file.json>
```
Fichero json:
```json
{
  "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
  "tasks.max": "1",
  "connection.url": "jdbc:sqlite:/tmp/test.db",
  "mode": "incrementing",
  "incrementing.column.name": "id",
  "topic.prefix": "sqlite-test-",
  "name": "sqlite-source"
}
```

## Crear un  Sink

Se puede crear mediante la interfaz web o mediante el cliente de confluent indicandle un JSON

### Desde interfaz web

En la UI -> http://localhost:9021 -> Kafka connect -> Send data out -> Add connector. Parametros:

* Topics: **sqlite-test-cuentas**
* Connector class: **JdbcSourceConnector**
* Name: **sqlite-sink**
* Common: 
  * Tasks max: **1**
* Database
  * JDBC Url: **jdbc:sqlite:/tmp/test.db**
* SQL/DDL Support
  * Auto-create: **true**
  
Para comprobar que se ha creado la tabla de destino: 
```sql
sqlite /tmp/test.db

.tables

SELECT * FROM cuentas;

SELECT * FROM "sqlite-test-cuentas"

```
### Desde cliente confluent
```bash
confluent load <path/to/file.json>
```
Fichero json:
```json
{
  "name": "test-1-sink",
  "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
  "tasks.max": "1",
  "topics": [
    "test-1-cuentas"
  ],
  "connection.url": "jdbc:sqlite:/tmp/test.db",
  "auto.create": "true"
}
```

## Enlaces de interes

https://docs.confluent.io/current/connect/kafka-connect-jdbc/source-connector/index.html
https://docs.confluent.io/current/connect/kafka-connect-jdbc/sink-connector/index.html