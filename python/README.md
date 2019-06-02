# Ejercicios Python

## Spark

### Wordcount

Localización `ejercicios/spark/word_count.py`

Ejecución:
* Consola 1:
```bash
$ nc -lk 9999
```

Consola 2: 
```bash
# Lanzado contra local

s spark-submit --master local[*] ejercicios/spark/word_count.py localhost 9999

```