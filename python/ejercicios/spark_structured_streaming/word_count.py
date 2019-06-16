from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split

import sys
import logging

log = logging.getLogger(__name__)
host = sys.argv[1] if len(sys.argv) > 1 else "localhost"
port = sys.argv[2] if len(sys.argv) > 2 else "9999"
mode = sys.argv[3] if len(sys.argv) > 2 else "complete"
log.warning(f"Socket {host}:{port}")
log.warning(f"Mode {mode}")

spark = SparkSession \
    .builder \
    .appName("StructuredNetworkWordCount") \
    .getOrCreate()

lines = spark \
    .readStream \
    .format("socket") \
    .option("host", host) \
    .option("port", port) \
    .load()

# Operaciones: Transformaciones y operaciones SQL
# Transformación. Dividir por espacios
words = lines.select(
    explode(split(lines.value, " ")).alias("word"))

wordCounts = words.groupBy("word").count()

# Sink: Consola
query = wordCounts \
    .writeStream \
    .outputMode(mode) \
    .format("console") \
    .start()

query.awaitTermination()
