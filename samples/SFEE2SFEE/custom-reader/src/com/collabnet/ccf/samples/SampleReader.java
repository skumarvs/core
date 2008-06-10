package com.collabnet.ccf.samples;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;

import com.collabnet.ccf.core.AbstractReader;
import com.collabnet.ccf.core.ga.GenericArtifact;
import com.collabnet.ccf.core.ga.GenericArtifactField;
import com.collabnet.ccf.core.utils.DateUtil;

/**
 * This sample reader provides a skeleton that can be used by
 * developers to create an adaptor without even going through the 
 * implementation details of the adaptors provided in CollabNet Connector
 * Framework.
 * 
 * To implement a reader we define a class that extends from AbstractReader.
 * AbstractReader provides four methods that should be implemented by the 
 * plugin developer. They are
 * 		1. getChangedArtifacts
 * 		2. getArtifactData
 * 		3. getArtifactAttachments
 * 		4. getArtifactDependencies
 * All these methods should be implemented. If you don't want some of these methods
 * to be implemented, make these methods to return an empty list of GenericArtifacts.
 * 
 * However, the getChangedArtifacts method should be implemented to return the artifact
 * ids in a list. If this method is coded to return an empty list CCF will think
 * that there are no artifacts that need to be shipped to the target system.
 * 
 * After implementing the Reader let us proceed to create the mapping XSLT file
 * that is responsible to transform the GenericArtifact that we are sending
 * into a format that can be understood by the SFEEWriter.
 * Please refer the xslt file at
 * $CCF_HOME/samples/SFEE2SFEE/custom-reader/xslt
 * 
 * $CCF_HOME is the directory where you have extracted the CCF
 * zip file.
 * @author madhusuthanan (madhusuthanan@collab.net)
 *
 */
public class SampleReader extends AbstractReader {

	@Override
	public List<GenericArtifact> getArtifactAttachments(Document syncInfo,
			String artifactId) {
		// Let us not worry about the attachments as of now
		// So let us return an empty list of GenericArtifact s.
		List<GenericArtifact> attachmentGAList = new ArrayList<GenericArtifact>();
		return attachmentGAList;
	}

	@Override
	public List<GenericArtifact> getArtifactData(Document syncInfo,
			String artifactId) {
		// CCF is asking us to return the data of an artifact
		// Let us ship a dummy artifact across.
		// Please note that we are constructing a GenericArtifact object
		// directly here. In a non-trivial implementation this
		// routine will connect to the source system, get the data
		// for this artifact and populate the data in the GenericArtifact
		// object and return it in a list.
		GenericArtifact ga = new GenericArtifact();
		// Let us mark this artifact as a plain artifact.
		// A plain artifact indicates that this artifact is neither an attachment
		// nor a dependency. It is the artifact data itself.
		ga.setArtifactType(GenericArtifact.ArtifactTypeValue.PLAINARTIFACT);
		// Let us always mark this artifactAction as create
		// This will result a new artifact being created all the time
		ga.setArtifactAction(GenericArtifact.ArtifactActionValue.CREATE);
		// We are not shipping only the changed fields
		// We are shipping the whole artifact data
		ga.setArtifactMode(GenericArtifact.ArtifactModeValue.COMPLETE);
		// Let us assume that our repository supports versioning of the
		// artifacts. Of course it does a very trivial versioning by marking 
		// all the artifact's version as 1. 
		ga.setArtifactVersion("1");
		// The artifacts that we are emitting does not include field
		// metadata.
		ga.setIncludesFieldMetaData(GenericArtifact.IncludesFieldMetaDataValue.FALSE);
		// The source artifact id is the artifact id that is passed to this
		// method.
		ga.setSourceArtifactId(artifactId);
		String sourceRepositoryId = this.getSourceRepositoryId(syncInfo);
		ga.setSourceRepositoryId(sourceRepositoryId);
		String sourceRepositoryKind = this.getSourceRepositoryKind(syncInfo);
		ga.setSourceRepositoryKind(sourceRepositoryKind);
		String sourceSystemId = this.getSourceSystemId(syncInfo);
		ga.setSourceSystemId(sourceSystemId);
		String sourceSystemKind = this.getSourceSystemKind(syncInfo);
		ga.setSourceSystemKind(sourceSystemKind);
		
		String targetRepositoryId = this.getTargetRepositoryId(syncInfo);
		ga.setTargetRepositoryId(targetRepositoryId);
		String targetRepositoryKind = this.getTargetRepositoryKind(syncInfo);
		ga.setTargetRepositoryKind(targetRepositoryKind);
		String targetSystemId = this.getTargetSystemId(syncInfo);
		ga.setTargetSystemId(targetSystemId);
		String targetSystemKind = this.getTargetSystemKind(syncInfo);
		ga.setTargetSystemKind(targetSystemKind);
		// Let us set the artifactLastModifiedDate to current time always.
		String artifactLastModifiedDate = DateUtil.format(new Date());
		ga.setArtifactLastModifiedDate(artifactLastModifiedDate);

		// Let us add the following fields into the Generic artifact.
		// These are the typical fields that can be found in any SFEE
		// tracker.
		// 1. Title
		// 2. Summary
		// 3. Status
		// 4. Priority
		// 5. Assigned To
		
		GenericArtifactField titleField = ga.addNewField("Title", "Title", GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD);
		titleField.setFieldValueHasChanged(true);
		titleField.setFieldValueType(GenericArtifactField.FieldValueTypeValue.STRING);
		titleField.setFieldValue("Sample artifact");
		titleField.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
		
		GenericArtifactField summaryField = ga.addNewField("Summary", "Summary", GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD);
		summaryField.setFieldValueHasChanged(true);
		summaryField.setFieldValueType(GenericArtifactField.FieldValueTypeValue.STRING);
		summaryField.setFieldValue("This is the summary of the Sample artifact");
		summaryField.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
		
		GenericArtifactField statusField = ga.addNewField("Status", "Status", GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD);
		statusField.setFieldValueHasChanged(true);
		statusField.setFieldValueType(GenericArtifactField.FieldValueTypeValue.STRING);
		statusField.setFieldValue("Open");
		statusField.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
		
		GenericArtifactField priorityField = ga.addNewField("Priority", "Priority", GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD);
		priorityField.setFieldValueHasChanged(true);
		priorityField.setFieldValueType(GenericArtifactField.FieldValueTypeValue.STRING);
		priorityField.setFieldValue("Highest");
		priorityField.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
		
		GenericArtifactField assignedToField = ga.addNewField("Assigned To", "Assigned To", GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD);
		assignedToField.setFieldValueHasChanged(true);
		assignedToField.setFieldValueType(GenericArtifactField.FieldValueTypeValue.USER);
		assignedToField.setFieldValue("martian");
		assignedToField.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
		
		//Let us add our sole GenericArtifact object into a list and return it
		List<GenericArtifact> gaList = new ArrayList<GenericArtifact>();
		gaList.add(ga);
		return gaList;
	}

	@Override
	public List<GenericArtifact> getArtifactDependencies(Document syncInfo,
			String artifactId) {
		// We are not going to worry about shipping the artifact
		// dependencies. So let us return an empty GenericArtifact list
		List<GenericArtifact> dependenciesGAList = new ArrayList<GenericArtifact>();
		return dependenciesGAList;
	}

	@Override
	public List<String> getChangedArtifacts(Document syncInfo) {
		// Let us return a single artifact in this method.
		// We are signalling the CCF that we have an artifact to be
		// shipped to the target system.
		String artifactId = "1";
		List<String> artifactsList = new ArrayList<String>();
		artifactsList.add(artifactId);
		return artifactsList;
	}

}
