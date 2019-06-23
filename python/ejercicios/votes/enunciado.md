# Architectura Kappa

El objetivo de este ejercicio es afianzar conceptos en el diseño de aplicaciones en streaming y practicar con Kafka Connect y Spark Streaming.

Se propone diseñar un sistema de procesado de votos para unas elecciones generales. Actualmente el recuento de votos no comienza hasta que se cierran los colegios electorales, a las 8:00 PM. Los resultados parciales se anuncian por goteo y el resultado final tarda horas. Queremos que, implementando un sistema de voto telemático, se permita saber en tiempo real el resultado de las votaciones y obtener el resultado final al cierre de las urnas. Nos mantendremos al margen del apartado etico del problema, ya que emitir resultados reales cuando aún hay gente votando puede alterar la intención de voto.

Podemos asumir que los votos son emitidos telemáticamente desde cabinas a tal efecto, que se valida la identidad del usuario con su DNI electrónico, que los votos no contienen PII, y que el voto, una vez emitido desde la cabina, está firmado con un certificado específico de la cabina. Kafka almancearía el voto completo, con firma electrónica incluida, y el procesado y validación de la firma se realiza en el núcleo de procesado.

El sistema estará formado por los siguientes componentes:
- Un generador de votos. Este componente se entrega fuera del ejercicio. Usa como fuente de datos los resultados de las generales de 2016.
- Una tabla estática con el listado de municipios por comunidad autónoma y provicia (leída preferiblemente con Connect o en su defecto con Spark).
- Un núcleo de procesado de resultados en tiempo real. Este componente calculará los resultados por comunidad autónoma y provincia y los escribirá en tiempo real en una base de datos.
- Kafka como desacoplador de los flujos de mensajes.

## Diseño de la aplicación
1. De los elementos anteriores, ¿cuáles serían consumers y cuáles serían producers?
2. ¿Cuántos topics necesitaríamos? ¿Necesitan una clave?
3. ¿Sobre qué topics produciría cada producer, y de qué topics leería cada consumer.
5. ¿Cuántos Consumer Groups necesitaríamos?

## Implementación
Consideraremos que:
- Los votos se emiten de uno en uno con un identificador del municipio donde se han emitido.
- La tabla estática contiene el mismo identificador que incluyen los votos (podríamos hilar más fino usando un ID de mesa electoral o de cabina de voto, pero la lógica a seguir es la misma y se simplifica la implementación).
- El conteo de votos se puede simplificar a una suma sencilla; es decir, no hay que entrar en el detalle de los escaños asignados a cada provincia ni considerar la ley D'Hondt.
- No es necesario implementar la _validación de la firma_ del voto, pero sí hacer uso de una una función auxiliar que devuelva un booleano a partir de los datos de un voto (puede ser un `return True`, pero debe aplicarse a todos los mensajes recibidos).
- Propondremos dos aproximaciones para combinar los datos de votos con los datos de municipios: con Connect y KSQL (opción preferida) y con Spark Streaming.

En un momento dado se detecta que los votos emitidos por una provincia concreta deben ser anulados por problemas técnicos en la firma de los mensajes. En un caso real sería más probable que se viera afectada una única cabina o mesa electoral, pero la aproximación al problema es similar. Debemos reprocesar todos los votos sin dejar de mostrar datos.

1. ¿Qué elementos habría que modificar, siguiendo la arquitectura Kappa?
2. ¿Qué modificaciones necesitan los topics, las particiones y los Consumer Groups?


## Cómo arrancar la aplicación
Podemos regenerar el fichero `votes.csv` a partir del dataset original con `build-votes-alone`, aunque se entrega un CSV con el que trabajar. Se entrega también un `build-votes-with-cities` para generar un stream que ya combina votos con municipios. Esta alternativa sólo se debe usar si no conseguimos avanzar en la combinación del stream de votos con la tabla estática.

Si optamos por unir el stream con la tabla estática en KSQL debemos seguir las instrucciones en `connect-y-join-ksql.md`. Una vez completado, podemos arrancar `process-votes-with-KSQL`.

Si optamos por implementar el join en Spark, será suficiente con arrancar Kafka con `confluent start kafka`, seguido de `votes_gen` y de `process-votes-spark-only`.








