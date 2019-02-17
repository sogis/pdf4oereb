<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:oereb="http://oereb.agi.so.ch" xmlns:gml="https://www.opengis.net/gml/3.2" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" xmlns:extract="http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract" xmlns:data="http://schemas.geo.admin.ch/V_D/OeREB/1.0/ExtractData" exclude-result-prefixes="gml xlink extract data" version="3.0">
  <xsl:output method="xml" indent="yes"/>
  <xsl:param name="localeUrl" select="'Resources.de.resx'"/>
  <xsl:variable name="localeXml" select="document($localeUrl)/*" />
  <xsl:variable name="OverlayImage"><xsl:value-of select="oereb:getOverlayImage(extract:GetExtractByIdResponse/data:Extract/data:RealEstate/data:Limit, extract:GetExtractByIdResponse/data:Extract/data:RealEstate/data:PlanForLandRegisterMainPage)"/></xsl:variable>
  <xsl:decimal-format name="swiss" decimal-separator="." grouping-separator="'"/>  
  <xsl:template match="extract:GetExtractByIdResponse/data:Extract">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" xmlns:xsd="https://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xml:lang="en">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="mainPage" page-height="297mm" page-width="210mm" margin-top="10mm" margin-bottom="12mm" margin-left="18mm" margin-right="18mm"> 
          <fo:region-body margin-top="30mm" margin-bottom="5mm" background-color="transparent"/>
          <fo:region-before extent="30mm" background-color="transparent"/>
          <fo:region-after extent="3mm" background-color="transparent"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
    <fo:page-sequence master-reference="mainPage" id="page-sequence-id">
        <xsl:call-template name="insertHeaderAndFooter"/>
        <fo:flow flow-name="xsl-region-body">
          <!--font size should be 18pt but that seems to large and will lead to non-directive conform line break behaviour-->
          <fo:block-container height="28mm" background-color="transparent">
            <xsl:if test="data:isReduced='true'">
                <fo:block line-height="21pt" linefeed-treatment="preserve" font-weight="700" font-size="17.7pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='MainPage.TitleReduced']/value/text()"/></fo:block>
            </xsl:if>
            <xsl:if test="data:isReduced='false'">
                <fo:block line-height="21pt" linefeed-treatment="preserve" font-weight="700" font-size="17.7pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='MainPage.Title']/value/text()"/></fo:block>
            </xsl:if>            
          </fo:block-container>            

            <fo:block-container height="109mm" background-color="transparent">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:external-graphic border="0.2pt solid black" width="174mm" height="99mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" fox:alt-text="PlanForLandRegisterMainPageImage">
                  <xsl:attribute name="src">
                    <xsl:text>url('data:</xsl:text>
                    <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="oereb:createPlanForLandRegisterMainPageImage(data:RealEstate/data:PlanForLandRegisterMainPage, $OverlayImage)" />                    
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </fo:external-graphic>
              </fo:block>
            </fo:block-container>

            <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-width="68mm"/>
                <fo:table-column column-width="106mm"/>
                <fo:table-body>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='MainPage.RealEstate_DPR.Number']/value/text()"/></fo:block>
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
                      <fo:block><xsl:value-of select="$localeXml/data[@name='MainPage.Municipality_FosNr']/value/text()"/></fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="data:RealEstate/data:Municipality"/> (<xsl:value-of select="data:RealEstate/data:FosNr"/>)</fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                    <xsl:if test="data:RealEstate/data:SubunitOfLandRegister">
	                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
		                    <fo:table-cell>
		                      <fo:block><xsl:value-of select="$localeXml/data[@name='MainPage.SubunitOfLandRegister']/value/text()"/></fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell>
		                      <fo:block><xsl:value-of select="data:RealEstate/data:SubunitOfLandRegister"/></fo:block>
		                    </fo:table-cell>
	                  </fo:table-row>
                    </xsl:if>      
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="$localeXml/data[@name='MainPage.LandRegistryArea']/value/text()"/></fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block line-height-shift-adjustment="disregard-shifts"><xsl:value-of select="format-number(data:RealEstate/data:LandRegistryArea, &quot;#'###&quot;, &quot;swiss&quot;)"/> m<fo:inline baseline-shift="super" font-size="60%">2</fo:inline></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

            <fo:block-container margin-top="10mm" font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
              <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-width="68mm"/>
                <fo:table-column column-width="106mm"/>
                <fo:table-body>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='MainPage.ExtractIdentifier']/value/text()"/></fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block font-weight="700"><xsl:value-of select="data:ExtractIdentifier"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="6mm">
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="$localeXml/data[@name='MainPage.CreationDate']/value/text()"/></fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block><xsl:value-of select="format-dateTime(data:CreationDate,'[D01].[M01].[Y0001]')"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" height="6mm">
                    <fo:table-cell display-align="center">
                      <fo:block><xsl:value-of select="$localeXml/data[@name='MainPage.ResponsibleOffice']/value/text()"/></fo:block>
                    </fo:table-cell>
                    <fo:table-cell display-align="center">
                      <fo:block><xsl:value-of select="data:PLRCadastreAuthority/data:Name/data:LocalisedText/data:Text"/>, <xsl:value-of select="data:PLRCadastreAuthority/data:Street"/><xsl:text> </xsl:text><xsl:value-of select="data:PLRCadastreAuthority/data:Number"/>, <xsl:value-of select="data:PLRCadastreAuthority/data:PostalCode"/><xsl:text> </xsl:text><xsl:value-of select="data:PLRCadastreAuthority/data:City"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </fo:block-container>

          <fo:block-container height="13mm" background-color="transparent">
            <fo:block page-break-before="always" line-height="18pt" linefeed-treatment="preserve" font-weight="700" font-size="15pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='ContentPage.Title']/value/text()"/></fo:block>
          </fo:block-container>            

          <fo:block-container background-color="transparent">
            <fo:block line-height="11.5pt" linefeed-treatment="preserve" font-weight="700" font-size="8.5pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='ContentPage.ConcernedTheme_Part1']/value/text()"/><xsl:text> </xsl:text><xsl:value-of select="data:RealEstate/data:Number"/><xsl:text> </xsl:text><xsl:value-of select="$localeXml/data[@name='ContentPage.ConcernedTheme_Part2']/value/text()"/><xsl:text> </xsl:text><xsl:value-of select="data:RealEstate/data:Municipality"/><xsl:text> </xsl:text><xsl:value-of select="$localeXml/data[@name='ContentPage.ConcernedTheme_Part3']/value/text()"/></fo:block>
          </fo:block-container>            

            <fo:block-container>
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>

          <!-- print table of contents -->
          <xsl:apply-templates select="data:RealEstate" mode="toc" />

          <fo:block-container background-color="transparent">
            <fo:block line-height="11.5pt" linefeed-treatment="preserve" font-weight="700" font-size="8.5pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='ContentPage.NotConcernedTheme']/value/text()"/></fo:block>
          </fo:block-container>            

            <fo:block-container margin-bottom="1mm">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>

            <fo:block-container margin-bottom="10mm" font-weight="400" font-size="8.5pt" line-height="11.5pt" font-family="Cadastra" background-color="transparent">
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

          <fo:block-container background-color="transparent">
            <fo:block line-height="11.5pt" linefeed-treatment="preserve" font-weight="700" font-size="8.5pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='ContentPage.ThemeWithoutData']/value/text()"/></fo:block>
          </fo:block-container>            

            <fo:block-container margin-bottom="1mm">
              <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
              </fo:block>
            </fo:block-container>

            <!-- margin-bottom ist heuristisch -->
            <fo:block-container margin-bottom="10mm" font-weight="400" font-size="8.5pt" line-height="11.5pt" font-family="Cadastra" background-color="transparent">
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
                    <fo:block-container margin-top="0mm" margin-bottom="0mm" font-weight="400" font-size="6.5pt" line-height="8.5pt" font-family="Cadastra" background-color="transparent">
	                      <fo:table table-layout="fixed" width="100%">
	                        <fo:table-column column-width="87mm"/>
	                        <fo:table-column column-width="87mm"/>
	                        <fo:table-body>
	                          <fo:table-row vertical-align="top">
	                            <fo:table-cell padding-right="1.5mm">
	                              <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='ContentPage.GeneralInformation']/value/text()"/></fo:block>
	                              <fo:block><xsl:value-of select="data:GeneralInformation/data:LocalisedText/data:Text"/></fo:block>
	                              <fo:block margin-top="2.2mm" font-weight="700"><xsl:value-of select="$localeXml/data[@name='ContentPage.BaseData']/value/text()"/></fo:block>
	                              <fo:block><xsl:value-of select="data:BaseData/data:LocalisedText/data:Text"/></fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding-left="1.5mm">
	                              <fo:block/>
	                              <xsl:for-each select="data:ExclusionOfLiability">
	                                <fo:block font-weight="700"><xsl:value-of select="data:Title/data:LocalisedText/data:Text"/></fo:block>
	                                <fo:block><xsl:value-of select="data:Content/data:LocalisedText/data:Text"/></fo:block>
	                              </xsl:for-each>
	                              <fo:block margin-top="2.2mm">
                                    <xsl:if test="data:QRCode">
		                                <fo:table table-layout="fixed" width="100%">
		                                  <fo:table-column column-width="35mm"/>
		                                  <fo:table-column column-width="40mm"/>
		                                  <fo:table-body>
		                                    <fo:table-row vertical-align="top">
		                                      <fo:table-cell>
		                                        <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='ContentPage.QRCode']/value/text()"/></fo:block>
		                                      </fo:table-cell>
		                                      <fo:table-cell padding-left="4mm">
		                                        <fo:block>
		                                          <fo:external-graphic width="20mm" height="20mm" content-width="scale-to-fit" content-height="scale-to-fit" fox:alt-text="QRCode">
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
	                                </xsl:if>
	                              </fo:block>
	                            </fo:table-cell>
	                          </fo:table-row>
	                        </fo:table-body>
	                      </fo:table>
                      <fo:block/>
                    </fo:block-container>
                  </fo:block>
                </fo:footnote-body>
              </fo:footnote>
            </fo:block>
            </fo:block-container>        
        </fo:flow>
      </fo:page-sequence>
      <!-- print grouped RestrictionOnLandownership pdf pages -->
      <xsl:apply-templates select="data:RealEstate" mode="sheet"/>
      <!-- print glossary -->
      <xsl:call-template name="insertGlossary"/>
    </fo:root>
  </xsl:template>

  <!-- Template for pdf restriction on landownership pages aka "the real output". Sorting is bit of a hack. Perhaps this whole TOC thing can -->
  <!-- be made much simpler with better apply-templates strategy.-->
  <xsl:template match="data:RealEstate" mode="sheet">
    <fo:page-sequence master-reference="mainPage" id="page-sequence-id">
      <xsl:call-template name="insertHeaderAndFooter"/>
      <fo:flow flow-name="xsl-region-body">
        <fo:block>
        
            <xsl:for-each-group select="data:RestrictionOnLandownership" group-by="data:Theme/data:Code">
            <xsl:sort data-type="number" order="ascending" select="(number(starts-with(data:Theme/data:Code, 'ch')) * 0.9) + (number(data:Theme/data:Code='LandUsePlans') * 1) + (number(data:Theme/data:Code='MotorwaysProjectPlaningZones') * 2) + (number(data:Theme/data:Code='MotorwaysBuildingLines') * 3) + (number(data:Theme/data:Code='RailwaysProjectPlanningZones') * 4) + (number(data:Theme/data:Code='RailwaysBuildingLines') * 5) + (number(data:Theme/data:Code='AirportsProjectPlanningZones') * 6) + (number(data:Theme/data:Code='AirportsBuildingLines') * 7) + (number(data:Theme/data:Code='AirportsSecurityZonePlans') * 8) + (number(data:Theme/data:Code='ContaminatedSites') * 9) + (number(data:Theme/data:Code='ContaminatedMilitarySites') * 10) + (number(data:Theme/data:Code='ContaminatedCivilAviationSites') * 11) + (number(data:Theme/data:Code='ContaminatedPublicTransportSites') * 12) + (number(data:Theme/data:Code='GroundwaterProtectionZones') * 13) + (number(data:Theme/data:Code='GroundwaterProtectionSites') * 14) + (number(data:Theme/data:Code='NoiseSensitivityLevels') * 15) + (number(data:Theme/data:Code='ForestPerimeters') * 16) + (number(data:Theme/data:Code='ForestDistanceLines') * 17)"/>            
              <fo:block-container height="19mm" background-color="transparent">
                <fo:block id="{generate-id()}" page-break-before="always" line-height="18pt" linefeed-treatment="preserve" font-weight="700" font-size="15pt" font-family="Cadastra"><xsl:value-of select="data:Theme/data:Text/data:Text"/></fo:block>
              </fo:block-container>            
              <fo:block-container height="105mm" background-color="transparent">
                  <!-- get rid of duplicate images (e.g. W2 and W3 are the same images)  -->
                  <xsl:if test="data:Map/data:Image">
	                  <xsl:for-each-group select="current-group()" group-by="data:Map/data:Image">
	                  <xsl:sort order="ascending" select="data:Information/data:LocalisedText/data:Text"/>
	                    <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
		                    <fo:external-graphic border="0.2pt solid black" width="174mm" height="99mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" fox:alt-text="RestrictionOnLandownershipImage">
		                    <xsl:attribute name="src">
		                      <xsl:text>url('data:</xsl:text>
		                      <xsl:text>image/png;base64,</xsl:text>
                                <xsl:value-of select="oereb:createRestrictionOnLandownershipImages(data:Map, ../data:PlanForLandRegister, $OverlayImage)" />                
		                      <xsl:text>')</xsl:text>
		                    </xsl:attribute>
		                    </fo:external-graphic>
	                    </fo:block>
	                  </xsl:for-each-group>
                  </xsl:if>
                  <xsl:if test="data:Map/data:ReferenceWMS and not(data:Map/data:Image)">
                  <!-- Does only work if the GetMap request is syntactically equal. -->
                      <xsl:for-each-group select="current-group()" group-by="data:Map/data:ReferenceWMS">
                      <xsl:sort order="ascending" select="data:Information/data:LocalisedText/data:Text"/>
                        <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                            <fo:external-graphic border="0.2pt solid black" width="174mm" height="99mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" fox:alt-text="RestrictionOnLandownershipImage">
                            <xsl:attribute name="src">
                              <xsl:text>url('data:</xsl:text>
                              <xsl:text>image/png;base64,</xsl:text>
                                <xsl:value-of select="oereb:createRestrictionOnLandownershipImages(data:Map, ../data:PlanForLandRegister, $OverlayImage)" />                
                              <xsl:text>')</xsl:text>
                            </xsl:attribute>
                            </fo:external-graphic>
                        </fo:block>
                      </xsl:for-each-group>
                  </xsl:if>                  
              </fo:block-container>
          
              <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                <fo:table table-layout="fixed" width="100%">
                  <fo:table-column column-width="68mm"/>
                  <fo:table-column column-width="10mm"/>
                  <fo:table-column column-width="59mm"/>
                  <fo:table-column column-width="20mm"/>
                  <fo:table-column column-width="17mm"/>
                  <fo:table-body>
                    <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm" >
                      <fo:table-cell>
                        <fo:block></fo:block>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block></fo:block>
                      </fo:table-cell>
                      <fo:table-cell text-align="left">
                        <fo:block font-size="6.5pt"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.Type']/value/text()"/></fo:block>
                      </fo:table-cell>
                      <fo:table-cell text-align="right">
                        <fo:block font-size="6.5pt"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.Share']/value/text()"/></fo:block>
                      </fo:table-cell>
                      <fo:table-cell text-align="right">
                        <fo:block font-size="6.5pt"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.PartInPercent']/value/text()"/></fo:block>
                      </fo:table-cell>
                    </fo:table-row>

                    <!-- TODO: Gruppierung prüfen -->
                    <xsl:for-each-group select="current-group()" group-by="data:TypeCode">
                    <xsl:sort lang="de" order="ascending" select="data:Information/data:LocalisedText/data:Text"/>
                      <!--<fo:block linefeed-treatment="preserve" font-weight="400" font-size="11pt" font-family="Cadastra"><xsl:value-of select="data:Information/data:LocalisedText/data:Text"/></fo:block>-->
                      <fo:table-row font-weight="400" vertical-align="middle" line-height="5mm" >
                        <fo:table-cell>
                          <xsl:if test="position()=1">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.Legend']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell display-align="center">
                          <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                            <fo:external-graphic border="0.2pt solid black" width="6mm" height="3mm" content-width="scale-to-fit" content-height="scale-to-fit" scaling="uniform" fox:alt-text="Symbol">
			                  <xsl:if test="data:Symbol">
                                  <xsl:attribute name="src">
                                    <xsl:text>url('data:</xsl:text>
                                    <xsl:text>image/png;base64,</xsl:text>
                                    <xsl:value-of select="oereb:fixImage(data:Symbol)"/>
                                    <xsl:text>')</xsl:text>
                                  </xsl:attribute>
			                  </xsl:if>
                                 <xsl:if test="data:SymbolRef and not(data:Symbol)">
                                  <xsl:attribute name="src">
                                    <xsl:text>url('</xsl:text>
                                    <xsl:value-of select="oereb:decodeURL(data:SymbolRef)"/>
                                    <xsl:text>')</xsl:text>
                                  </xsl:attribute>
			                  </xsl:if>
			                </fo:external-graphic>
                          </fo:block>
                        </fo:table-cell>
                        <fo:table-cell display-align="center" text-align="left" line-height="10.5pt">
                          <!-- Sind die Werte nicht falsch in Kt. NW? Hier sollte doch "Wohnen 3" o.ä. stehen. -->
                          <fo:block><xsl:value-of select="data:Information/data:LocalisedText/data:Text"/></fo:block>
                          <!-- Zum Testen der Summenbildung und Gruppierung-->
                          <!--<fo:block><xsl:value-of select="data:TypeCode"/></fo:block>-->
                        </fo:table-cell>
                        <fo:table-cell text-align="right">
                          <xsl:if test="data:AreaShare">
                            <fo:block line-height-shift-adjustment="disregard-shifts"><xsl:value-of select="format-number(sum(current-group()/data:AreaShare), &quot;#'###&quot;, &quot;swiss&quot;)"/> m<fo:inline baseline-shift="super" font-size="60%">2</fo:inline></fo:block>
                          </xsl:if>
                          <xsl:if test="data:LengthShare">
                            <fo:block line-height-shift-adjustment="disregard-shifts"><xsl:value-of select="format-number(sum(current-group()/data:LengthShare), &quot;#'###&quot;, &quot;swiss&quot;)"/> m</fo:block>
                          </xsl:if>
                          <xsl:if test="data:NrOfPoints">
                            <fo:block line-height-shift-adjustment="disregard-shifts"><xsl:value-of select="format-number(sum(current-group()/data:NrOfPoints), &quot;#'###&quot;, &quot;swiss&quot;)"/></fo:block>
                          </xsl:if>  
                          <fo:block/>                        
                        </fo:table-cell>
                        <fo:table-cell text-align="right">
                          <xsl:if test="data:PartInPercent">
                            <fo:block><xsl:value-of select="format-number(sum(current-group()/data:PartInPercent), &quot;#'###.#&quot;, &quot;swiss&quot;)"/>%</fo:block>
                          </xsl:if>
                          <fo:block/>
                        </fo:table-cell>
                      </fo:table-row>
                    </xsl:for-each-group>
                  </fo:table-body>
                </fo:table>
              </fo:block-container>

              <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                <fo:table table-layout="fixed" width="100%">
                  <fo:table-column column-width="68mm"/>
                  <fo:table-column column-width="10mm"/>
                  <fo:table-column column-width="59mm"/>
                  <fo:table-column column-width="20mm"/>
                  <fo:table-column column-width="17mm"/>
                  <fo:table-body>
                    <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm" >
                      <fo:table-cell>
                        <fo:block/>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block/>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block/>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block/>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block/>
                      </fo:table-cell>
                    </fo:table-row>

                    <xsl:for-each-group select="current-group()/data:Map/data:OtherLegend" group-by="data:TypeCode">
                    <xsl:sort lang="de" order="ascending" select="data:LegendText/data:LocalisedText/data:Text"/>
                      <fo:table-row font-weight="400" vertical-align="middle" line-height="5mm" >
                        <fo:table-cell>
                          <xsl:if test="position()=1">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.OtherLegend']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell display-align="center">
                          <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                            <fo:external-graphic border="0.2pt solid black" width="6mm" height="3mm" content-width="scale-to-fit" content-height="scale-to-fit" scaling="uniform" fox:alt-text="Symbol">
                              <xsl:if test="data:Symbol">
                                  <xsl:attribute name="src">
                                    <xsl:text>url('data:</xsl:text>
                                    <xsl:text>image/png;base64,</xsl:text>
                                    <xsl:value-of select="oereb:fixImage(data:Symbol)"/>
                                    <xsl:text>')</xsl:text>
                                  </xsl:attribute>
                              </xsl:if>
                                 <xsl:if test="data:SymbolRef and not(data:Symbol)">
                                  <xsl:attribute name="src">
                                    <xsl:text>url('</xsl:text>
                                    <xsl:value-of select="oereb:decodeURL(data:SymbolRef)"/>
                                    <xsl:text>')</xsl:text>
                                  </xsl:attribute>
                              </xsl:if>
                            </fo:external-graphic>
                          </fo:block>
                        </fo:table-cell>
                        <fo:table-cell display-align="center" text-align="left" line-height="10.5pt">
                          <fo:block><xsl:value-of select="data:LegendText/data:LocalisedText/data:Text"/></fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="right">
                        <!-- This is just to get the same line height as in the table above. -->
                            <fo:block line-height-shift-adjustment="disregard-shifts" color="white" visibility="hidden"><xsl:text>m</xsl:text><fo:inline baseline-shift="super" font-size="60%"><xsl:text> </xsl:text></fo:inline></fo:block>
                          </fo:table-cell>
                        <fo:table-cell text-align="right ">
                          <fo:block></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                    </xsl:for-each-group>
                  </fo:table-body>
                </fo:table>
              </fo:block-container>

              <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                <fo:table table-layout="fixed" width="100%">
                  <fo:table-column column-width="68mm"/>
                  <fo:table-column column-width="106mm"/>
                  <fo:table-body>
                    <fo:table-row  border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm">
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                    </fo:table-row>

                    <xsl:for-each-group select="current-group()/data:Map" group-by="data:LegendAtWeb">
                    <!-- Wegen möglicher leeren LegendAtWeb-Elementen ist die Sortierung entscheidend bezüglich der position()-Bedingung. -->
                    <xsl:sort lang="de" order="ascending" select="data:LegendAtWeb"/>
                    <xsl:if test="not(normalize-space(data:LegendAtWeb)='')">

                      <fo:table-row vertical-align="middle" line-height="5mm" font-weight="400">
                        <fo:table-cell>
                          <xsl:if test="position()=1">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.LegendRef']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell line-height="8.5pt" display-align="center">
                          <!-- Use hyphenating for very long url (e.g. getlegendgraphics).  -->
                          <fo:block language="en" hyphenate="true" hyphenation-character="-" font-size="6.5pt">
                          <fo:basic-link text-decoration="none" color="rgb(76,143,186)">
                            <xsl:attribute name="external-destination"><xsl:value-of select="oereb:decodeURL(data:LegendAtWeb)"/></xsl:attribute>
                            <xsl:value-of select="oereb:decodeURL(data:LegendAtWeb)"/>
                          </fo:basic-link>
                          </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      </xsl:if>
                    </xsl:for-each-group>
                  </fo:table-body>
                </fo:table>
              </fo:block-container>

              <fo:block-container height="10mm" background-color="transparent">
                <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                  <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
                </fo:block>
              </fo:block-container>

              <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                <fo:table table-layout="fixed" width="100%">
                  <fo:table-column column-width="68mm"/>
                  <fo:table-column column-width="106mm"/>
                  <fo:table-body>
                    <fo:table-row  border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm">
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <xsl:for-each-group select="current-group()/data:LegalProvisions[data:DocumentType='LegalProvision']" group-by="data:TextAtWeb/data:LocalisedText/data:Text">
                    <xsl:sort lang="de" order="ascending" select="data:Title/data:LocalisedText/data:Text"/>
                      <fo:table-row vertical-align="middle" line-height="5mm" font-weight="400">
                        <fo:table-cell>
                          <xsl:if test="position()=1">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.LegalProvision']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell display-align="center">
                          <fo:block font-size="8.5pt" line-height="10.5pt">
                            <xsl:choose>
                                <xsl:when test="data:OfficialNumber">
                                    <xsl:value-of select="data:Title/data:LocalisedText/data:Text"/>, <xsl:value-of select="data:OfficialNumber"/><xsl:text>:</xsl:text>                          
                                </xsl:when>
	                            <xsl:otherwise>
	                                <xsl:value-of select="data:Title/data:LocalisedText/data:Text"/><xsl:text>:</xsl:text>                            
	                            </xsl:otherwise>
                            </xsl:choose>
                          </fo:block>
                          <fo:block font-size="6.5pt" line-height="8.5pt" margin-left="3mm" margin-top="0mm">
                          <fo:basic-link text-decoration="none" color="rgb(76,143,186)">
                            <xsl:attribute name="external-destination"><xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/></xsl:attribute>
                            <xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/>
                          </fo:basic-link>
                          </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                    </xsl:for-each-group>
                  </fo:table-body>
                </fo:table>
              </fo:block-container>

              <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                <fo:table table-layout="fixed" width="100%">
                  <fo:table-column column-width="68mm"/>
                  <fo:table-column column-width="106mm"/>
                  <fo:table-body>
                    <fo:table-row  border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm">
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <xsl:for-each-group select="current-group()/data:LegalProvisions[data:DocumentType='Law']" group-by="data:TextAtWeb/data:LocalisedText/data:Text">
                    <xsl:sort lang="de" order="ascending" select="data:Title/data:LocalisedText/data:Text"/>
                      <fo:table-row vertical-align="middle" line-height="5mm" font-weight="400">
                        <fo:table-cell>
                          <xsl:if test="position()=1">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.Law']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell display-align="center">
                          <fo:block font-size="8.5pt" line-height="10.5pt">
                            <xsl:value-of select="data:Title/data:LocalisedText/data:Text"/><xsl:text>:</xsl:text>
                          </fo:block>
                          <fo:block font-size="6.5pt" line-height="8.5pt" margin-left="3mm" margin-top="0mm">
                          <fo:basic-link text-decoration="none" color="rgb(76,143,186)">
                            <xsl:attribute name="external-destination"><xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/></xsl:attribute>
                            <xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/>
                          </fo:basic-link>
                          </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                    </xsl:for-each-group>
                    
                    <!-- TODO: validate if title is only written when it isn't already there from the for-each-group-iteration from above -->
                    <xsl:for-each-group select="current-group()/data:LegalProvisions/data:Reference[data:DocumentType='Law']" group-by="data:TextAtWeb/data:LocalisedText/data:Text">
                    <xsl:sort lang="de" order="ascending" select="data:Title/data:LocalisedText/data:Text"/>
                      <fo:table-row vertical-align="middle" line-height="5mm" font-weight="400">
                        <fo:table-cell>
                          <xsl:if test="position()=1 and not(../data:LegalProvisions[data:DocumentType='Law'])">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.Law']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell display-align="center">
                          <fo:block font-size="8.5pt" line-height="10.5pt">
                            <xsl:value-of select="data:Title/data:LocalisedText/data:Text"/><xsl:text>:</xsl:text>
                          </fo:block>
                          <fo:block font-size="6.5pt" line-height="8.5pt" margin-left="3mm" margin-top="0mm">
                          <fo:basic-link text-decoration="none" color="rgb(76,143,186)">
                            <xsl:attribute name="external-destination"><xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/></xsl:attribute>
                            <xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/>
                          </fo:basic-link>
                          </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                    </xsl:for-each-group>
                    
                  </fo:table-body>
                </fo:table>
              </fo:block-container>
              
              <!-- Hints müssen nicht vorhanden sein. -->
              <xsl:if test="current-group()/data:LegalProvisions[data:DocumentType='Hint']">
                <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                  <fo:table table-layout="fixed" width="100%">
                    <fo:table-column column-width="68mm"/>
                    <fo:table-column column-width="106mm"/>
                    <fo:table-body>
                      <fo:table-row  border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm">
                          <fo:table-cell>
                            <fo:block></fo:block>
                          </fo:table-cell>
                          <fo:table-cell>
                            <fo:block></fo:block>
                          </fo:table-cell>
                      </fo:table-row>
                      <xsl:for-each-group select="current-group()/data:LegalProvisions[data:DocumentType='Hint']" group-by="data:TextAtWeb/data:LocalisedText/data:Text">
                      <xsl:sort lang="de" order="descending" select="data:Title/data:LocalisedText/data:Text"/>
                        <fo:table-row vertical-align="middle" line-height="5mm" font-weight="400">
                          <fo:table-cell>
                            <xsl:if test="position()=1">
                              <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.Hint']/value/text()"/></fo:block>
                            </xsl:if>
                            <xsl:if test="position()!=1">
                              <fo:block></fo:block>
                            </xsl:if>
                          </fo:table-cell>
                          <fo:table-cell display-align="center">
                            <fo:block font-size="8.5pt" line-height="10.5pt">
                              <xsl:value-of select="data:Title/data:LocalisedText/data:Text"/><xsl:text>:</xsl:text>
                            </fo:block>
                            <fo:block font-size="6.5pt" line-height="8.5pt" margin-left="3mm" margin-top="0mm">
                            <fo:basic-link text-decoration="none" color="rgb(76,143,186)">
                              <xsl:attribute name="external-destination"><xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/></xsl:attribute>
                              <xsl:value-of select="oereb:decodeURL(data:TextAtWeb/data:LocalisedText/data:Text)"/>
                            </fo:basic-link>
                            </fo:block>
                          </fo:table-cell>
                        </fo:table-row>
                      </xsl:for-each-group>
                    </fo:table-body>
                  </fo:table>
                </fo:block-container>
              </xsl:if>

              <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
                <fo:table table-layout="fixed" width="100%">
                  <fo:table-column column-width="68mm"/>
                  <fo:table-column column-width="106mm"/>
                  <fo:table-body>
                    <fo:table-row  border-bottom="0.2pt solid black" vertical-align="middle" line-height="5mm">
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                          <fo:block></fo:block>
                        </fo:table-cell>
                    </fo:table-row>         
                    <!-- <xsl:for-each-group select="current-group()/data:LegalProvisions/data:ResponsibleOffice" group-by="data:Name">  -->                  
                    <xsl:for-each-group select="current-group()/data:ResponsibleOffice" group-by="data:Name">
                    <xsl:sort lang="de" order="ascending" select="data:Name"/>
                      <fo:table-row vertical-align="middle" line-height="5mm" font-weight="400">
                        <fo:table-cell>
                          <xsl:if test="position()=1">
                            <fo:block font-weight="700"><xsl:value-of select="$localeXml/data[@name='RestrictionPage.ResponsibleOffice']/value/text()"/></fo:block>
                          </xsl:if>
                          <xsl:if test="position()!=1">
                            <fo:block></fo:block>
                          </xsl:if>
                        </fo:table-cell>
                        <fo:table-cell display-align="center">
                          <fo:block font-size="8.5pt">
                            <xsl:value-of select="data:Name/data:LocalisedText/data:Text"/><xsl:text>:</xsl:text>
                          </fo:block>
                          <fo:block font-size="6.5pt" line-height="8.5pt" margin-left="3mm" margin-top="-1mm">
                          <fo:basic-link text-decoration="none" color="rgb(76,143,186)">
                            <xsl:attribute name="external-destination"><xsl:value-of select="oereb:decodeURL(data:OfficeAtWeb)"/></xsl:attribute>
                            <xsl:value-of select="oereb:decodeURL(data:OfficeAtWeb)"/>
                          </fo:basic-link>
                          </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                    </xsl:for-each-group>
                  </fo:table-body>
                </fo:table>
              </fo:block-container>

              <fo:block-container background-color="transparent">
                <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
                  <fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" rule-thickness="0.2pt"/>
                </fo:block>
              </fo:block-container>

            </xsl:for-each-group>
        </fo:block>
      </fo:flow>
    </fo:page-sequence>
  </xsl:template>

  <!-- Template for the table of contents. Sorting is bit of a hack. Perhaps this whole TOC thing can -->
  <!-- be made much simpler with better apply-templates strategy.-->
  <xsl:template match="data:RealEstate" mode="toc">
    <fo:block-container margin-bottom="10mm" font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
      <fo:table table-layout="fixed" width="100%">
        <fo:table-column column-width="7mm"/>
        <fo:table-column column-width="167mm"/>
        <fo:table-body>
          <fo:table-row vertical-align="middle">
            <fo:table-cell>
              <fo:block margin-top="1mm" margin-bottom="2.8mm" font-weight="700" font-size="6.5pt"><xsl:value-of select="$localeXml/data[@name='Page']/value/text()"/></fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block/>
            </fo:table-cell>
          </fo:table-row>
          <xsl:for-each-group select="data:RestrictionOnLandownership" group-by="data:Theme/data:Code">
          <xsl:sort data-type="number" order="ascending" select="(number(starts-with(data:Theme/data:Code, 'ch')) * 0.9) + (number(data:Theme/data:Code='LandUsePlans') * 1) + (number(data:Theme/data:Code='MotorwaysProjectPlaningZones') * 2) + (number(data:Theme/data:Code='MotorwaysBuildingLines') * 3) + (number(data:Theme/data:Code='RailwaysProjectPlanningZones') * 4) + (number(data:Theme/data:Code='RailwaysBuildingLines') * 5) + (number(data:Theme/data:Code='AirportsProjectPlanningZones') * 6) + (number(data:Theme/data:Code='AirportsBuildingLines') * 7) + (number(data:Theme/data:Code='AirportsSecurityZonePlans') * 8) + (number(data:Theme/data:Code='ContaminatedSites') * 9) + (number(data:Theme/data:Code='ContaminatedMilitarySites') * 10) + (number(data:Theme/data:Code='ContaminatedCivilAviationSites') * 11) + (number(data:Theme/data:Code='ContaminatedPublicTransportSites') * 12) + (number(data:Theme/data:Code='GroundwaterProtectionZones') * 13) + (number(data:Theme/data:Code='GroundwaterProtectionSites') * 14) + (number(data:Theme/data:Code='NoiseSensitivityLevels') * 15) + (number(data:Theme/data:Code='ForestPerimeters') * 16) + (number(data:Theme/data:Code='ForestDistanceLines') * 17)"/>
            <fo:table-row line-height="6mm" border-bottom="0.2pt solid black" vertical-align="middle">
              <fo:table-cell>
                <fo:block>
                  <fo:basic-link internal-destination="{generate-id(.)}">
                    <fo:page-number-citation ref-id="{generate-id(.)}"/>
                  </fo:basic-link>  
                </fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block>
                  <fo:basic-link internal-destination="{generate-id(.)}">
                    <xsl:value-of select="data:Theme/data:Text/data:Text"/>
                  </fo:basic-link>  
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </xsl:for-each-group>
        </fo:table-body>
      </fo:table>
    </fo:block-container>
  </xsl:template>

  <xsl:template name="insertHeaderAndFooter">
    <fo:static-content flow-name="xsl-region-before">
      <fo:block>
        <fo:block-container absolute-position="absolute" top="0mm" left="0mm" background-color="transparent">
          <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
             <fo:external-graphic width="44mm" content-width="scale-to-fit" fox:alt-text="FederalLogo">
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:FederalLogo">
                   <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                     <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:FederalLogo)"/>
                     <xsl:text>')</xsl:text>
                   </xsl:attribute>
                </xsl:if>
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:FederalLogoRef and not(/extract:GetExtractByIdResponse/data:Extract/data:FederalLogo)">
                  <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:FederalLogoRef)"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </xsl:if>
             </fo:external-graphic>          
          </fo:block>
        </fo:block-container>

        <fo:block-container absolute-position="absolute" top="0mm" left="60mm" background-color="transparent">
          <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
             <fo:external-graphic border="0pt solid black" width="30mm" height="13mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" text-align="center" fox:alt-text="CantonalLogo">
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:CantonalLogo">
                   <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                     <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:CantonalLogo)"/>
                     <xsl:text>')</xsl:text>
                   </xsl:attribute>
                </xsl:if>
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:CantonalLogoRef and not(/extract:GetExtractByIdResponse/data:Extract/data:CantonalLogo)">
                  <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:CantonalLogoRef)"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </xsl:if>
             </fo:external-graphic>          
          </fo:block>
        </fo:block-container>

        <fo:block-container absolute-position="absolute" top="0mm" left="95mm" background-color="transparent">
          <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
            <fo:external-graphic width="30mm" height="13mm" scaling="uniform" content-width="scale-to-fit" content-height="scale-to-fit" text-align="center" fox:alt-text="MunicipalityLogo">
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:MunicipalityLogo">
                   <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                     <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:MunicipalityLogo)"/>
                     <xsl:text>')</xsl:text>
                   </xsl:attribute>
                </xsl:if>
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:MunicipalityLogoRef and not(/extract:GetExtractByIdResponse/data:Extract/data:MunicipalityLogo)">
                  <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:MunicipalityLogoRef)"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </xsl:if>
             </fo:external-graphic>          
          </fo:block>
        </fo:block-container>

        <fo:block-container absolute-position="absolute" top="0mm" left="139mm" background-color="transparent">
          <fo:block font-size="0pt" padding="0mm" margin="0mm" line-height="0mm">
             <fo:external-graphic width="35mm" height="10mm" scaling="non-uniform" content-width="scale-to-fit" content-height="scale-to-fit" fox:alt-text="LogoPLRCadastre">
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:LogoPLRCadastre">
	               <xsl:attribute name="src">
	                 <xsl:text>url('data:</xsl:text>
	                 <xsl:text>image/png;base64,</xsl:text>
	                 <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:LogoPLRCadastre)"/>
	                 <xsl:text>')</xsl:text>
	               </xsl:attribute>
                </xsl:if>
                <xsl:if test="/extract:GetExtractByIdResponse/data:Extract/data:LogoPLRCadastreRef and not(/extract:GetExtractByIdResponse/data:Extract/data:LogoPLRCadastre)">
                  <xsl:attribute name="src">
                     <xsl:text>url('data:</xsl:text>
                     <xsl:text>image/png;base64,</xsl:text>
                    <xsl:value-of select="oereb:fixImage(/extract:GetExtractByIdResponse/data:Extract/data:LogoPLRCadastreRef)"/>
                    <xsl:text>')</xsl:text>
                  </xsl:attribute>
                </xsl:if>
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
                  <xsl:value-of select="format-dateTime(/extract:GetExtractByIdResponse/data:Extract/data:CreationDate,'[D01].[M01].[Y0001]')"/><fo:inline padding-left="1em"><xsl:value-of select="format-dateTime(/extract:GetExtractByIdResponse/data:Extract/data:CreationDate,'[H01]:[m01]:[s01]')"/></fo:inline><fo:inline padding-left="1em"><xsl:value-of select="/extract:GetExtractByIdResponse/data:Extract/data:ExtractIdentifier"/></fo:inline>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="right">
               <!-- <fo:block>Seite <fo:page-number/>/<fo:page-number-citation-last ref-id="page-sequence-id"/></fo:block> -->
               <fo:block><xsl:value-of select="$localeXml/data[@name='Page']/value/text()"/><xsl:text> </xsl:text><fo:page-number/>/<fo:page-number-citation ref-id="last-page"/></fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
      </fo:block-container>
    </fo:static-content>
  </xsl:template>

  <xsl:template name="insertGlossary">
    <fo:page-sequence master-reference="mainPage" id="page-sequence-id">
      <xsl:call-template name="insertHeaderAndFooter"/>
      <fo:flow flow-name="xsl-region-body">
        <fo:block>
          <fo:block-container height="13mm" background-color="transparent">
            <fo:block page-break-before="always" line-height="18pt" linefeed-treatment="preserve" font-weight="700" font-size="15pt" font-family="Cadastra"><xsl:value-of select="$localeXml/data[@name='GlossaryPage.Title']/value/text()"/></fo:block>
          </fo:block-container>            
          <fo:block-container font-weight="400" font-size="8.5pt" font-family="Cadastra" background-color="transparent">
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-width="174mm"/>
              <fo:table-body>
              <xsl:for-each select="data:Glossary">
                <xsl:sort select="data:Title/data:LocalisedText/data:Text"/>
                <fo:table-row border-bottom="0.2pt solid black" vertical-align="middle" line-height="11.5pt" >
                  <fo:table-cell padding-top="1mm" padding-bottom="1mm">
                    <fo:block><fo:inline font-weight="700"><xsl:value-of select="data:Title/data:LocalisedText/data:Text"/>: </fo:inline><xsl:value-of select="data:Content/data:LocalisedText/data:Text"/></fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
              </fo:table-body>
            </fo:table>
          </fo:block-container>
        </fo:block>
        <fo:block id="last-page"/>
      </fo:flow>  
    </fo:page-sequence>
  </xsl:template>

</xsl:stylesheet>
