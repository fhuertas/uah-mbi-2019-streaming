# Servicios dockerizados

Esta carpeta contiene los servicios de Confluent Platform y Apache Spark. Estos
se levantan mediante contenedores docker mediante docker-compose. Sirven como alternativa
a la maquina virtual y es compatible con windows 10 Pro, Linux y Mac. Es necesario tener 
docker y docker-compose instalado

## Apache Spark

```bash
# Dentro de la carpeta de Spark
docker-compse -p spark down # Solo si es necesario limpiar un servicio anterior
docker-compose -p spark up 
```
En los ficheros `spark/spark_master_env` y `spark/spark_worker_eng` contienen variales 
de entorno que configuran ciertos aspectos del servicio de de spark.
 

## Confluent Platform

Los servicios desplegados son loso siguientes

* Zookeeper (1 instancia)
* kafka (1 broker)
* Schema registry
* Kafka Connect
* KSQL
* Control center

No es necesario levantar todos los servicios del stack. 

```bash
# Dentro de la carpeta cp-platform
docker-compose -p cp-platform down # si es necesario limpiar una instancia del servicio anterior
docker-compose -p cp-platform up [<id del servicio>] # Si no se indica nada se arrancan todos los servicios  
```

Identificadores de los servicios: 
* zookeeper
* broker
* schema-registry
* connect
* control-center
* ksql-server
* ksql-cli
* ksql-datagen
* rest-proxy
