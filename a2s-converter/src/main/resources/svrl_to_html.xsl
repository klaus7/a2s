<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:iso="http://purl.oclc.org/dsdl/schematron"
	xmlns:svrl="http://purl.oclc.org/dsdl/svrl" version="2.0">
	<xsl:output method="html" indent="yes" />
	<xsl:template match="/">
		<html>
			<head>
				<title>Schematron-Validierung</title>
				<style type="text/css">
#newspaper-a
{
	font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif;
	font-size: 12px;
	margin: 45px;
	/*width: 480px;*/
	text-align: left;
	border-collapse: collapse;
	border: 1px solid #69c;
}
#newspaper-a th
{
	padding: 12px 17px 12px 17px;
	font-weight: normal;
	font-size: 14px;
	color: #039;
	border-bottom: 1px dashed #69c;
}
#newspaper-a td
{
	padding: 7px 17px 7px 17px;
	color: #669;
}
#newspaper-a tbody tr:hover td
{
	color: #339;
	background: #d0dafd;
}
				</style>
			</head>
			<body>
				<h1>Schematron-Validierung</h1>
				<h2>Test-Ergebnisse</h2>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="svrl:text">
		<h2 align="center">
			<xsl:apply-templates />
		</h2>
	</xsl:template>
	<xsl:template match="svrl:text[2]">
		<h3 align="left">
			<font color="magenta">
				<xsl:value-of select="substring-after(.,'/')" />
			</font>
		</h3>
	</xsl:template>

	<xsl:template match="svrl:failed-assert">
		<xsl:choose>
			<xsl:when test="@role = 'error'">
				<table id="newspaper-a">
				<thead>
					<tr>
						<th>
							<font color="red">
								<b>Error</b>
							</font>
						</th>
						<th>
							<xsl:value-of select="svrl:text" />
						</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Location</td>
						<td><xsl:value-of select="@location" /></td>
					</tr>
					<tr>
						<td>Test</td>
						<td><xsl:value-of select="@test" /></td>
					</tr>
				</tbody>
				</table>
			</xsl:when>
			<xsl:when test="@role = 'warning'">
				<p>
					<font color="blue">
						<font color="green">
							<xsl:value-of select="@line" />
							-
							<xsl:value-of select="@column" />
							:
						</font>
						Warning:
						<xsl:value-of select="svrl:text" />
					</font>
				</p>
			</xsl:when>
			<xsl:when test="@role = 'info'">
				<p>
					<font color="blue">
						<font color="green">
							<xsl:value-of select="@line" />
							-
							<xsl:value-of select="@column" />
							:
						</font>
						Info:
						<xsl:value-of select="svrl:text" />
					</font>
				</p>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
