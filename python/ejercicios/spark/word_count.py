from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split

spark = SparkSession \
    .builder \
    .appName("StructuredNetworkWordCount") \
    .getOrCreate()

# Source: DataFrame con la entrada por web socket. Cada entrada representa una linea
lines = spark \
    .readStream \
    .format("socket") \
    .option("host", "localhost") \
    .option("port", 9999) \
    .load()

# Operaciones: Transformaciones y operaciones SQL
# Transformación. Dividir por espacios
words = lines.select(
    explode(split(lines.value, " ")).alias("word"))

wordCounts = words.groupBy("word").count()

# Sink: Consola
query = wordCounts \
    .writeStream \
    .outputMode("complete") \
    .format("console") \
    .start()

query.awaitTermination()
