<?xml version="1.0" encoding="UTF-8"?>
<!--
This file was generated by Altova MapForce 2009sp1

YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.

Refer to the Altova MapForce Documentation for further details.
http://www.altova.com/mapforce
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs xsi xsl">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<artifact>
			<xsl:attribute name="xsi:noNamespaceSchemaLocation">
				<xsl:value-of select="'C:/CCF/Sts-ccf/workspace/CCF/samples/JIRATF/TF2JIRA/xslt/TEAMFO~2.XSD'"/>
			</xsl:attribute>
			<xsl:variable name="var1_instance" select="."/>
			<xsl:for-each select="$var1_instance/artifact">
				<xsl:variable name="var2_artifact" select="."/>
				<topLevelAttributes>
					<xsl:attribute name="artifactMode">
						<xsl:value-of select="string(topLevelAttributes/@artifactMode)"/>
					</xsl:attribute>
					<xsl:attribute name="artifactAction">
						<xsl:value-of select="string(topLevelAttributes/@artifactAction)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceArtifactVersion">
						<xsl:value-of select="string(topLevelAttributes/@sourceArtifactVersion)"/>
					</xsl:attribute>
					<xsl:attribute name="targetArtifactVersion">
						<xsl:value-of select="string(topLevelAttributes/@targetArtifactVersion)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceArtifactLastModifiedDate">
						<xsl:value-of select="string(topLevelAttributes/@sourceArtifactLastModifiedDate)"/>
					</xsl:attribute>
					<xsl:attribute name="targetArtifactLastModifiedDate">
						<xsl:value-of select="string(topLevelAttributes/@targetArtifactLastModifiedDate)"/>
					</xsl:attribute>
					<xsl:attribute name="conflictResolutionPriority">
						<xsl:value-of select="string(topLevelAttributes/@conflictResolutionPriority)"/>
					</xsl:attribute>
					<xsl:attribute name="artifactType">
						<xsl:value-of select="string(topLevelAttributes/@artifactType)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceSystemKind">
						<xsl:value-of select="string(topLevelAttributes/@sourceSystemKind)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceSystemId">
						<xsl:value-of select="string(topLevelAttributes/@sourceSystemId)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceRepositoryKind">
						<xsl:value-of select="string(topLevelAttributes/@sourceRepositoryKind)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceRepositoryId">
						<xsl:value-of select="string(topLevelAttributes/@sourceRepositoryId)"/>
					</xsl:attribute>
					<xsl:attribute name="sourceArtifactId">
						<xsl:value-of select="string(topLevelAttributes/@sourceArtifactId)"/>
					</xsl:attribute>
					<xsl:attribute name="targetSystemKind">
						<xsl:value-of select="string(topLevelAttributes/@targetSystemKind)"/>
					</xsl:attribute>
					<xsl:attribute name="targetSystemId">
						<xsl:value-of select="string(topLevelAttributes/@targetSystemId)"/>
					</xsl:attribute>
					<xsl:attribute name="targetRepositoryKind">
						<xsl:value-of select="string(topLevelAttributes/@targetRepositoryKind)"/>
					</xsl:attribute>
					<xsl:attribute name="targetRepositoryId">
						<xsl:value-of select="string(topLevelAttributes/@targetRepositoryId)"/>
					</xsl:attribute>
					<xsl:attribute name="targetArtifactId">
						<xsl:value-of select="string(topLevelAttributes/@targetArtifactId)"/>
					</xsl:attribute>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depParentSourceRepositoryKind">
						<xsl:attribute name="depParentSourceRepositoryKind">
							<xsl:value-of select="string(topLevelAttributes/@depParentSourceRepositoryKind)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depParentSourceRepositoryId">
						<xsl:attribute name="depParentSourceRepositoryId">
							<xsl:value-of select="string(topLevelAttributes/@depParentSourceRepositoryId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depParentSourceArtifactId">
						<xsl:attribute name="depParentSourceArtifactId">
							<xsl:value-of select="string(topLevelAttributes/@depParentSourceArtifactId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depChildSourceRepositoryKind">
						<xsl:attribute name="depChildSourceRepositoryKind">
							<xsl:value-of select="string(topLevelAttributes/@depChildSourceRepositoryKind)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depChildSourceRepositoryId">
						<xsl:attribute name="depChildSourceRepositoryId">
							<xsl:value-of select="string(topLevelAttributes/@depChildSourceRepositoryId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depChildSourceArtifactId">
						<xsl:attribute name="depChildSourceArtifactId">
							<xsl:value-of select="string(topLevelAttributes/@depChildSourceArtifactId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depParentTargetRepositoryKind">
						<xsl:attribute name="depParentTargetRepositoryKind">
							<xsl:value-of select="string(topLevelAttributes/@depParentTargetRepositoryKind)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depParentTargetRepositoryId">
						<xsl:attribute name="depParentTargetRepositoryId">
							<xsl:value-of select="string(topLevelAttributes/@depParentTargetRepositoryId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depParentTargetArtifactId">
						<xsl:attribute name="depParentTargetArtifactId">
							<xsl:value-of select="string(topLevelAttributes/@depParentTargetArtifactId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depChildTargetRepositoryKind">
						<xsl:attribute name="depChildTargetRepositoryKind">
							<xsl:value-of select="string(topLevelAttributes/@depChildTargetRepositoryKind)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depChildTargetRepositoryId">
						<xsl:attribute name="depChildTargetRepositoryId">
							<xsl:value-of select="string(topLevelAttributes/@depChildTargetRepositoryId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@depChildTargetArtifactId">
						<xsl:attribute name="depChildTargetArtifactId">
							<xsl:value-of select="string(topLevelAttributes/@depChildTargetArtifactId)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="errorCode">
						<xsl:value-of select="string(topLevelAttributes/@errorCode)"/>
					</xsl:attribute>
					<xsl:attribute name="transactionId">
						<xsl:value-of select="string(topLevelAttributes/@transactionId)"/>
					</xsl:attribute>
					<xsl:attribute name="includesFieldMetaData">
						<xsl:value-of select="string(topLevelAttributes/@includesFieldMetaData)"/>
					</xsl:attribute>
					<xsl:if test="$var2_artifact/topLevelAttributes/@sourceSystemTimezone">
						<xsl:attribute name="sourceSystemTimezone">
							<xsl:value-of select="string(topLevelAttributes/@sourceSystemTimezone)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$var2_artifact/topLevelAttributes/@targetSystemTimezone">
						<xsl:attribute name="targetSystemTimezone">
							<xsl:value-of select="string(topLevelAttributes/@targetSystemTimezone)"/>
						</xsl:attribute>
					</xsl:if>
				</topLevelAttributes>
				<xsl:for-each select="title">
					<summary>
						<xsl:value-of select="string(.)"/>
					</summary>
				</xsl:for-each>
				<xsl:for-each select="description">
					<description>
						<xsl:value-of select="string(.)"/>
					</description>
				</xsl:for-each>
				<status>
					<xsl:value-of select="'Open'"/>
				</status>
				<xsl:for-each select="CommentText">
					<comment>
						<xsl:value-of select="string(.)"/>
					</comment>
				</xsl:for-each>
			</xsl:for-each>
		</artifact>
	</xsl:template>
</xsl:stylesheet>
