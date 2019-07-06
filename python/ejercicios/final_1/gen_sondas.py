"""Generator of on and off .

Usage:
  gen_sondas.py --bootstrap-server=<server> --topic=<topic> --sensors=<sensors> --time=<time>
  gen_sondas.py  (--help | -h)

Options:
  -h, --help                                Show help message
  -n <sensors>, --sensors=<sensors>         Number of sensors
  -b <server>, --bootstrap-server=<server>  Kafka connection String
  -t <topic>, --topic=<topic>               Kafka topic
  -c <time>, --time=<time>           Time between changes (in seconds)
"""

import json
import logging
import random
import time

from confluent_kafka import Producer
from docopt import docopt


def to_csv():
    pass


def main(args):
    logging.debug(args)

    bootstrap_server = args['--bootstrap-server']
    n_sensors = int(args['--sensors'])
    topic = args['--topic']
    change_time = float(args['--time'])

    p = Producer({'bootstrap.servers': bootstrap_server})

    senson_names = [f"Sensor {i}" for i in range(n_sensors)]

    while True:
        time.sleep(change_time)
        logging.debug("New event")
        for sensor in range(n_sensors):
            data = dict()
            data["id"] = sensor
            data["name"] = senson_names[sensor]
            data["temp"] = random.randrange(-10, 50)
            data["damp"] = random.randrange(0, 101) / 100.0
            str_data = json.dumps(data)
            logging.debug(f"Publish {str_data}")
            p.produce(topic, key=str(sensor), value=str_data.encode('utf-8'))


if __name__ == "__main__":
    logging.basicConfig(level=logging.DEBUG)
    arguments = docopt(__doc__, version='Sensor generator')
    main(arguments)
