# Architectura Kappa

El objetivo de este ejercicio es afianzar conceptos en el diseño de aplicaciones en streaming y practicar con Kafka Connect y Spark Streaming.

Se propone diseñar un sistema de procesado de votos para unas elecciones generales. Actualmente el recuento de votos no comienza hasta que se cierran los colegios electorales, a las 8:00 PM. Los resultados parciales se anuncian por goteo y el resultado final tarda horas. Queremos que, implementando un sistema de voto telemático, se permita saber en tiempo real el resultado de las votaciones y obtener el resultado final al cierre de las urnas. Nos mantendremos al margen del apartado etico del problema, ya que emitir resultados reales cuando aún hay gente votando puede alterar la intención de voto.

Podemos asumir que los votos son emitidos telemáticamente desde cabinas a tal efecto, que se valida la identidad del usuario con su DNI electrónico, que los votos no contienen PII, y que el voto, una vez emitido desde la cabina, está firmado con un certificado específico de la cabina. Kafka almancearía el voto completo, con firma electrónica incluida, y el procesado y validación de la firma se realiza en el núcleo de procesado.

El sistema estará formado por los siguientes componentes:
- Un generador de votos. Este componente se entrega fuera del ejercicio. Usa como fuente de datos los resultados de las generales de 2016.
- Una tabla estática con el listado de municipios por comunidad autónoma y provicia.
- Un núcleo de procesado de resultados en tiempo real. Este componente calculará los resultados por comunidad autónoma y los escribirá en tiempo real en una base de datos.
- Kafka como desacoplador de los flujos de mensajes.

1. De los elementos anteriores, ¿cuáles serían consumers y cuáles serían producers?
2. ¿Cuántos topics necesitaríamos? ¿Necesitan una clave?
3. ¿Sobre qué topics produciría cada producer, y de qué topics leería cada consumer.
5. ¿Cuántos Consumer Groups necesitaríamos?

Consideraremos que:
- Los votos se emiten de uno en uno con un identificador del municipio donde se han emitido.
- La tabla estática contiene el mismo identificador que incluyen los votos (podríamos hilar más fino usando un ID de mesa electoral o de cabina de voto, pero la lógica a seguir es la misma y se simplifica la implementación).
- Propondremos dos aproximaciones para combinar los datos de votos con los datos de municipios: con KSQL y con Spark Streaming.



En un momento dado se detecta que los votos emitidos por una provincia concreta deben ser anulados por problemas técnicos en la firma de los mensajes. Debemos reprocesar todos los votos sin dejar de mostrar datos.

1. ¿Qué elementos habría que modificar, siguiendo la arquitectura Kappa?
2. ¿Qué modificaciones necesitan los topics, las particiones y los Consumer Groups?
