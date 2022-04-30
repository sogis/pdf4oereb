# pdf4oereb 

_pdf4oereb_ transforms a PLR DATA-Extract XML document version 2 into the corresponding PDF document.

It uses XSLT (with some extension functions) and XSL-FO for creating the PDF.

For DATA-Extract version 1 see branch V1_0.

## Features
* Java library
* Java stand alone program

##  License

_pdf4oereb_ is licensed under the [MIT License](LICENSE).

## Status

_pdf4oereb_  is in development state.

## System Requirements

For the current version of _pdf4oereb_ , you will need a JRE (Java Runtime Environment) installed on your system, version 11 or later.

## Developing

_pdf4oereb_ is a Gradle project.

### Testing

NOT YET IMPLEMENTED: The app has some special "wms" tests which will only be run if you explicitly run the task:

```
./gradlew clean app:test app:wmsTest
```

These tests require a running external web map service.

### Building

Building the app: `./gradlew clean app:build app:assembleDist`.

## Running

### Standalone program

Unzip / untar distribution.

Get program options:
```
./bin/pdf4oereb --help
```

Transform a XML to PDF:
```
./bin/pdf4oereb --xml CH567107399166_geometry_images.xml --out /Users/stefan/tmp/
```

Transform a XML to FO:
```
./bin/pdf4oereb --fo --xml CH567107399166_geometry_images.xml --out /Users/stefan/tmp/
```

## Details
### Extension functions
Die Bibliothek verwendet XSLT (Saxon) und XSL-FO (Apache FOP) für die Umwandlung des XML-Dokumentes in die PDF-Datei. Für wenige Aspekte müssen sogenannte "extension functions" geschrieben werden:

- Decodierung von URL: `http%3A%2F%2Fwww.binningen.ch` -> `http://www.binningen.ch`
- Apache FOP bekundet (?) Mühe beim Rendern/Anzeigen von 8bit-PNG-Dateien. Bilder können mit einer extension function in 24bit-PNG-Dateien umgewandelt werden.
- Herstellung eines "Overlay-Images" mit der Grundstückbandierung und dem Massstabsbalken und dem Nordpfeil (falls die Informationen im XML vorhanden sind).
- Zusammenfügen der Bilder (Titelseite und pro ÖREB-Thema).

Ein Standalone-Programm für die Umwandlung der XML-Datei in die PDF-Datei wäre nicht nötig, da für beide Schritte jeweils bereits ein Standalone-Programm existiert:

XML -> FO: `java -jar saxon-he-10.6.jar -s:CH567107399166_geometry_images.xml -xsl:oereb_extract.xslt -o:CH567107399166_geometry_images.fo`

FO -> PDF: `fop -fo CH567107399166_geometry_images.fo -pdf CH567107399166_geometry_images.pdf -c fop.xconf`

Die Herausforderung ist in diesem Fall aber die Verwendung der extension functions im ersten Schritt. Diese müssen im CLASSPATH vorhanden sein und zusätzlich müssen sie in einem Config-File registriert werden. Es können jedoch nur Funktionen registriert werden, die ein bestimmtes Interface implementierten. Für das einfachere Interface (hier verwendet) gibt es diese Möglichkeit nicht. Aus diesem Grund wurde ein simples zusätzliches Standalone-Programm geschrieben.

### Embedded Images
Es wird zuerst immer versucht die eingebetteten Kartenausschnitte, Logos und Symbole zu verwenden. Nur wenn diese nicht vorhanden sind, wird versucht die die referenzierten Objekte zu laden und zu verwenden.

### Multilanguage
Eine XSLT-Transformation ist gut parametrisierbar. Statische Texte (z.B. der Titel des Auszuges usw.) können in eigenen Sprachdateien verwaltet werden und entsprechend geladen werden. Diese Texte stammen aus einem noch nicht offiziellen Teilmodell: https://www.cadastre.ch/content/cadastre-internet/de/manual-oereb/modell/frame/_jcr_content/contentPar/tabs_copy_copy_copy_/items/dokumente/tabPar/downloadlist_269503579/downloadItems/90_1646229667044.download/OeREBKRM_Vx_x_AuszugTexte_20220301.zip. Meines Erachtens haben sich aber noch ein paar wenige Fehler eingeschlichen.

Die eigentlichen Auszugsinhalte sind per Definition mehrsprachig. Die Sprache wird der Transformation als Parameter übergeben (default=de). Bei jedem Multilingual-Element wird diese Sprache im XML gesucht. Falls sie nicht vorhanden, wird die erste vorhandene Sprache gewählt. In den Extension Functions ist die analoge Logik eingebaut. Was momentan nicht geht, ist z.B. französische statische Text und italienische Auszugsinhalte (falls diese vorhanden wären). Sowohl für statische Texte wie auch für die Auszugsinhalte wird der gleiche Sprachen-Parameter verwendet.

### Gruppierung und Sortierung der Themen
Die Gruppierung und Sortierung der Themen funktioniert nun in OEREB V2_0 generisch und korrekt.

### Overlay-Image
Das Overlay-Image mit der Grundstücksbandierung, dem Nordpfeil und dem Massstabsbalken verwendet die gleiche Auflösung wie die eingebetteten Kartenausschnitte resp. WMS-GetMap-Request. Falls die Auflösung gering ist, sehen die drei Objekte auch bescheiden aus.

