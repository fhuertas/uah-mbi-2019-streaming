# HOWTO clases prácticas

Los ejercicios de las clases prácticas necesitan librerías adicionales. Para instalar las dependencias y activar el entorno la primera vez:
```sh
cd ~/uah-mbi-2019-streaming/python/
make env
```

Una vez instaladas también podemos activar el entorno directamente y arrancar Jupyter con:
```sh
cd
. ./uah-mbi-2019-streaming/python/env/bin/activate
jupyter notebook
```

A partir de ese momento podemos abrir los notebooks en http://localhost:8888.

El stack de confluent se arranca como en los demás ejercicios, sin necesidad de activar un entorno de python. Por ejemplo, podemos arrancar el stack completo con `confluent start`, o arrancar sólo el boker de Kafka con `confluent start kafka`.

Para ejecutar los comandos de KSQL debemos arrancar el servidor de KSQL, bien con `confluent start` o `confluent start ksql-server`, y ejecutar `ksql` para iniciar el CLI. Si hemos arrancado el control center también podemos ejecutar las consultas en la interfaz web en http://localhost:9021.

Los clientes de shell de Kafka son útiles para comprobar qué topics hemos arrancado y qué mensajes están entrando, así como modificar los topics si es necesario. Por ejemplo:

```sh
kafka-topics --list --zookeeper localhost
kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic TOPIC_NAME
kafka-topics --zookeeper localhost --alter --partitions 2 --topic TOPIC_NAME
```