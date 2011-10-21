<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ccf="http://ccf.open.collab.net/GenericArtifactV1.0" version="2.0" exclude-result-prefixes="xsl xs fn ccf"><!--Automatically generated stylesheet to convert from the generic artifact format to a repository specific schema--><xsl:template match="node()" priority="1"/><xsl:template match="/ccf:artifact" priority="2"><artifact><topLevelAttributes><xsl:copy-of select="@* [not(name()='xmlns:xsi' or name() ='xsi:schemaLocation')] "/></topLevelAttributes><xsl:apply-templates/></artifact></xsl:template><xsl:template match="ccf:field[@fieldName='startDate' and @fieldType='mandatoryField']" priority="2"><startDate><xsl:value-of select="text()"/></startDate></xsl:template><xsl:template match="ccf:field[@fieldName='endDate' and @fieldType='mandatoryField']" priority="2"><endDate><xsl:value-of select="text()"/></endDate></xsl:template><xsl:template match="ccf:field[@fieldName='title' and @fieldType='mandatoryField']" priority="2"><title><xsl:value-of select="text()"/></title></xsl:template><xsl:template match="ccf:field[@fieldName='description' and @fieldType='mandatoryField']" priority="2"><description><xsl:value-of select="text()"/></description></xsl:template><xsl:template match="ccf:field[@fieldName='path' and @fieldType='mandatoryField']" priority="2"><path><xsl:value-of select="text()"/></path></xsl:template><xsl:template match="ccf:field[@fieldName='projectId' and @fieldType='mandatoryField']" priority="2"><projectId><xsl:value-of select="text()"/></projectId></xsl:template><xsl:template match="ccf:field[@fieldName='parentFolderId' and @fieldType='mandatoryField']" priority="2"><parentFolderId><xsl:value-of select="text()"/></parentFolderId></xsl:template><xsl:template match="ccf:field[@fieldName='createdBy' and @fieldType='mandatoryField']" priority="2"><createdBy><xsl:value-of select="text()"/></createdBy></xsl:template><xsl:template match="ccf:field[@fieldName='createdDate' and @fieldType='mandatoryField']" priority="2"><createdDate><xsl:value-of select="text()"/></createdDate></xsl:template><xsl:template match="ccf:field[@fieldName='id' and @fieldType='mandatoryField']" priority="2"><id><xsl:value-of select="text()"/></id></xsl:template><xsl:template match="ccf:field[@fieldName='lastModifiedBy' and @fieldType='mandatoryField']" priority="2"><lastModifiedBy><xsl:value-of select="text()"/></lastModifiedBy></xsl:template><xsl:template match="ccf:field[@fieldName='lastModifiedDate' and @fieldType='mandatoryField']" priority="2"><lastModifiedDate><xsl:value-of select="text()"/></lastModifiedDate></xsl:template><xsl:template match="ccf:field[@fieldName='version' and @fieldType='mandatoryField']" priority="2"><version><xsl:value-of select="text()"/></version></xsl:template></xsl:stylesheet>