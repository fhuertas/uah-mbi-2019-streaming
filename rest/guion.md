# kafka REST

## Preparar el entorno

```bash
apt install jq
confluent start

```

## Operaciones

```bash
# Obtener lista de topics
$ curl "http://localhost:8082/topics"
  
# Obtener informacion de un topic
$ curl "http://localhost:8082/topics/untopic"

# Producir mensajes
$ curl -X POST -H "Content-Type: application/vnd.kafka.json.v2+json" \
      http://localhost:8082/topics/untopic \
      --data '{"records":[{"value":{"name": "juan"}}]}'
      
      
# Crear un consumidor en JSON, comenzando desde el principio del topic

$ curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" \
               -H "Accept: application/vnd.kafka.v2+json" \
               http://localhost:8082/consumers/my_consumer_group \
               --data '{"name": "my_consumer_name", "format": "json", 
               "auto.offset.reset": "earliest"}'

# Subscribirse a un topic

$ curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" \
              --data '{"topics":["untopic"]}' \
  http://localhost:8082/consumers/my_consumer_group/instances/my_consumer_name/subscription
  
# Then consume some data from a topic using the base URL in the first response.

$ curl -X GET -H "Accept: application/vnd.kafka.json.v2+json" \
    http://localhost:8082/consumers/my_consumer_group/instances/my_consumer_name/records
  
# Eliminar el consumidor

$ curl -X DELETE -H "Accept: application/vnd.kafka.v2+json" \
    http://localhost:8082/consumers/my_consumer_group/instances/my_consumer_name
```


