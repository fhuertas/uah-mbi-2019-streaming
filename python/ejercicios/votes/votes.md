confluent start kafka

connect-standalone connect-avro-standalone.properties sqlite-municipios.properties
kafka-topics --list --zookeeper localhost


confluent start ksql-server

CREATE STREAM votes_stream (Codigo BIGINT, Partido VARCHAR) WITH (KAFKA_TOPIC='votes', VALUE_FORMAT='JSON');

CREATE STREAM municipios_stream (Codigo INT, Municipio VARCHAR) WITH (KAFKA_TOPIC='connect_municipios', VALUE_FORMAT='JSON');
-- SELECT * FROM municipios_stream;

SET 'auto.offset.reset' = 'earliest';
CREATE STREAM municipios_by_key WITH (PARTITIONS=1) AS SELECT * FROM municipios_stream PARTITION BY Codigo;
-- SELECT * FROM municipios_by_key;

CREATE STREAM votes_enriched AS
  SELECT votes_stream.Codigo AS Codigo, Partido, Municipio
  FROM votes_stream
  INNER JOIN municipios_by_key WITHIN 1 DAYS ON votes_stream.Codigo = municipios_by_key.Codigo;


kafka-topics --list --zookeeper localhost



kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic VOTES_ENRICHED --property print.key=true --property print.value=true
