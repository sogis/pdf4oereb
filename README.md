# oereb-xml2pdf

## TODO:
- ConverterTest -> functionalTest
- Multiproject
- web service
- Dockerimage
- Haftungsausschluss ist unbekannt, ob es sich sauber verhält wenn es mehrere davon gibt (oder sehr lange).
- Nach was genau gruppieren? Da braucht es wohl Absprache, da die Maschinen nicht alles wissen kann. 
  - Gruppieren nach den 17 ÖREB-Themen (tendenziell nach ROL/Theme/Text/Text) und irgendwie auch nach Subthema, falls vorhanden und ungleich Theme/Text/Text. Ist aber auch Heuristik.
  - siehe PDF-Weisung: Thema/Text muss ja der Codeliste entsprechen, wäre also spezifiert.
- Unit-Tests für die Extension-Functions von Saxon.
- Multilanguage

## Bugs / Fragen?
- Können im Auszug mehrere Sprachen vorkommen? 


- BL: 
  - interior ring == exterior ring of real estate geometry
  - not 300 dpi images ACHTUNG: die müssen gar nicht 300dpi aufweisen? Bravo.
  - LengthShare nur im extensions-Element
- ZH:
  - nicht 300dpi images (embedded wie auch WMS)
  - Georeferenzierung für Image fehlt.
  - "java.lang.IllegalArgumentException: Illegal base64 character a" beim Versuch PlanForLandRegisterMainPage zu encodieren.
  - WMS-GetMap liefert die Bandierung des Grundstückes mit. Ist das korrekt?
  - Grundbuchkreis fehlt im XML? (im PDF vorhanden)
  - Beteiligte Symbole sind nochmals unter OtherLegend. So wie ich Weisung lese, dürfen die nicht nochmals auftreteten. Einigermassen plausibel.
  - Zuständige Stellen auf dem PDF kommen von den zuständigen Stellen der Dokumente (? oder irgendwie anders gemacht). Es fehlt das ARE bei den zuständigen Stellen direkt beim ÖREB. M.E. sollten die Stellen von dort verwendet werden.
  - java.net.URISyntaxException: Illegal character in authority at index 7: http://www.raumplanung.zh.ch
  - Linien haben AreaShare und PartInPercent von 0. Jedoch keine LineShare.
  - Symbole haben nicht das korrekte Width/Height-Verhältnis.
  - Falls es zwei ÖREB des gleichen Typs im Grundstück gibt, fehlt einer (?). Z.B. Freihaltezone in meinem Beispiel. 
  - Die WMS-Requests der ÖREB beinhalten auch schon alles (highlighting und so.)
  - layerOpacity ist immer 0 (falls komplett unsichtbar entspricht)
- NW:
  - information text of restriction on landownership is wrong
  - symbols for restriction on landownership appear also in other legend
  - naming / grouping of theme ?
  - URL encoding
  - legend symbols size?
- VS:
 - TypeCode = unknown -> Wird beim Aggregieren/Gruppieren der Symbole Probleme machen, falls das mehrfach vorkommt.
 - GetExtractByIdResponse-Tag fehlt. Es beginnt beim Extract. (?)