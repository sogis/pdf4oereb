<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:gml="https://www.opengis.net/gml/3.2"  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:extract="http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract" xmlns:data="http://schemas.geo.admin.ch/V_D/OeREB/1.0/ExtractData" exclude-result-prefixes="gml xlink extract data" version="3.0">
  <xsl:output method="xml" indent="yes"/>
  <xsl:decimal-format name="swiss" decimal-separator="." grouping-separator="'"/>  
  <xsl:template match="extract:GetExtractByIdResponse/data:Extract">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsd="https://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="mainPage" page-height="297mm" page-width="210mm" margin-top="10mm" margin-bottom="12mm" margin-left="18mm" margin-right="18mm"> 
          <fo:region-body margin-top="30mm" background-color="yellow"/>
          <fo:region-before extent="30mm" background-color="transparent"/>
          <fo:region-after extent="3mm" background-color="khaki"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
    <fo:page-sequence master-reference="mainPage" id="page-sequence-id">
        <fo:static-content flow-name="xsl-region-before">
          <fo:block>
            <fo:block-container absolute-position="absolute" top="0mm" left="0mm" background-color="transparent">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:external-graphic width="44mm" content-width="scale-to-fit" >
                  <xsl:attribute name="src">
                    <xsl:text>url('data:</xsl:text>
                    <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="data:FederalLogo"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </fo:external-graphic>
              </fo:block>
            </fo:block-container>

            <fo:block-container absolute-position="absolute" top="0mm" left="60mm" background-color="transparent">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:external-graphic border="0pt solid black" width="30mm" height="13mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" text-align="center">
                  <xsl:attribute name="src">
                    <xsl:text>url('data:</xsl:text>
                    <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="data:CantonalLogo"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </fo:external-graphic>
              </fo:block>
            </fo:block-container>

            <fo:block-container absolute-position="absolute" top="0mm" left="95mm" background-color="transparent">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:external-graphic width="30mm" height="13mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" text-align="center">
                  <xsl:attribute name="src">
                    <xsl:text>url('data:</xsl:text>
                    <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="data:MunicipalityLogo"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </fo:external-graphic>
              </fo:block>
            </fo:block-container>

            <fo:block-container absolute-position="absolute" top="0mm" left="139mm" background-color="transparent">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:external-graphic width="35mm" height="10mm" scaling="non-uniform" content-width="scale-to-fit" content-height="scale-to-fit">
                  <xsl:attribute name="src">
                    <xsl:text>url('data:</xsl:text>
                    <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="data:LogoPLRCadastre"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </fo:external-graphic>
              </fo:block>
            </fo:block-container>

            <fo:block-container absolute-position="absolute" top="19mm" left="0mm">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
            <fo:block-container absolute-position="absolute" top="0mm" left="0mm">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.8pt"/>
              </fo:block>
              <fo:table table-layout="fixed" width="100%" margin-top="0.5mm" font-size="6.5pt" font-weight="400" font-family="Cadastra">
                <fo:table-column column-width="50%"/>
                <fo:table-column column-width="50%"/>
                <fo:table-body>
                  <fo:table-row vertical-align="bottom">
                    <fo:table-cell vertical-align="bottom">
                      <fo:block vertical-align="bottom">
                        <xsl:value-of select="format-dateTime(data:CreationDate,'[D01].[M01].[Y0001]')"/><fo:inline padding-left="1em"><xsl:value-of select="format-dateTime(data:CreationDate,'[H01]:[m01]:[s01]')"/></fo:inline><fo:inline padding-left="1em"><xsl:value-of select="data:ExtractIdentifier"/></fo:inline>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right">
                      <fo:block>Seite <fo:page-number/>/<fo:page-number-citation-last ref-id="page-sequence-id"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>

            </fo:block-container>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <!--font size should be 18pt but that seems to large and will lead to non-directive conform line break behaviour-->
          <fo:block-container height="28mm" background-color="green">
            <fo:block line-height="21pt" linefeed-treatment="preserve" font-weight="700" font-size="17.7pt" font-family="Cadastra">Auszug aus dem Kataster der&#x000A;öffentlich-rechtlichen Eigentumsbeschränkungen&#x000A;(ÖREB-Kataster)</fo:block>
          </fo:block-container>            
          <!--<xsl:apply-templates/>-->

            <fo:block-container height="109mm" background-color="gold">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:external-graphic border="0.2pt solid black" width="174mm" height="99mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit">
                  <xsl:attribute name="src">
                    <xsl:text>url('data:</xsl:text>
                    <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="data:RealEstate/data:PlanForLandRegisterMainPage"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </fo:external-graphic>
              </fo:block>
            </fo:block-container>

            <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="orange">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-width="68mm"/>
                <fo:table-column column-width="106mm"/>
                <fo:table-body>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block font-weight="700">Grundstück-Nr.</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block font-weight="700"><xsl:value-of select="data:RealEstate/data:Number"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block>E-GRID</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="data:RealEstate/data:EGRID"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block>Gemeinde (BFS-Nr.)</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="data:RealEstate/data:Municipality"/> (<xsl:value-of select="data:RealEstate/data:FosNr"/>)</fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block>Grundbuchkreis</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="data:RealEstate/data:SubunitOfLandRegister"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block>Fläche</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block line-height-shift-adjustment="disregard-shifts"><xsl:value-of select="format-number(data:RealEstate/data:LandRegistryArea, &quot;#'###&quot;, &quot;swiss&quot;)"/> m<fo:inline baseline-shift="super" font-size="60%">2</fo:inline></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

            <fo:block-container margin-top="10mm" font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="aquamarine">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-width="68mm"/>
                <fo:table-column column-width="106mm"/>
                <fo:table-body>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block font-weight="700">Auszugsnummer</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block font-weight="700"><xsl:value-of select="data:ExtractIdentifier"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block>Erstellungsdatum des Auszugs</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="format-dateTime(data:CreationDate,'[D01].[M01].[Y0001]')"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block>Katasterverantwortliche Stelle</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="data:PLRCadastreAuthority/data:Name/data:LocalisedText/data:Text"/>, <xsl:value-of select="data:PLRCadastreAuthority/data:Street"/><xsl:text> </xsl:text><xsl:value-of select="data:PLRCadastreAuthority/data:Number"/>, <xsl:value-of select="data:PLRCadastreAuthority/data:PostalCode"/><xsl:text> </xsl:text><xsl:value-of select="data:PLRCadastreAuthority/data:City"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

          <fo:block-container height="13mm" background-color="green">
            <fo:block page-break-before="always" line-height="18pt" linefeed-treatment="preserve" font-weight="700" font-size="15pt" font-family="Cadastra">Übersicht ÖREB-Themen</fo:block>
          </fo:block-container>            

          <fo:block-container background-color="indianred">
            <fo:block line-height="11.5pt" linefeed-treatment="preserve" font-weight="700" font-size="8.5pt" font-family="Cadastra">Eigentumsbeschränkungen, welche das Grundstück <xsl:value-of select="data:RealEstate/data:Number"/> in <xsl:value-of select="data:RealEstate/data:Municipality"/> betreffen</fo:block>
          </fo:block-container>            

            <fo:block-container>
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>

            <fo:block-container margin-bottom="10mm" font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="orange">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-width="7mm"/>
                <fo:table-column column-width="167mm"/>
                <fo:table-body>
                  <fo:table-row vertical-align="middle">
                    <fo:table-cell>
                      <fo:block margin-top="1mm" margin-bottom="3.8mm" font-weight="700" font-size="6.5pt">Seite</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block/>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle">
                    <fo:table-cell height="30mm" >
                      <fo:block font-weight="400" font-size="6.5pt">Fubar</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block/>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

          <fo:block-container background-color="indianred">
            <fo:block line-height="11.5pt" linefeed-treatment="preserve" font-weight="700" font-size="8.5pt" font-family="Cadastra">Eigentumsbeschränkungen, welche das Grundstück nicht betreffen</fo:block>
          </fo:block-container>            

            <fo:block-container margin-bottom="1mm">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>

            <fo:block-container margin-bottom="10mm" font-weight="400" font-size="8.5pt" line-height="11.5pt" font-family="Cadastra" background-color="orange">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-body>
                  <xsl:for-each select="data:NotConcernedTheme">
                    <xsl:sort data-type="number" order="ascending" select="(number(data:Code='LandUsePlans') * 1) + (number(data:Code='MotorwaysProjectPlaningZones') * 2) + (number(data:Code='MotorwaysBuildingLines') * 3) + (number(data:Code='RailwaysProjectPlanningZones') * 4) + (number(data:Code='RailwaysBuildingLines') * 5) + (number(data:Code='AirportsProjectPlanningZones') * 6) + (number(data:Code='AirportsBuildingLines') * 7) + (number(data:Code='AirportsSecurityZonePlans') * 8) + (number(data:Code='ContaminatedSites') * 9) + (number(data:Code='ContaminatedMilitarySites') * 10) + (number(data:Code='ContaminatedCivilAviationSites') * 11) + (number(data:Code='ContaminatedPublicTransportSites') * 12) + (number(data:Code='GroundwaterProtectionZones') * 13) + (number(data:Code='GroundwaterProtectionSites') * 14) + (number(data:Code='NoiseSensitivityLevels') * 15) + (number(data:Code='ForestPerimeters') * 16) + (number(data:Code='ForestDistanceLines') * 17)"/>
                    <fo:table-row vertical-align="middle">
                      <fo:table-cell>
                        <fo:block><xsl:value-of select="data:Text/data:Text"/></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                  </xsl:for-each>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

          <fo:block-container background-color="indianred">
            <fo:block line-height="11.5pt" linefeed-treatment="preserve" font-weight="700" font-size="8.5pt" font-family="Cadastra">Allfällige Eigentumsbeschränkungen, zu denen noch keine Daten vorhanden sind</fo:block>
          </fo:block-container>            

            <fo:block-container margin-bottom="1mm">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>

<!-- margin-bottom ist heuristisch -->
            <fo:block-container margin-bottom="20mm" font-weight="400" font-size="8.5pt" line-height="11.5pt" font-family="Cadastra" background-color="orange">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-body>
                  <xsl:for-each select="data:ThemeWithoutData">
                    <xsl:sort data-type="number" order="ascending" select="(number(data:Code='LandUsePlans') * 1) + (number(data:Code='MotorwaysProjectPlaningZones') * 2) + (number(data:Code='MotorwaysBuildingLines') * 3) + (number(data:Code='RailwaysProjectPlanningZones') * 4) + (number(data:Code='RailwaysBuildingLines') * 5) + (number(data:Code='AirportsProjectPlanningZones') * 6) + (number(data:Code='AirportsBuildingLines') * 7) + (number(data:Code='AirportsSecurityZonePlans') * 8) + (number(data:Code='ContaminatedSites') * 9) + (number(data:Code='ContaminatedMilitarySites') * 10) + (number(data:Code='ContaminatedCivilAviationSites') * 11) + (number(data:Code='ContaminatedPublicTransportSites') * 12) + (number(data:Code='GroundwaterProtectionZones') * 13) + (number(data:Code='GroundwaterProtectionSites') * 14) + (number(data:Code='NoiseSensitivityLevels') * 15) + (number(data:Code='ForestPerimeters') * 16) + (number(data:Code='ForestDistanceLines') * 17)"/>
                    <fo:table-row vertical-align="middle">
                      <fo:table-cell>
                        <fo:block><xsl:value-of select="data:Text/data:Text"/></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                  </xsl:for-each>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

            <fo:block-container>
            <fo:block>
              <fo:footnote>
                <fo:inline/>
                <fo:footnote-body>
                  <fo:block keep-together.within-column="always">
                    <fo:block-container margin-top="0mm" margin-bottom="5mm" font-weight="400" font-size="6.5pt" line-height="8.5pt" font-family="Cadastra" background-color="yellowgreen">
                      <fo:table table-layout="fixed" width="100%">
                        <fo:table-column column-width="87mm"/>
                        <fo:table-column column-width="87mm"/>
                        <fo:table-body>
                          <fo:table-row vertical-align="top">
                            <fo:table-cell padding-right="1.5mm">
                              <fo:block font-weight="700">Allgemeine Informationen</fo:block>
                              <fo:block><xsl:value-of select="data:GeneralInformation/data:LocalisedText/data:Text"/></fo:block>
                              <fo:block margin-top="2.2mm" font-weight="700">Grundlagedaten</fo:block>
                              <fo:block><xsl:value-of select="data:BaseData/data:LocalisedText/data:Text"/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-left="1.5mm">
                              <fo:block/>
                              <xsl:for-each select="data:ExclusionOfLiability">
                                <fo:block font-weight="700"><xsl:value-of select="data:Title/data:LocalisedText/data:Text"/></fo:block>
                                <fo:block><xsl:value-of select="data:Content/data:LocalisedText/data:Text"/></fo:block>
                              </xsl:for-each>
                              <fo:block margin-top="2.2mm">
                                <fo:table table-layout="fixed" width="100%">
                                  <fo:table-column column-width="35mm"/>
                                  <fo:table-column column-width="40mm"/>
                                  <fo:table-body>
                                    <fo:table-row vertical-align="top">
                                      <fo:table-cell>
                                        <fo:block font-weight="700">Um einen aktualisierten Auszug aus dem ÖREB-Kataster zu erhalten, scannen Sie bitte den QR-Code.</fo:block>
                                      </fo:table-cell>
                                      <fo:table-cell padding-left="4mm">
                                        <fo:block>
                                          <fo:external-graphic width="20mm" height="20mm" content-width="scale-to-fit" content-height="scale-to-fit">
                                            <xsl:attribute name="src">
                                              <xsl:text>url('data:</xsl:text>
                                              <xsl:text>image/png;base64,</xsl:text>
                                              <xsl:value-of select="data:QRCode"/>
                                              <xsl:text>')</xsl:text>
                                            </xsl:attribute>
                                          </fo:external-graphic>
                                        </fo:block>
                                      </fo:table-cell>
                                    </fo:table-row>
                                  </fo:table-body>
                                </fo:table>
                              </fo:block>
                            </fo:table-cell>
                          </fo:table-row>
                        </fo:table-body>
                      </fo:table>
                    </fo:block-container>
                  </fo:block>
                </fo:footnote-body>
              </fo:footnote>
            </fo:block>
            </fo:block-container>

        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <xsl:template match="data:Extract">
    <fo:block-container font-size="12pt" margin-left="5mm" margin-bottom="5mm">
      <fo:block>
           gaga
        </fo:block>
      <fo:block>
        <xsl:value-of select="data:CreationDate"/>
      </fo:block>
      <fo:block>
        <xsl:value-of select="format-dateTime(data:CreationDate, '[D01].[M01].[Y0001]')"/>
      </fo:block>
      <!-- <xsl:apply-templates select="data:CreationDate"/> -->
    </fo:block-container>
  </xsl:template>
  <!--
    <xsl:template match="data:CreationDate">
        <fo:block>
            <xsl:value-of select="."/>
        </fo:block>
        <fo:block>
           asdfasdfasdf
        </fo:block>
        
    </xsl:template>
    -->
</xsl:stylesheet>
