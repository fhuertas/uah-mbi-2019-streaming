from confluent_kafka import Producer, Consumer, KafkaError
import time
import re
import sys


def word_count(text):
    return len(re.sub("[\n\t,. ]+", " ", text).split(" "))


def main():
    print(len(sys.argv))
    origen = 'origen' if len(sys.argv) < 2 else sys.argv[1]
    destino = 'destino' if len(sys.argv) < 3 else sys.argv[2]

    # Configuracion producer
    p = Producer({'bootstrap.servers': 'localhost:9092'})
    # Configuracion del consumidor
    c = Consumer({
        'bootstrap.servers': 'localhost:9092',
        'group.id': round(time.time() * 1000),
        'auto.offset.reset': 'earliest'
    })

    print(f'Subscrito al canal {origen} y Produciendo en el canal {destino}')

    c.subscribe([origen])

    while True:
        msg = c.poll(1.0)
        if msg is None:
            pass
        elif msg.error():
            if msg.error().code() != KafkaError._PARTITION_EOF:
                # ignore EOF
                print("Consumer error: {}".format(msg.error()))

        elif msg:
            result = word_count(msg.value().decode('utf-8'))
            print(f'Existen {result} palabras en la frase: "{str(msg.value()[:40], "utf-8")}"')
            p.produce(destino, str(result))


if __name__ == "__main__":
    main()
