# Arquitectura Lambda

El objetivo de este ejercicio es familiarizar al estudiante con las decisiones de diseño de una aplicación completa y practicar con el procesado de streams en vivo con KSQL. Algunos componentes serán meramente simuladores, mientras que otros se simplificarán para no complicar en exceso el apartado de programación. El sistema seguirá el modelo de arquitectura lambda.

Se espera obtener el esqueleto de un portal de compra-venta de viviendas con la funcionalidad extra de que el sistema nos propondrá un precio estimado idóneo para las casas que están en venta a partir de los precios de casa ya vendidas. El sistema estará compuesto por:
- Un generador de casas vendidas. Este componente simulará que una casa previamente en venta pasa a ser vendida por una cantidad concreta.
- Un cliente de rama _batch_ que almacenará los datos de las casas vendidas.
- Un entrenador de un modelo de regresión lineal (extremadamente sencillo y que probablemente haga predicciones muy malas, pero no es el objetivo de este ejercicio entrar en el detalle del modelo de ML).
- Un generador de predicciones a partir del modelo entrenado por el componente anterior. Se supone que este componente puede ser usado por un sitio web al que accede un usuario y solicita una estimación de una casa actualmente en venta.
- Un procesador de streams que calcule, al vuelo, el precio medio por década de las casas vendidas en una ventana de tiempo.
- Un dashboard que lea la última ventana y muestre los estadísticos que ha calculado el procesador de streams.

## Diseño de la aplicación
1. De los elementos anteriores, ¿cuáles serían consumers y cuáles serían producers?
2. ¿Cuántos topics necesitaremos? ¿Necesitan clave? ¿Qué ventajas e inconvenientes tendría usar una partición? ¿Y usar varias?  ¿Cómo afecta esto al número de procesos que podemos desplegar? ¿Y Consumer Groups?
3. ¿Cuántas streams y/o tablas necesitaremos?
4. ¿Qué tipo de ventanas usaríamos en el procesado de streams?

## Implementación
Añadimos algunas restricciones más:
- Obtendremos los datos a partir de [House Sales in King County](https://www.kaggle.com/harlfoxem/housesalesprediction). Un script nos dividirá el CSV original en varios ficheros separados: un histórico inicial, un pseudogenerador de ventas y un dataset sin precio final.
- El generador de ventas escogerá elementos del CSV y emitirá un subconjunto de las columnas, incluido el precio. Se pueden repetir sin que por ello se vea afectado el sistema.
- El cliente (o clientes) de batch puede limitarse a escribir en un CSV histórico sin añadir procesado extra. Podría volcar los datos en una base de datos, en HDFS, S3, etc.
- Los procesos del entrenador y el generador de predicciones deben ser independientes: es decir, el script de python que genere el modelo no debe ser el mismo que use el modelo para predecir el precio. Debe haber un mecanismo para _exportar_ e _importar_ un modelo.
- El entrenador debe usar todos los datos de ventas producidos hasta el momento. El histórico dispondrá de datos antes de empezar a trabajar para tener una base sobre la que entrenar.
- El generador de predicciones puede ser un script estático que devuelva un precio a partir de un ID de casa, sin necesidad de añadir complejidad extra como peticiones web o similares.
- Las limitaciones de KSQL puede obligar a generar varias tablas cuando al principio parecía que una era suficiente. No hay que desesperar.

## Cómo arrancar la aplicación
Podemos regenerar los CSV a partir del dataset original con `build_datasets`, aunque se entregan unos CSV con los que trabajar. 

Debemos arrancar el stack completo con `confluent start`, seguido del notebook `house_sales_gen` y el `batch_client`. A continuación, abrimos la consola de KSQL con `ksql` y creamos los streams y tablas con el código de `houses.ksql`. Una vez hayamos arrancado los streams podemos arrancar `dashboard`.

La rama batch es independiente de Kafka y podemos ejecutar `model_trainer` y `model_predict` tantas veces necesitemos, incluso sin haber arrancado Kafka. Eso sí, para comprobar que los precios de `model_predict` cambiar deberemos reentrenar el modelo una vez hayan aparecido suficientes ventas en el stream.









