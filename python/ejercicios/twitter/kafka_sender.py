from confluent_kafka import Producer

import string


class KafkaSender:

    def __init__(self, params, topic):
        self.p = Producer(params)
        print(f'Producing in topic: {topic}, properties: {params}')
        self.topic = topic

    def send(self, msg):
        self.p.poll(0)
        self.p.produce(self.topic, msg.encode("utf-8"))
