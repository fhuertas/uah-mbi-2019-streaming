"""Generator of on and off .

Usage:
  gen_samples.py --bootstrap-server=<server> --topic=<topic> --sensors=<sensors> --change-time=<time> --threshold=<threshold>
  gen_samples.py  (--help | -h)

Options:
  -h, --help                                Show help message
  -n <sensors>, --sensors=<sensors>         Number of sensors
  -b <server>, --bootstrap-server=<server>  Kafka connection String
  -t <topic>, --topic=<topic>               Kafka topic
  -c <time>, --change-time=<time>           Time between changes (in seconds)
  -H <threshold>, --threshold=<threshold>   Probability of change of sensor [0-1]. E.g 0.9
"""
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
    change_time = float(args['--change-time'])
    threshold = float(args['--threshold'])

    p = Producer({'bootstrap.servers': bootstrap_server})
    sensor_status = [True for _ in range(n_sensors)]
    logging.debug("First commit")
    for sensor in range(n_sensors):
        value = str("True")
        logging.debug(f'Publish: "{sensor}:{value}"')
        p.produce(topic, key=str(sensor), value=value   .encode('utf-8'))

    while True:
        time.sleep(change_time)
        logging.debug("New event")
        if random.random() < threshold:
            logging.debug(sensor_status)
            sensor = random.randrange(0, n_sensors)
            logging.debug(f"Changing sensor nº {sensor} from {sensor_status[sensor]} to {not sensor_status[sensor]}")
            sensor_status[sensor] = not sensor_status[sensor]
            key = str(sensor)
            value = str(sensor_status[sensor])
            logging.debug(f'Publish: "{key}:{value}"')
            p.produce(topic, key=str(sensor), value=value.encode('utf-8'))


if __name__ == "__main__":
    logging.basicConfig(level=logging.DEBUG)
    arguments = docopt(__doc__, version='Sensor generator')
    main(arguments)
