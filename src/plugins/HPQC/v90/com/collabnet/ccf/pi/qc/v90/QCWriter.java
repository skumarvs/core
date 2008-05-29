package com.collabnet.ccf.pi.qc.v90;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.openadaptor.core.IDataProcessor;
import org.openadaptor.core.exception.NullRecordException;
import org.openadaptor.core.exception.RecordFormatException;

import com.collabnet.ccf.core.eis.connection.ConnectionManager;
import com.collabnet.ccf.core.eis.connection.MaxConnectionsReachedException;
import com.collabnet.ccf.core.ga.GenericArtifact;
import com.collabnet.ccf.core.ga.GenericArtifactAttachment;
import com.collabnet.ccf.core.ga.AttachmentMetaData;
import com.collabnet.ccf.core.ga.GenericArtifactField;
import com.collabnet.ccf.core.ga.GenericArtifactHelper;
import com.collabnet.ccf.core.ga.GenericArtifactParsingException;
import com.collabnet.ccf.pi.qc.v90.api.IConnection;

public class QCWriter /*extends QCConnectHelper */ implements
		IDataProcessor {

	private static final Log log = LogFactory.getLog(QCWriter.class);
	private String CreateToken;
	private QCDefectHandler defectHandler;
	private QCAttachmentHandler attachmentHandler;
	private ConnectionManager<IConnection> connectionManager = null;
	
	private String serverUrl;

	private String userName;

	private String password;
    public QCWriter(String id) {
	   // super(id);
	}

	private Object[] processXMLDocument(Object data) {
		
		Document genericArtifactDocument = (Document) data;
		GenericArtifact genericArtifact = getArtifactFromDocument(genericArtifactDocument);
		Document resultDoc = null;
		attachmentHandler = new QCAttachmentHandler();
		int bugId=0;
		Boolean doesBugIdExistsInQC = false;
		//Populate the sourceArtifactId into each of the GenericArtifacts from the QCEntityService
		//This operation is done in a separate component called QCEntityService. So, the input flowing into this file is already populated
		String targetArtifactId = genericArtifact.getTargetArtifactId();
		String targetSystemId = genericArtifact.getTargetSystemId();
		String targetSystemKind = genericArtifact.getTargetSystemKind();
		String targetRepositoryId = genericArtifact.getTargetRepositoryId();
		String targetRepositoryKind = genericArtifact.getTargetRepositoryKind();
		IConnection connection = connect(targetSystemId, targetSystemKind, targetRepositoryId, targetRepositoryKind,
				serverUrl, userName + QCConnectionFactory.PARAM_DELIMITER + password);
		
		List<GenericArtifactField> allFields = genericArtifact.getAllGenericArtifactFields();
		List<GenericArtifactAttachment> allAttachments = genericArtifact.getAllGenericArtifactAttachments();
		if(allFields!=null)
			genericArtifact = concatValuesOfSameFieldNames(genericArtifact);
		allFields = genericArtifact.getAllGenericArtifactFields();
		GenericArtifact.ArtifactActionValue artifactAction = genericArtifact.getArtifactAction();
		String stringBugId = getFieldValueFromGenericArtifact(genericArtifact, "BG_BUG_ID");
		log.info("The bugId coming in is :"+stringBugId);
		
		String sourceArtifactId = genericArtifact.getSourceArtifactId();
		String sourceSystemId  = genericArtifact.getSourceSystemId();
		String sourceSystemKind = genericArtifact.getSourceSystemKind();
		String sourceRepositoryId = genericArtifact.getSourceRepositoryId();
		String sourceRepositoryKind = genericArtifact.getSourceRepositoryKind();
		String artifactType = genericArtifact.getArtifactType().toString();
		
		/*if(stringBugId!=null && (stringBugId!=null && !stringBugId.equals("")) )
			bugId = Integer.parseInt(stringBugId);
		*/
		if(targetArtifactId!=null && (targetArtifactId!=null && (!targetArtifactId.equals("unknown") && !targetArtifactId.equals("NEW"))) )
				bugId = Integer.parseInt(targetArtifactId);
		if(bugId!=0)
			doesBugIdExistsInQC = checkForBugIdInQC(bugId, connection);
		
		
		switch (artifactAction) {
			
			case CREATE: {
				//If the bugId already exists in QC, throw an error. Otherwise create it.
				if(doesBugIdExistsInQC==true) {
					if(artifactType.equalsIgnoreCase("attachment")) {
						targetArtifactId = genericArtifact.getTargetArtifactId();
						String attachmentName = getFieldValueFromGenericArtifact(genericArtifact, AttachmentMetaData.getAttachmentName());
						String contentTypeValue = getFieldValueFromGenericArtifact(genericArtifact, AttachmentMetaData.getAttachmentType());
						String attachmentSourceUrl = getFieldValueFromGenericArtifact(genericArtifact, AttachmentMetaData.getAttachmentSourceUrl());
						byte [] attachmentData = genericArtifact.getRawAttachmentData().getBytes();
						attachmentHandler.createAttachment(connection, targetArtifactId, attachmentName, contentTypeValue, attachmentData, attachmentSourceUrl);
						this.disconnect(connection);
						connection = null;
					}
					else {
						//Send this artifact to hospital
					}
					break;
				}
				else
				{
					if(artifactType.equalsIgnoreCase("plainartifact")) {
						if(targetArtifactId==null || targetArtifactId.equals("unknown") || targetArtifactId.equals("NEW")) {
						try {
						IQCDefect createdArtifact = defectHandler.createDefect(connection, allFields);
						String targetArtifactIdAfterCreation = createdArtifact.getId();
						log.info("Write Operation SUCCESSFULL!!!!! and the targetArtifactIdAfterCreation="+targetArtifactIdAfterCreation);
						// Update the QC_ENTITY_CHECK HSQL DB Table
						//@SuppressWarnings("unused")
						//Boolean status = DBHelper.updateTable(sourceArtifactId, sourceSystemId, sourceSystemKind, sourceRepositoryId, sourceRepositoryKind, targetArtifactIdAfterCreation, targetSystemId, targetSystemKind, targetRepositoryId, targetRepositoryKind);
						genericArtifact.setTargetArtifactId(targetArtifactIdAfterCreation);
						// send this artifact to RCDU (Read Connector Database Updater) indicating a success in creating the artifact
						
						
						if(allAttachments!=null && (allAttachments!=null && allAttachments.size()>0)) {
							//create the attachment per genericArtifact
							defectHandler.createAttachment(connection, targetArtifactIdAfterCreation, allAttachments);
							
						}
						}
						catch(Exception e) {
							log.error("Exception occured while creating defect in QC:"+e);
							throw new RuntimeException(e);
							//send this artifact to HOSPITAL	
						}
						finally{
							this.disconnect(connection);
							connection = null;
						}
						}
						else {
							// Now, the targetArtifactId is not null. It must be an UPDATE operation. But since the ACTION is create, send it to HOSPITAL.
							//send this artifact to HOSPITAL
							break;
						}
					}
				break;
				}
			}
				
			case UPDATE: {
				//If the bugId does not exists in QC, throw an error. Otherwise continue.
				/*if(doesBugIdExistsInQC==false) { //should be checked if doesBugIdExistsInQc==false
					//send this artifact to HOSPITAL
					break;
				}*/
				
				//if(targetArtifactId!=null || !(targetArtifactId.equals("")) || targetArtifactId.equals("unknown")) {
				if(targetArtifactId!=null || !(targetArtifactId.equals(""))) {
					try {
						
						//String targetArtifactIdFromTable = DBHelper.getTargetArtifactIdFromTable(sourceArtifactId, sourceSystemId, sourceSystemKind, sourceRepositoryId, sourceRepositoryKind, targetSystemId, targetSystemKind, targetRepositoryId, targetRepositoryKind);
						if(allFields!=null) {
							@SuppressWarnings("unused")
							IQCDefect updatedArtifact = defectHandler.updateDefect(connection, targetArtifactId, allFields, this.getUserName());
							log.info("Update Operation SUCCESSFULL!!!!! and the targetArtifactIdFromTable="+targetArtifactId);
							genericArtifact.setTargetArtifactId(targetArtifactId);
							//send this artifact to RCDU (Read COnnector Database Updater) indicating a success in updating the artifact
						}
						
						if(allAttachments!=null && (allAttachments!=null && allAttachments.size()>0)) {
							//create the attachment per genericArtifact
							defectHandler.createAttachment(connection, targetArtifactId, allAttachments);
							
						}
					}
					catch(Exception e) {
						log.error("Exception occured while updating defect in QC:"+genericArtifact.toString(),e);
						throw new RuntimeException(e);
						//send this artifact to HOSPITAL	
					}
					finally{
						this.disconnect(connection);
						connection = null;
					}
					}
					else {
						// Now, the targetArtifactId is null. It must be a CREATE operation. But since the ACTION is update, send it to HOSPITAL.
						//send this artifact to HOSPITAL
						throw new RuntimeException("The targetArtifactId is null. It must be a CREATE operation. But since the ACTION is update, sending it to HOSPITAL");
					}
				
				break;
			}
			
			case DELETE:
				break;
			case UNKNOWN: {
				break;
			}
			}
		
		try {
			resultDoc = GenericArtifactHelper.createGenericArtifactXMLDocument(genericArtifact); 
		}
		catch(GenericArtifactParsingException e) {
			log.error("Exception occured while parsing the GenericArtifact into a Document:" + e);
		}
		log.info("***"+resultDoc.asXML());
		Object[] result={resultDoc};
		disconnect(connection);
		return result;
	}
	
	public static GenericArtifact getArtifactFromDocument(Document genericArtifactDocument) {
		
		GenericArtifact genericArtifact = new GenericArtifact();
		
		try {
			genericArtifact = GenericArtifactHelper.createGenericArtifactJavaObject(genericArtifactDocument);
		}
		catch(GenericArtifactParsingException e) {
			log.error("Exception occured while parsing the Document into a GenericArtifact:" + e);
		}
		
		return genericArtifact;
	}
	
	public GenericArtifact concatValuesOfSameFieldNames(GenericArtifact genericArtifact) {
		
		List<GenericArtifactField> allFields = genericArtifact.getAllGenericArtifactFields();
		List<String> allFieldNames = new ArrayList<String>();
		for(int cnt=0; cnt < allFields.size(); cnt++) {
			
			if( !(allFieldNames.contains(allFields.get(cnt).getFieldName())) && 
					genericArtifact.getAllGenericArtifactFieldsWithSameFieldName(allFields.get(cnt).getFieldName()).size()>1) {
				List<GenericArtifactField> allSameFields = genericArtifact.getAllGenericArtifactFieldsWithSameFieldName(allFields.get(cnt).getFieldName());
				String concatinatedString = (String) allSameFields.get(0).getFieldValue();
				for(int newCnt=1; newCnt < allSameFields.size(); newCnt++) {
					concatinatedString+=";";
					concatinatedString+=(String)allSameFields.get(newCnt).getFieldValue();
				}
				genericArtifact.getAllGenericArtifactFieldsWithSameFieldName(allFields.get(cnt).getFieldName()).get(0).setFieldValue(concatinatedString);
			}
			allFieldNames.add(allFields.get(cnt).getFieldName());
			
			
		}
		
		return genericArtifact;
	}
	
	
//	public void connectProperly() {
//		
//		connect();
//		//qcc.connectProjectEx(getDomain(), getProjectName(), getUserName(), getPassword());
//		return;
//	}
	public static String getFieldValueFromGenericArtifact(GenericArtifact individualGenericArtifact, String fieldName) {
		
		String fieldValue = null;
		if(individualGenericArtifact.getAllGenericArtifactFields()!=null && individualGenericArtifact.getAllGenericArtifactFieldsWithSameFieldName(fieldName)!=null)
			fieldValue = (String) individualGenericArtifact.getAllGenericArtifactFieldsWithSameFieldName(fieldName).get(0).getFieldValue();
		
		return fieldValue;
	}
	
	public boolean checkForBugIdInQC(int bugId, IConnection connection) {
		
		QCDefect thisDefect = defectHandler.getDefectWithId(connection, bugId);
		
		if(thisDefect!=null)
			return true;
		else
			return false;
	}
	
	public IConnection connect(String systemId, String systemKind, String repositoryId,
			String repositoryKind, String connectionInfo, String credentialInfo) {
		log.info("Before calling the parent connect()");
		//super.connect();
		IConnection connection = null;
		try {
			connection = connectionManager.getConnection(systemId, systemKind, repositoryId,
					repositoryKind, connectionInfo, credentialInfo);
		} catch (MaxConnectionsReachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//isDry=false;
		return connection;
	}
	
	private void disconnect(IConnection connection) {
		// TODO Auto-generated method stub
		if(connection != null)
			connectionManager.releaseConnection(connection);
	}
	
	@SuppressWarnings("unchecked")
	public void validate(List exceptions) {
		//super.validate(exceptions);
		// Capture the return exception list and validate the exceptions

		defectHandler = new QCDefectHandler();
	}

	public Object[] process(Object data) {
		if (data == null) {
		      throw new NullRecordException("Expected Document. Null record not permitted.");
		    }

		    if (!(data instanceof Document)) {
		      throw new RecordFormatException("Expected Document. Got [" + data.getClass().getName() + "]");
		    }
		    
		    return processXMLDocument((Object) data);
	}

	public void reset(Object context) {
	}

	public String getCreateToken() {
		return CreateToken;
	}

	public void setCreateToken(String createToken) {
		CreateToken = createToken;
	}

	public QCWriter() {
		super();
	}

	public ConnectionManager<IConnection> getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(
			ConnectionManager<IConnection> connectionManager) {
		this.connectionManager = connectionManager;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
