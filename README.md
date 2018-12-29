# pdf4oereb 

_pdf4oereb_ transforms a PLR DATA-Extract XML document into the corresponding in PDF document.

It uses XSLT (with some extension functions) and XSL-FO for creating the PDF.

## Features
* Java library
* Java stand alone program (aka fatjar)
* Spring Boot web service (incl. a simple GUI)

##  License

_pdf4oereb_ is licensed under the [MIT License](LICENSE).

## Status

_pdf4oereb_  is in development state.

## System Requirements

For the current version of _pdf4oereb_ , you will need a JRE (Java Runtime Environment) installed on your system, version 1.8 or later.

## Developing

_pdf4oereb_ is a Gradle multiproject. `library` contains the code for the library and the stand alone programm and `web-service` is the Spring Boot application.

### Testing

The library has some special "wms" tests which will only be run if you explicitly run the task:

```
./gradlew library:test library:wmsTest
```

These tests require a running external web map service.

The web service has only some functional / integration tests and no unit tests.

### Building

Building the library: `./gradlew clean library:build` or `./gradlew clean library:jar`.

Building the standalone program: `./gradlew clean library:build library:customFatJar`. The executable fat-jar (`pdf4oereb-fat-VERSION.jar`) can be found in `./build/libs/`.

Building the Spring Boot application: `./gradlew clean web-service:build`.

A Docker image of the web service can also be built: `./gradlew clean web-service:build web-service:buildDockerImage`.

## Running

### Standalone program

Get program options:
```
java -jar pdf4oereb-fat-0.1.0-SNAPSHOT.jar 
```

Transform a XML to PDF:
```
java -jar pdf4oereb-fat-0.1.0-SNAPSHOT.jar --xml CH567107399166_geometry_images.xml --out /Users/stefan/tmp/
```

Transform a XML to FO:
```
java -jar pdf4oereb-fat-0.1.0-SNAPSHOT.jar --fo --xml CH567107399166_geometry_images.xml --out /Users/stefan/tmp/
```

### Web service:

Start web service from within the source directory:
```
./gradlew clean web-service:bootRun
```

Or build the executable jar first and run the jar:
```
./gradlew clean web-service:build
```
```
java -jar ./web-service/build/libs/pdf4oereb-web-service-0.1.0-SNAPSHOT.jar
```

You can either use to GUI for uploading files or just send the files by using `curl`:
```
curl -v -XPOST -F file=@CH567107399166_geometry_images.xml "http://localhost:8888/pdf4oereb/" -o CH567107399166_geometry_images.pdf
```

If you want to use the Docker image:
```
docker run -it --rm --name pdf4oereb-web-service -p 8888:8888 sogis/pdf4oereb-web-service:latest
```

## Examples
### BL
[CH567107399166_geometry_images.xml](tree/library/src/test/bl/CH567107399166_geometry_images.xml) -> [CH567107399166_geometry_images.pdf](tree/library/src/test/bl/CH567107399166_geometry_images.pdf)

### ZH
[CH282399917939_geometry_wms.xml](tree/library/src/test/zh/CH282399917939_geometry_images.xml) -> [CH282399917939_geometry_wms.pdf](tree/library/src/test/zh/CH282399917939_geometry_wms.pdf)


## Details
### Extension functions
Die Bibliothek verwendet XSLT (Saxon) und XSL-FO (Apache FOP) für die Umwandlung des XML-Dokumentes in die PDF-Datei. Für wenige Aspekte müssen sogenannte "extension functions" geschrieben werden:

- Decodierung von URL: `http%3A%2F%2Fwww.binningen.ch` -> `http://www.binningen.ch`
- Apache FOP bekundet (?) Mühe beim Rendern/Anzeigen von 8bit-PNG-Dateien. Bilder können mit einer extension function in 24bit-PNG-Dateien umgewandelt werden.
- Herstellung eines "Overlay-Images" mit der Grundstückbandierung und dem Massstabsbalken und dem Nordpfeil (falls die Informationen im XML vorhanden sind).
- Zusammenfügen der Bilder (Titelseite und pro ÖREB-Thema).

Ein Standalone-Programm für die Umwandlung der XML-Datei in die PDF-Datei wäre nicht nötig, da für den jeweiligen Schritt bereits ein Standalone-Programm existiert:

XML -> FO: `java -jar saxon9he.jar -s:CH567107399166_geometry_images.xml -xsl:oereb_extract.xslt -o:CH567107399166_geometry_images.fo`

FO -> PDF: `fop -fo CH567107399166_geometry_images.fo -pdf CH567107399166_geometry_images.pdf -c fop.xconf`

Die Herausforderung ist in diesem Fall aber die Verwendung der extension functions im ersten Schritt. Diese müssen im CLASSPATH vorhanden sein und zusätzlich müssen sie in einem Config-File registriert werden. Es können jedoch nur Funktionen registriert werden, die ein bestimmtes Interface implementierten. Für das einfachere Interface (hier verwendet) gibt es diese Möglichkeit nicht. Aus diesem Grund wurde ein simples zusätzliches Standalone-Programm geschrieben.

### Auszugs-Flavour
Es wird zur Zeit nur `reduced` unterstützt. Auf die Zeile mit der Beglaubigung wird auf verzichtet. Beides ist wohl (?) am Nachhaltigsten.

### Embedded Images
Es wird zuerst immer versucht die eingebetteten Kartenausschnitte, Logos und Symbole zu verwenden. Nur wenn diese nicht vorhanden sind, wird versucht die die referenzierten Objekte zu laden und zu verwenden.

### Multilanguage
Eine XSLT-Transformation ist gut parametrisierbar. Statische Texte (z.B. der Titel des Auszuges usw.) können in eigenen Sprachdateien verwaltet werden und entsprechend geladen werden. Die eigentlichen Auszugsinhalte sind per Definition mehrsprachig und die gewünschte Sprache (falls im XML vorhanden), kann ebenfalls mit XSLT-/XPath-Bordmitteln gewählt werden. Für die statischen Texte ist bereits rudimentärer (proof-of-concept) Support vorhanden. Die Option ist nicht im Standalone-Programm verfügbar, nur in der Bibliothek und im Web service.

### Gruppierung
Es wird momentan einzig nach ÖREB-Thema-Code (`RestrictionOnLandownership.Thema.Code`) gruppiert. Gruppiert heisst in diesem Kontext: Sämtliche `<RestrictionOnLandownership>`-Elemente des gleichen Themas erscheinen auf der gleichen PDF-Seite. Was nicht zu sinnvollen Ergebnissen führt, ist die Verwendung des gleichen Codes mit unterschiedlichem Text.

Ziel ist die Unterstützung von Subthemen: Zuerst wird nach Hauptthema gruppiert und falls innerhalb des Hauptthemas Subthemen vorhanden sind, wird nach diesen gruppiert. 

### Overlay-Image
Das Overlay-Image mit der Grundstücksbandierung, dem Nordpfeil und dem Massstabsbalken verwendet die gleiche Auflösung wie die eingebetteten Kartenausschnitte resp. WMS-GetMap-Request. Falls die Auflösung gering ist, sehen die drei Objekte auch bescheiden aus.