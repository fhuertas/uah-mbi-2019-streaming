import time

from pyspark import SparkContext
from pyspark.streaming import StreamingContext

# Creando el contexto, AppName y master establecidos en spark submit
sc = SparkContext(appName=f'word-count-{round(time.time() * 1000)}')
ssc = StreamingContext(sc, 1)
# Checkpouint para operaciones de ventanas
ssc.checkpoint("/tmp/wordcount")
# Tipo de conexión socket
lines = ssc.socketTextStream("localhost", 9999)

# lógica
word_pairs = lines.flatMap(lambda line: line.split(" ")) \
    .map(lambda word: (word, 1))

word_counts_windowed = word_pairs.reduceByKeyAndWindow(lambda x, y: x + y, lambda x, y: x - y, 30, 10)

word_counts_windowed.pprint()

ssc.start()
ssc.awaitTermination()
