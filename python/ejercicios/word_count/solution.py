from confluent_kafka import Producer, Consumer, KafkaError
import time
import re


def word_count(text):
    return len(re.sub("[\n\t,. ]+", " ", text).split(" "))


def main():
    p = Producer({'bootstrap.servers': 'localhost:9092'})
    c = Consumer({
        'bootstrap.servers': 'localhost:9092',
        'group.id': round(time.time() * 1000),
        'auto.offset.reset': 'earliest'
    })

    c.subscribe(['ejercicio2-origen'])

    while True:
        msg = c.poll(1.0)
        if msg is None:
            pass
        elif msg.error():
            if msg.error().code() != KafkaError._PARTITION_EOF:
                # ignore EOF
                print("Consumer error: {}".format(msg.error()))

        elif msg:
            p.produce('ejercicio2-destino', str(word_count(msg.value().decode('utf-8'))))


if __name__ == "__main__":
    main()
