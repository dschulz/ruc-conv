# ruc-conv

## Aplicación de escritorio para depurar listado de RUCs de la SET.

Este experimento fue inicialmente una prueba para jugar con un feature de versiones recientes de Java (_Virtual Threads_).
Las pruebas con _Virtual Threads_ fueron aburridas y terminaron en este rejunte que usé para poder cocinar una base de datos `SQLite3` con el listado de RUCs razonablemente limpio.

## ¿Qué hace la aplicación?

![Captura de pantalla](https://github.com/dschulz/ruc-conv/blob/main/screenshot.png?raw=true)


1. Recibe una lista de archivos Zip ([los que publica la SET](https://www.set.gov.py/portal/PARAGUAY-SET/InformesPeriodicos?folder-id=repository:collaboration:/sites/PARAGUAY-SET/categories/SET/Informes%20Periodicos/listado-de-ruc-con-sus-equivalencias)).
2. Abre los archivos de texto dentro del zip, recorre el contenido y extrae, línea por línea, cada entrada.
3. Los registros que tienen estado `CANCELADO` se descartan, solo se conservan los que tienen estado `ACTIVO` o `SUSPENSION TEMPORAL`.
4. Cada entrada es comparada con [una lista ridículamente larga de expresiones regulares](https://github.com/dschulz/ruc-conv/blob/533484d81a4ebe4baefa75e859bfa83dfa6a50c6/src/main/java/com/dschulz/rucconv/task/ObtenerContribuyentesDesdeZipTask.java#L30) para encontrar inconsistencias y si se puede, se remienda de una manera no destructiva.
5. Se conserva la denominación original y en una columna separada la denominación remendada (si coincidió con uno de los patrones de texto).
6. La lista queda cargada en memoria y exhibida. Es cuando se puede exportar a varios formatos (SQL, JSON, CSV, etc.).

## ¿Qué cosas se corrigen en el listado de RUCs?

A la fecha de hoy (Abril 2023) hay +10.000 entradas defectuosas que contienen caracteres extraños que resultaron de prácticas descuidadas en el manejo de datos por parte de la SET (conversión de codificación de caracteres). Este tipo de errores ocurre comunmente cuando un idiota está a cargo de exportar/importar bases de datos.

También hay una cantidad enorme de nombres con errores obvios de tipeo y puntuación inconsistente, que parecen haber sido registrados por un gorila drogado.

Aproximadamente `650 mil` de un total de `1.032.969` registros son remendados a una versión mejorada.

## ¿Por qué es importante corregir los nombres? ¿Por qué dedicarle tiempo a esto?

Algunas razones:

- Las personas y razones sociales tienen nombre propio por una razón. Los nombres propios se deben escribir bien, y con mas razón si se trata de una institución estatal.
- Es 2023, `Unicode 2.0` existe desde _1996_. Nada puede justificar que en miles de sistemas de gestión haya nombres de personas de apellido QUIÑÓNEZ, ACUÑA o NÚÑEZ aparezcan como `"QUIÃ¿Â¿¿ONEZ"`, `"ACUÃ¿â¿¿A"` o `"NUÃ¿â¿¿EZ"`. No solo es ridículo y vergonzoso, también es irrespetuoso.
- La SET publica la misma basura desde que recuerdo y no tiene sentido esperar que arreglen lo que publican.

## ¿A quién le importa? Las impresoras matriciales igual imprimen mal las palabras con diacríticos y acentos.

A mi me importa. Si no podés hacer que una impresora matricial imprima correctamente caracteres acentuados tenés algunas opciones:

- Leer el manual de la impresora.
- Usar una funcionalidad que normalice el texto para obtener caracteres no acentuados, por ejemplo, [la extensión `unaccent` de PostgreSQL](https://www.postgresql.org/docs/current/unaccent.html).
- Delegar la tarea a una persona menos mediocre.

## ¿Se corrigen _todos_ los errores?

No, no se corrigen todos los errores. Y hay errores que no se pueden corregir porque la información es insuficiente.
Falta agregar mas patrones de texto para identificar nombres defectuosos. Es un trabajo que consume tiempo, es propenso a errores y no es tan divertido.

Los nombres, apellidos y palabras que deben ser acentuados se sustituyen por versiones corregidas que se mantienen en el código fuente en una tabla de sustituciones. Esto no es muy práctico porque la tabla de sustituciones siempre va a ser incompleta. Pero no hay una mejor manera, los errores deben corregirse en origen.

También hay menos de una docena de entradas que se descartan al cargar los zips originales de la SET porque contienen una cantidad errada de columnas.

## ¿Por qué no usaste un parser de CSV?

Inicialmente usé la librería Apache `commons-csv`, pero no aporta nada. Además es una librería Java sin descriptor de módulo, por lo que hace complicado incluir en un proyecto Java modular que se pretenda construir con `jlink` para obtener un paquete distribuible con el JRE incluido.

## ¿No hay una manera menos aparatosa de hacer lo mismo?

Claro que si. Una opción razonable es implementar la misma cosa en un simple script en [Python](https://www.python.org/).
Pero hay maneras mas aparatosas, por ejemplo como un componente para [Apache Camel](https://camel.apache.org/components/3.20.x/bean-component.html) para que funcione como un patrón de integración automatizado.

## Lo que falta hacer

* Agregar GitHub Actions para construir paquetes distribuibles.
* El SQL exportado para el dialecto SQL de Oracle no fue probado y lo mas probable es que no funcione sin modificar a mano.
* Agregar mas patrones de texto para corregir mas entradas defectuosas.
* Recuperar registros que se descartan durante el parseo. Son alrededor de 10.
* Implementar esto como un backend y no como una aplicación de escritorio.

# Construir paquete binario
Se necesita tener un JDK versión `20` instalado. Cualquier distribución debería funcionar indistintamente.

* [Oracle](https://www.oracle.com/java/technologies/downloads/)
* [Eclipse Temurin](https://adoptium.net/es/temurin/releases/?version=20)
* Cualquier otra siempre que sea 20+

Se puede construir la aplicación usando [jlink](https://docs.oracle.com/javase/9/tools/jlink.htm) de manera que no se necesita tener Java instalado en el sistema donde se vaya a usar.

## Linux / macOS

```
./mvnw clean verify javafx:jlink

```

## Windows

```
mvnw.bat clean verify javafx:jlink
```

Al terminar debe haber un paquete zip `ruc-conv-${version}.zip` en el directorio `target`. Este paquete es distribuible y contiene la aplicación enlazada con un `JRE` incluido. 
El paquete se puede descomprimir en cualquier directorio y la aplicación se puede ejecutar directamente desde ahí o creando un acceso directo al ejecutable.
En Linux, el ejecutable se llama `rucconv` y se encuentra en el directorio `bin/`. 
En Windows, el ejecutable es un script con nombre `rucconv.bat` y se encuentra en el directorio `\bin`.



## Autor

Diego Schulz
`dschulz en gmail.com`


