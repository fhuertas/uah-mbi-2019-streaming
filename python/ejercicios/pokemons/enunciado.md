# Consumer-Producer API

El objetivo de este ejercicio es practicar con la API de Consumer y Producer de Kafka, así como tomar decisiones de diseño sencillas. Simularemos varios producers y varios consumers, algunos de éstos compartiendo el mismo Consumer Group.

De una manera extremadamente simplificada, intentaremos modelar parte del comportamiendo del famoso Pokemo Go. La arquitectura tendrá los siguientes componentes:
- Uno o varios generadores de Pokemons (pseudoaleatorio, cada pokémon aparecerá en una latitud/longitud cercana a dos puntos predefinidos).
- Uno o varios generador de logins de usuarios.
- Uno o varios contadores del número de usuarios que han iniciado sesión y de los pokemons que han aparecido.
- Uno o varios "controladores de autenticación" que manden un email a cada usuario cuando inicie sesión.
- Varios paneles de control de usuario que verán los pokemons que aparecen cerca de su zona.
- Kafka como desacoplador de los flujos de mensajes.

1. De los elementos anteriores, ¿cuáles serían consumers y cuáles serían producers?
2. ¿Cuántos topics necesitaríamos? ¿Necesitan una clave?
3. ¿Sobre qué topics produciría cada producer, y de qué topics leería cada consumer.
4. El enunciado indica "uno o varios" para poder expresar que puede haber varias instancias o procesos de un mismo producer o consumer:
   1. ¿En qué casos disponer de varios procesos facilita la alta disponibilidad?
   2. ¿En qué casos es necesario disponer de varios procesos para permitir una funcionalidad concreta?
5. ¿Cuántas particiones deberían tener los topics? ¿Cómo afecta esto al número de procesos que podemos desplegar?   
6. ¿Cuántos Consumer Groups necesitaríamos?

Y ahora a programar. Vamos a restringir  un poco más los requisitos para enfocar la implentación.
- El generador de Pokemons debe escogerlos aleatoriamente del CSV y emitir un JSON con dos campos adicionales, `lat` y `lon`. Pueden aparecer repetidos.
- El generador de usuarios debe emitir eventos de login y logoff. Hay dos opciones:
  - Emitir el usuario entero en ambos eventos, con un campo de `action` que sea `login` y `logoff`.
  - Emitir el ID de usuario como clave, y como valor incluir el usuario completo para el `login` y emitir un valor `None` para el `logoff`.
- Simplificaremos el dashboard para que no use ventanas. Mantendrá un estado interno de los pokemons que han aparecido y los usuarios que hay online en cada monento.
- El controlador de autenticación simplemente tiene que indicar por pantalla que va a enviar un mensaje de correo.
- Un usuario está identificado por su latitud y longitud. Por tanto, abrir un panel de control de usuario no va a generar un evento de login (principalmente por simplificar el código y para poder tener más eventos de login que paneles de control). No obstante, será necesario generar un ID (que no tiene por qué coincidir con ninguno de los IDs del generador de logins) para que cada usuario disponga de un consumer group.



Aquí tenemos unos cuantos comandos que pueden ser útiles para hacer troubleshooting:
```
kafka-topics --list --zookeeper localhost
kafka-console-consumer --bootstrap-server localhost:9092 --topic users --from-beginning
kafka-topics --zookeeper localhost --topic users --alter --partitions 2
```
