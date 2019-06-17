from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *


def job():
    spark = SparkSession \
        .builder \
        .appName("KafkaTweetsReader").getOrCreate()

    schema_messages = StructType([
        StructField("userId", StringType(), True),
        StructField("createdAt", StringType(), True)])

    schema_users = StructType([StructField("id", StringType(), True), StructField("name", StringType(), True)])

    kafka_input = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:19092,localhost:29092,localhost:39092") \
        .option("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
        .option("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
        .option("subscribe", "tweets") \
        .load()

    users = spark.read.format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:19092,localhost:29092,localhost:39092") \
        .option("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
        .option("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
        .option("subscribe", "users") \
        .load()
    users = users \
        .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)") \
        .withColumn("users_json", from_json('value', schema_users)) \
        .withColumn("key", col('users_json.id'))

    tweets = kafka_input.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)") \
        .withColumn("msg_json", from_json('value', schema_messages)) \
        .withColumn("key", col('msg_json.userId'))

    join_df = users.join(tweets, "key")

    # query = raw_input.writeStream.format("json").option("path", "/tmp/json-dir").start()
    query = join_df \
        .writeStream \
        .outputMode("append") \
        .format("console") \
        .start()

    query.awaitTermination()


if __name__ == "__main__":
    job()
