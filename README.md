# oereb-xml2pdf

## TODO:
- Haftungsausschluss ist unbekannt, ob es sich sauber verhält wenn es mehrere davon gibt (oder sehr lange).
- Images from Ref.
- Scalebar und Nordpfeil.
- Nach was genau gruppieren? Da braucht es wohl Absprache, da die Maschinen nicht alles wissen kann. 
  - Gruppieren nach den 17 ÖREB-Themen (tendenziell nach ROL/Theme/Text/Text) und irgendwie auch nach Subthema, falls vorhanden und ungleich Theme/Text/Text. Ist aber auch Heuristik.
  - siehe PDF-Weisung: Thema/Text muss ja der Codeliste entsprechen, wäre also spezifiert.
- Unit-Tests für die Extension-Functions von Saxon.
- Multilanguage

## Bugs?
- BL: 
  - interior ring == exterior ring of real estate geometry
  - not 300 dpi images
  - LengthShare nur im extensions-Element
- NW:
  - information text of restriction on landownership is wrong
  - symbols for restriction on landownership appear also in other legend
  - naming / grouping of theme ?
  - URL encoding
  - legend symbols size?