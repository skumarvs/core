<?xml version="1.0"?>
	<!--
		Copyright 2009 CollabNet, Inc. ("CollabNet") Licensed under the Apache
		License, Version 2.0 (the "License"); you may not use this file except
		in compliance with the License. You may obtain a copy of the License
		at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
		applicable law or agreed to in writing, software distributed under the
		License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
		CONDITIONS OF ANY KIND, either express or implied. See the License for
		the specific language governing permissions and limitations under the
		License.
	-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ccf="http://ccf.open.collab.net/GenericArtifactV1.0"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:stringutil="xalan://com.collabnet.ccf.core.utils.GATransformerUtil"
	exclude-result-prefixes="xsl xs ccf stringutil" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">
	<xsl:template match='/ccf:artifact[@artifactType = "plainArtifact"]'>
		<artifact xmlns="http://ccf.open.collab.net/GenericArtifactV1.0">
			<xsl:copy-of select="@*" />
			<field><xsl:attribute name="fieldName">active</xsl:attribute><xsl:attribute name="fieldAction">replace</xsl:attribute><xsl:attribute name="fieldType">mandatoryField</xsl:attribute><xsl:attribute name="fieldValueHasChanged">true</xsl:attribute><xsl:attribute name="fieldValueType">Boolean</xsl:attribute><xsl:attribute name="fieldValueIsNull">false</xsl:attribute>true</field>
			<xsl:apply-templates />
		</artifact>
	</xsl:template>
	<xsl:template match="/ccf:artifact[@artifactType = 'attachment']">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="title"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">title</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:value-of select="." />
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="description"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">description</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:choose>
				<xsl:when test="string(.)='&lt;blank&gt;'">
					<xsl:value-of select="''" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="." />
				</xsl:otherwise>
			</xsl:choose>
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="Benefit"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">benefit</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:value-of select="." />
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="Penalty"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">penalty</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:value-of select="." />
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="Backlog Effort"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">estimate</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:attribute name="fieldValueType">Integer</xsl:attribute>
			<xsl:value-of select="." />
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="points"]'>
		<xsl:if test="not(../ccf:field[@fieldName='Backlog Effort'])">
			<field>
				<xsl:copy-of select="@*" />
				<xsl:attribute name="fieldName">estimate</xsl:attribute>
				<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
				<xsl:attribute name="fieldValueType">Integer</xsl:attribute>
				<xsl:value-of select="." />
			</field>
		</xsl:if>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="Themes"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">theme</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:attribute name="fieldValueType">String</xsl:attribute>
			<xsl:value-of select="." />
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="Comment Text"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldName">Comment Text</xsl:attribute>
			<xsl:attribute name="fieldType">mandatoryField</xsl:attribute>
			<xsl:attribute name="fieldValueType">String</xsl:attribute>
			<xsl:value-of select="." />
		</field>
	</xsl:template>
	<xsl:template match='ccf:field[@fieldName="status"]'>
		<field>
			<xsl:copy-of select="@*" />
			<xsl:attribute name="fieldValueType">Date</xsl:attribute>
			<xsl:attribute name="fieldName">completedDate</xsl:attribute>
			<xsl:choose>
				<xsl:when test="string(.)='Open'">
					<xsl:attribute name="fieldValueIsNull">true</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="/ccf:artifact/ccf:field[@fieldName='lastModifiedDate']" />
				</xsl:otherwise>
			</xsl:choose>	
		</field>
	</xsl:template>
	
	<xsl:template match="text()" />
</xsl:stylesheet>