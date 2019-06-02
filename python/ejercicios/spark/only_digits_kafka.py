import logging
import re
import sys

from pyspark.sql import SparkSession
from pyspark.sql.functions import *

log = logging.getLogger(__name__)
host = sys.argv[1] if len(sys.argv) > 1 else "localhost"
port = sys.argv[2] if len(sys.argv) > 2 else "9999"

log.warning(f"Socket {host}:{port}")

spark = SparkSession \
    .builder \
    .appName("Only digits") \
    .getOrCreate()
# Source: DataFrame con la entrada por web socket. Cada entrada representa una linea

lines = spark \
    .readStream \
    .format("socket") \
    .option("host", host) \
    .option("port", port) \
    .load()


# Split the lines into words

def numbers(string):
    result = re.sub("\\D", "", string)
    return int(0 if result == "" else result)


remove_chars = udf(numbers)
words = lines.withColumn("value", remove_chars("value"))

# Generate running word count
# word_counts = words.withColumn("value", sum("value"))

query = words \
    .writeStream \
    .outputMode("append") \
    .format("console") \
    .start()

query.awaitTermination()
