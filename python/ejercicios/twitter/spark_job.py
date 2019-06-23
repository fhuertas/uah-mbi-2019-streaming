from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *
import time
# submit with --packages org.apache.spark:spark-streaming-kafka-0-10_2.11:2.4.3"
spark = SparkSession \
    .builder \
    .appName("KafkaTweetsReader").getOrCreate()

msgs_schema = StructType([StructField("id", LongType(), False),
                          StructField("text", StringType(), False),
                          StructField("user", StructType([StructField("id", LongType(), False)]), False)])

user_schema = StructType([StructField("id", LongType(), False),
                          StructField("screen_name", StringType(), False),
                          StructField("location", StringType(), True),
                          StructField("name", StringType(), False)])

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
    .withColumn("user_id", col("message.user.id")) \
    .drop("user") \
    .drop("value")

users = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
    .option("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
    .option("subscribe", "users") \
    .load() \
    .select(col("value").cast("string"), col("timestamp")) \
    .withColumn("json", from_json("value", user_schema)).select("json.*")

join = msgs.join(users, msgs.user_id == users.id)

# Console
# query = join.writeStream.outputMode("append").format("console").start()

query = join\
    .selectExpr("to_json(struct(*)) AS value") \
    .writeStream \
    .outputMode("append") \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("topic", "join-topic") \
    .option("checkpointLocation", f"/tmp/join-{time.time()}") \
    .start()
query.awaitTermination()
