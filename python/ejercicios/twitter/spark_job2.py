from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *

spark = SparkSession \
    .builder \
    .appName("KafkaTweetsReader").getOrCreate()

msgs_schema = StructType([StructField("id", LongType(), False),
                          StructField("text", StringType(), False),
                          StructField("user", StructType([StructField("id", LongType(), False)]), False)])

msgs = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
    .option("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
    .option("subscribe", "msgs") \
    .load() \
    .select(col("value").cast("string"), col("timestamp")) \
    .withColumn("message", from_json("value", msgs_schema)) \
    .withColumn("user_id", col("message.user.id"))
msgs_grouped = msgs \
    .withWatermark("timestamp", "1 minutes") \
    .groupBy(window(msgs.timestamp, "10 seconds", "2 seconds"), msgs.user_id) \
    .count()

query = msgs_grouped.writeStream.outputMode("complete").format("console").start()
query.awaitTermination()
