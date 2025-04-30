# Compiladores

Colección de programas para la clase de Compiladores en el periodo 2025A.


### Requisitos

* Java SDK 8+
* Graphviz

### Pasos previos

```sh
mkdir build
```

## 1. Primer analizador lexicográfico

Programa para simular un analizador lexicográfico para un lenguaje establecido.
El lenguaje está descrito en el diagrama `lenguaje.dot` con [Graphviz](https://graphviz.org/doc/info/lang.html).

![Diagrama del lenguaje](imagenes/lenguaje.svg)

Para generar la imagen de los diagramas ejecuta el siguiente comando.

```sh
dot -Tsvg lenguaje.dot > imagenes/lenguaje.svg
```

### Compilar
```sh
javac com/compiladores/primero/Primero.java -d build
```

### Ejecutar
```sh
java -classpath build com.compiladores.primero.Primero <directorio/hacia/programa.p1>
```

## 2. Parser recursivo descendente

### Compilar
```sh
javac com/compiladores/segundo/Segundo.java -d build
```

### Ejecutar
```sh
java -classpath build com.compiladores.segundo.Segundo <directorio/hacia/programa.p2>
```

## 3. Parser LL(1)

### Compilar
```sh
javac com/compiladores/tercero/Tercero.java -d build
```

### Ejecutar
```sh
java -classpath build com.compiladores.tercero.Tercero <directorio/hacia/programa.p2>
```

## 4. Parser SLR(1)

### Compilar
```sh
javac com/compiladores/cuarto/Cuarto.java -d build
```

### Ejecutar
```sh
java -classpath build com.compiladores.cuarto.Cuarto <directorio/hacia/programa.p2>
```
