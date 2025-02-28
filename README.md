# Compiladores

Colección de programas para la clase de Compiladores en el periodo 2025A.


### Requisitos

* Java SDK 8+
* Graphviz

### Pasos previos

```sh
mkdir build
```

## 1. Analizador lexicográfico

Programa para simular un analizador lexicográfico para un lenguaje establecido.
El lenguaje está descrito en el diagrama `lenguaje.dot` con [Graphviz](https://graphviz.org/doc/info/lang.html).

![Diagrama del lenguaje](imagenes/lenguaje.svg)

Para generar la imagen de los diagramas ejecuta el siguiente comando.

```sh
dot -Tsvg lenguaje.dot > imagenes/lenguaje.svg
```

### Compilar
```sh
javac com/compiladores/Lexer.java -d build
```


### Ejecutar
```sh
java -classpath build com.compiladores.Lexer <directorio/hacia/programa.pro>
```
