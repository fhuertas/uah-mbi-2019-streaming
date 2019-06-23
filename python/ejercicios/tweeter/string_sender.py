class StringSender:
    def __init__(self):
        self.count = 0

    def send(self, msg):
        cleaned = msg.encode('ascii', 'ignore').decode('utf-8')
        print(f'{self.count} --> {cleaned}')
        self.count += 1
