from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import time


def slit_by_space(line):
    return line.split(" ")


# Creando el contexto, AppName y master establecidos en spark submit
sc = SparkContext(appName=f'word-count-{round(time.time() * 1000)}')
ssc = StreamingContext(sc, 1)
# Checkpouint para operaciones de ventanas
ssc.checkpoint("/tmp/wordcount")
# Tipo de conexión socket
lines = ssc.socketTextStream("localhost", 9999)

# lógica
word_pairs = lines.flatMap(slit_by_space) \
    .map(lambda word: (word, 1))

word_counts = word_pairs.reduceByKey(lambda x, y: x + y)

word_counts.pprint()

ssc.start()
ssc.awaitTermination()
