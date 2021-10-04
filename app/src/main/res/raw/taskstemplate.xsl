<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

    <xsl:template match="Tasks">
        <xsl:copy>
            <xsl:for-each select="Task[Date='07.10.2021']">
                    <xsl:sort select="Category" order="ascending"/>
                    <xsl:sort select="Category"/>
                    <xsl:sort select="Description"/>
                    <xsl:copy-of select="." />
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>