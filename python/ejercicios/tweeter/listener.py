from tweepy.streaming import StreamListener
import json


class MyListener(StreamListener):

    def __init__(self, api, sender_msg, sender_user):
        super().__init__()
        self.api = api
        self.sender_msg = sender_msg
        self.sender_user = sender_user

    def on_data(self, data):
        try:
            json_data = json.loads(data)
            self.sender_msg.send(data)
            user = self.api.lookup_users([json_data['user']['id']])
            for u in user:
                self.sender_user.send(json.dumps(u._json))

            return True
        except BaseException as e:
            print("Error on_data: %s" % str(e))
        return True

    def on_error(self, status):
        print(status)
        return True
