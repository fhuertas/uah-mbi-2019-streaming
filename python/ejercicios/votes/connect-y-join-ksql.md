# Join entre el topic de votos y la tabla estática en sqlite


Para combinar un stream y datos de una DB podemos aprovechar las herramientas Connect y KSQL. Primero arrancamos Connect con un conector que lee la tabla de municipios. Se podría configurar desde la UI, pero hay dos propiedades específicas que hay que configurar a través de un fichero `.properties`. Para ello arrancamos Connect independientemente del stack completo con:

```sh
cd uah-mbi-2019-streaming/python/ejercicios/votes
cp municipios.db /tmp/

confluent start kafka

connect-standalone connect-avro-standalone.properties sqlite-municipios.properties

# y en otra consola:
kafka-topics --list --zookeeper localhost

confluent start ksql-server
```

Arrancamos también el script `votes_gen` para inicializar el topic `votes`. Ahora abrimos el cliente de KSQL y creamos las siguientes streams:

```sql
CREATE STREAM votes_stream (Codigo BIGINT, Partido VARCHAR)
    WITH (KAFKA_TOPIC='votes', VALUE_FORMAT='JSON');

CREATE STREAM municipios_stream (Codigo INT, Comunidad VARCHAR, Provincia VARCHAR, Municipio VARCHAR)
    WITH (KAFKA_TOPIC='connect_municipios', VALUE_FORMAT='JSON');


SET 'auto.offset.reset' = 'earliest';
CREATE STREAM municipios_by_key WITH (PARTITIONS=1) AS
    SELECT * FROM municipios_stream PARTITION BY Codigo;


CREATE STREAM votes_enriched AS
  SELECT votes_stream.Codigo AS Codigo, Partido, Comunidad, Provincia, Municipio
  FROM votes_stream
  INNER JOIN municipios_by_key
      WITHIN 10 DAYS
      ON votes_stream.Codigo = municipios_by_key.Codigo;

```

Podemos inspeccionar las streams directamente en KSQL con:

```sql
SELECT * FROM municipios_stream;
SELECT * FROM municipios_by_key;
```

Finalmente revisamos que las streams de KSQL han creado los topics necesarios:

```sh
kafka-topics --list --zookeeper localhost

kafka-console-consumer --bootstrap-server localhost:9092 \
    --from-beginning --topic VOTES_ENRICHED \
    --property print.key=true --property print.value=true
``