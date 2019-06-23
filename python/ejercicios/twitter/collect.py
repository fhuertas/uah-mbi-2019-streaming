"""Collector.

The secrets vars needed are:
 * secrets.YOUR_CONSUMER_KEY
 * secrets.YOUR_CONSUMER_SECRET
 * secrets.YOUR_ACCESS_TOKEN
 * secrets.YOUR_ACCESS_SECRET

Usage:
  collector.py  --bootstrap-server=<server> --hashtag=<hashtag> --secrets-file=<secret-file> --topic-messages=<msgs-topic> --topic-users=<users-topic>
  collector.py  (--help | -h)

Options:
  -h, --help                                           Show help message
  -b <server>, --bootstrap-server=<server>             Kafka connection String
  -t <hashtag>, --hashtag=<hashtag>                    Hashtag to filter
  -s <secret-file>, --secrets-file=<secret-file>       Path where the secrets vars is stored. The format should be yaml
  -u <users-topic>, --topic-users=<users-topic>        Topic for users
  -m <msgs-topic>, --topic-messages=<msgs-topic>       Topic for msgs
"""
import tweepy
import yaml
from docopt import docopt
from tweepy import OAuthHandler
from tweepy import Stream

from ejercicios.twitter.kafka_sender import KafkaSender
from ejercicios.twitter.listener import MyListener


def main(args):
    secrets = yaml.safe_load(open(args['--secrets-file']))
    hashtag = args['--hashtag']
    topic_users = args['--topic-users']
    topic_msgs = args['--topic-messages']
    bootstrap_server = args['--bootstrap-server']

    #    producer = Producer()
    auth = OAuthHandler(secrets['secrets']['CONSUMER_KEY'], secrets['secrets']['CONSUMER_SECRET'])
    auth.set_access_token(secrets['secrets']['ACCESS_TOKEN'], secrets['secrets']['ACCESS_SECRET'])
    api = tweepy.API(auth)
    sender = KafkaSender({'bootstrap.servers': bootstrap_server}, topic_msgs)
    sender_user = KafkaSender({'bootstrap.servers': bootstrap_server}, topic_users)
    twitter_stream = Stream(auth, MyListener(api, sender, sender_user))
    print(f'Getting tweets about {hashtag}')
    twitter_stream.filter(track=[f'#{hashtag}'])


if __name__ == "__main__":
    arguments = docopt(__doc__, version='Tweeter consumer')
    main(arguments)
