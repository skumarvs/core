package com.collabnet.ccf.pi.qc.v90;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.openadaptor.core.IDataProcessor;
import com.collabnet.ccf.core.AbstractReader;

import com.collabnet.ccf.core.eis.connection.ConnectionManager;
import com.collabnet.ccf.core.eis.connection.MaxConnectionsReachedException;
import com.collabnet.ccf.core.ga.GenericArtifact;
import com.collabnet.ccf.core.ga.GenericArtifactHelper;
import com.collabnet.ccf.core.ga.GenericArtifactParsingException;
import com.collabnet.ccf.pi.qc.v90.api.IConnection;

/**
 * QCReader is responsible for reading the defect data from
 * HP Quality Center systems for a domain & project combination.
 * 
 * @author Venugopal Ananthakrishnan
 *
 */
public class QCReader extends AbstractReader  implements
		IDataProcessor {
    
	private static final Log log = LogFactory.getLog(QCReader.class);
	
	private boolean isDry=true;

	private Object readerContext;

	private QCDefectHandler defectHandler;
	private QCAttachmentHandler attachmentHandler;
	private ConnectionManager<IConnection> connectionManager = null;
	
	private String serverUrl;

	private String userName;

	private String password;
	
	private static int testCount = 0;
	private static int bufferCount = 0;
	private static Object[] result = {};
	
	public QCReader(String id) {
	   // super(id);
	}
	/**
	 * Deprecated process method. Since QCReader extends AbstractReader, the process method of the AbstractReader is executed in
	 * the openadaptor wiring.
	 * 
	 */
	public Object[] processDeprecated(Object data) {
		// TODO evaluate data to decide which items to fetch again
		if (!(data instanceof Document)) {
			log.error("Supplied data not in the expected dom4j format: "+data);
			return null;
		}
			
		Document document=(Document) data;
		if(document!=null) log.info(document.getText());
		
		String sourceArtifactId = getSourceArtifactId(document); 
		String sourceRepositoryId = getSourceRepositoryId(document);
		String sourceRepositoryKind = getSourceRepositoryKind(document);
		String sourceSystemId = getSourceSystemId(document);
		String sourceSystemKind = getSourceSystemKind(document);
		String targetRepositoryId = getTargetRepositoryId(document);
		String targetRepositoryKind = getTargetRepositoryKind(document);
		String targetSystemId = getTargetSystemId(document);
		String targetSystemKind = getTargetSystemKind(document);
		
		String fromTimestamp = getFromTime(document);
		String fromTime = convertIntoString(fromTimestamp);
		String transactionId = getTransactionId(document);
		
		IConnection connection = null;
		try {
			connection = connect(sourceSystemId, sourceSystemKind, sourceRepositoryId,
					sourceRepositoryKind, serverUrl,
					userName + QCConnectionFactory.PARAM_DELIMITER + password);
			//qcc.connectProjectEx(getDomain(), getProjectName(), getUserName(), getPassword());
		} catch (Exception e) {
			// TODO Declare exception so that it can be processed by OA exception handler
			log.error("Could not log into QC", e);
			return null;
		}
		// Fix these time operations
		log.error(fromTime);
		if(testCount==0) {
		result=readModifiedDefects(transactionId, fromTime, sourceArtifactId, sourceRepositoryId,
				sourceRepositoryKind, sourceSystemId, sourceSystemKind, targetRepositoryId,
				targetRepositoryKind, targetSystemId, targetSystemKind, connection);
		disconnect(connection);
		bufferCount = result.length;
		testCount=bufferCount;
		}
		log.info("Testing across multiple runs, #################: bufferCount="+bufferCount+", testCunt="+testCount);
		
		if(result!=null) {
		Object[] oneResult = {result[bufferCount-testCount]};
		result[bufferCount-testCount] = null;
		testCount-=1;
		return oneResult;
		}
		else {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		
	}

	/**
	 * Fetches the defect IDs that are changed depending on the synchronization parameters.
	 * 
	 * @param syncInfo
	 *            Document given by the polling reader of QC system containing the source & target system and
	 *            synchronization information.
	 *             
	 * @return List<String>
	 * 			  List of strings that contains the defect ID(s)	
	 * 
	 */
	@Override
	public List<String> getChangedArtifacts(Document syncInfo) {
		String sourceArtifactId = getSourceArtifactId(syncInfo); 
		String sourceRepositoryId = getSourceRepositoryId(syncInfo);
		String sourceRepositoryKind = getSourceRepositoryKind(syncInfo);
		String sourceSystemId = getSourceSystemId(syncInfo);
		String sourceSystemKind = getSourceSystemKind(syncInfo);
		String targetRepositoryId = getTargetRepositoryId(syncInfo);
		String targetRepositoryKind = getTargetRepositoryKind(syncInfo);
		String targetSystemId = getTargetSystemId(syncInfo);
		String targetSystemKind = getTargetSystemKind(syncInfo);
		
		String fromTimestamp = getFromTime(syncInfo);
		String fromTime = convertIntoString(fromTimestamp);
		String transactionId = getTransactionId(syncInfo);
		IConnection connection = null;
		try {
			connection = connect(sourceSystemId, sourceSystemKind, sourceRepositoryId,
					sourceRepositoryKind, serverUrl,
					userName + QCConnectionFactory.PARAM_DELIMITER + password);
		} catch (Exception e) {
			// TODO Declare exception so that it can be processed by OA exception handler
			log.error("Could not log into QC", e);
			return null;
		}
		List<String> artifactIds = new ArrayList<String>();
		List<GenericArtifact> artifactRows = new ArrayList<GenericArtifact>();
		try {
			artifactIds = defectHandler.getLatestChangedDefects(artifactRows, connection, getUserName(), transactionId, fromTime, sourceArtifactId, sourceRepositoryId, sourceRepositoryKind, sourceSystemId, sourceSystemKind, targetRepositoryId, targetRepositoryKind, targetSystemId, targetSystemKind);
		} catch (Exception e) {
			// TODO Throw an exception?
			log.error("During the artifact retrieval process to SFEE, an error occured",e);
			return artifactIds;
		} finally {
			this.disconnect(connection);
		}
		/*if(artifactRows != null){
			for(GenericArtifact artifact:artifactRows){
				String artifactId = artifact.getSourceArtifactId();
				if(!artifactIds.contains(artifactId))
					artifactIds.add(artifactId);
			}
		}*/
		return artifactIds;
	}
	
	
	/**
	 * Fetches the attachment(s) for a given artifactId from the system.	 * 
	 * 
	 * @param syncInfo
	 *            Document given by the polling reader of QC system containing the source & target system and
	 *            synchronization information.
	 * @param artifactId
	 *            A string that represents the defectId for which the attachments need to be fetched.            
	 * @return List<GenericArtifact>
	 * 			  List of GenericArtifact Java Object that contains the complete information abou the attachment(s)	
	 * 
	 */
	@Override
	public List<GenericArtifact> getArtifactAttachments(Document syncInfo,
			String artifactId) {
		String sourceArtifactId = artifactId; 
		String sourceRepositoryId = getSourceRepositoryId(syncInfo);
		String sourceRepositoryKind = getSourceRepositoryKind(syncInfo);
		String sourceSystemId = getSourceSystemId(syncInfo);
		String sourceSystemKind = getSourceSystemKind(syncInfo);
		String targetRepositoryId = getTargetRepositoryId(syncInfo);
		String targetRepositoryKind = getTargetRepositoryKind(syncInfo);
		String targetSystemId = getTargetSystemId(syncInfo);
		String targetSystemKind = getTargetSystemKind(syncInfo);
		
		String fromTimestamp = getFromTime(syncInfo);
		String fromTime = convertIntoString(fromTimestamp);
		String transactionId = getTransactionId(syncInfo);
		IConnection connection = null;
		try {
			connection = connect(sourceSystemId, sourceSystemKind, sourceRepositoryId,
					sourceRepositoryKind, serverUrl,
					userName + QCConnectionFactory.PARAM_DELIMITER + password);
		} catch (Exception e) {
			// TODO Declare exception so that it can be processed by OA exception handler
			log.error("Could not log into QC", e);
			return null;
		}
		List<String> artifactIds = new ArrayList<String>();
		artifactIds.add(artifactId);
		List<GenericArtifact> attachments = new ArrayList<GenericArtifact>();
		try {
			attachments = attachmentHandler.getLatestChangedAttachments(attachments, connection, getUserName(), transactionId, fromTime, sourceArtifactId, sourceRepositoryId, sourceRepositoryKind, sourceSystemId, sourceSystemKind, targetRepositoryId, targetRepositoryKind, targetSystemId, targetSystemKind, this.getMaxAttachmentSizePerArtifact());
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		} finally {
			this.disconnect(connection);
		}
		return attachments;
	}
	
	/**
	 * Fetches the defect(s) data for a given artifactId from the system. In the case of shipping only the latest state of a defect, 
	 * it returns only 1 artifact element in the list. Whereas in the case of shipping the history or incremental updates, it 
	 * ships the history information as individual GenericArtifacts. 
	 * 
	 * @param syncInfo
	 *            Document given by the polling reader of QC system containing the source & target system and
	 *            synchronization information.
	 * @param artifactId
	 *            A string that represents the defectId for which the defects need to be fetched.            
	 * @return List<GenericArtifact>
	 * 			  List of GenericArtifact Java Object that contains the complete information about the defect(s)	
	 * 
	 */
	@Override
	public List<GenericArtifact> getArtifactData(Document syncInfo,
			String artifactId) {
		String sourceArtifactId = artifactId; 
		String sourceRepositoryId = getSourceRepositoryId(syncInfo);
		String sourceRepositoryKind = getSourceRepositoryKind(syncInfo);
		String sourceSystemId = getSourceSystemId(syncInfo);
		String sourceSystemKind = getSourceSystemKind(syncInfo);
		String targetRepositoryId = getTargetRepositoryId(syncInfo);
		String targetRepositoryKind = getTargetRepositoryKind(syncInfo);
		String targetSystemId = getTargetSystemId(syncInfo);
		String targetSystemKind = getTargetSystemKind(syncInfo);
		
		String fromTimestamp = getFromTime(syncInfo);
		String fromTime = convertIntoString(fromTimestamp);
		String syncInfoTransactionId = getTransactionId(syncInfo);
		IConnection connection = null;
		try {
			connection = connect(sourceSystemId, sourceSystemKind, sourceRepositoryId,
					sourceRepositoryKind, serverUrl,
					userName + QCConnectionFactory.PARAM_DELIMITER + password);
		} catch (Exception e) {
			// TODO Declare exception so that it can be processed by OA exception handler
			log.error("Could not log into QC", e);
			return null;
		}
		ArrayList<GenericArtifact> modifiedDefectArtifacts = new ArrayList<GenericArtifact>();
		try {
			List<Object> transactionIdAndAttachOperation = defectHandler.getTxnIdAndAuDescription(
					artifactId, syncInfoTransactionId, connection, getUserName());
			if(transactionIdAndAttachOperation==null)
				return null;
			String lastTransactionId = (String) transactionIdAndAttachOperation
					.get(0);
			QCDefect latestDefect = defectHandler.getDefectWithId(connection, Integer.parseInt(artifactId));
			GenericArtifact latestDefectArtifact = latestDefect
					.getGenericArtifactObject(connection, lastTransactionId, artifactId,
							null);
			//if (latestDefectArtifact == null)
				//return null;
			latestDefectArtifact = defectHandler.getArtifactAction(latestDefectArtifact, connection, syncInfoTransactionId, 
					lastTransactionId, Integer.parseInt(artifactId), fromTime);
			latestDefectArtifact
					.setArtifactMode(GenericArtifact.ArtifactModeValue.COMPLETE);
			latestDefectArtifact
					.setArtifactType(GenericArtifact.ArtifactTypeValue.PLAINARTIFACT);
			latestDefectArtifact.setErrorCode("ok");
			latestDefectArtifact.setIncludesFieldMetaData(GenericArtifact.IncludesFieldMetaDataValue.FALSE);

			sourceArtifactId = defectHandler.getBugIdValueFromGenericArtifactInDefectHandler(
					latestDefectArtifact, "BG_BUG_ID");
			latestDefectArtifact = defectHandler.assignValues(latestDefectArtifact,
					sourceArtifactId, sourceRepositoryId, sourceRepositoryKind,
					sourceSystemId, sourceSystemKind, targetRepositoryId,
					targetRepositoryKind, targetSystemId, targetSystemKind,
					lastTransactionId);
			modifiedDefectArtifacts.add(latestDefectArtifact);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.disconnect(connection);
		}
		return modifiedDefectArtifacts;
	}

	/**
	 * TO BE DONE: Fetches the dependencies that are changed depending on the synchronization parameters.
	 * 
	 * @param syncInfo
	 *            Document given by the polling reader of QC system containing the source & target system and
	 *            synchronization information.
	 *             
	 * @return List<GenericArtifact>
	 * 			  List of strings that contains the GenericArtifact Java objects containing the complete information 
	 * 			  about the dependencies.	
	 * 
	 */
	@Override
	public List<GenericArtifact> getArtifactDependencies(Document syncInfo,
			String artifactId) {
		// TODO Auto-generated method stub
		return new ArrayList<GenericArtifact>();
	}
	
	
	
	
	/**
	 * Disconnects from the QC using the ConnectionManager.
	 * 
	 * @param connection
	 */
	private void disconnect(IConnection connection) {
		// TODO Auto-generated method stub
		connectionManager.releaseConnection(connection);
	}

	public Object getReaderContext() {
		return readerContext;
	}
	
	public boolean isDry() {
		return isDry;
	}

	/**
	 * Establish a connection with QC system
	 * 
	 * @param systemId
	 *            Id indicating a QC system.
	 * @param systemKind
	 *            Indicates whether it is a DEFECT or TEST and so on.
	 * @param repositoryId
	 *            Specifies the name of DOMAIN and PROJECT in QC system to which connection needs to established.
	 * @param repositoryKind
	 *            Indicates which version of QC like QC 9.0, QC9.2.
	 * @param connectionInfo
	 *            The server URL     
	 * @param credentialInfo
	 *            Username and password needed for establishing a connection  
	 * @return IConnection
	 *            The connection object                           
	 *
	 */
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
		isDry=false;
		return connection;
	}
	
	
	/**
	 * Deprecated method that was previously used along with the process method of this class which is also deprecated now.
	 * 
	 */
	public Object[] readModifiedDefects(String transactionId, String lastReadTime, String sourceArtifactId, 
			String sourceRepositoryId, String sourceRepositoryKind, String sourceSystemId,
			String sourceSystemKind, String targetRepositoryId, String targetRepositoryKind,
			String targetSystemId, String targetSystemKind, IConnection connection) {
		// TODO Use the information of the firstTimeImport flag
		
		//Object[] retObj = new Object[100];
		List<List<Document>> retObj=new ArrayList<List<Document>>();
		List<Document> dataRows=new ArrayList<Document>();
		List<GenericArtifact> defectRows = new ArrayList<GenericArtifact>();
		
		try {
			log.error("The transactionId coming from HQSL DB is:" + transactionId);
			defectRows = defectHandler.getChangedDefects(defectRows, connection, getUserName(), transactionId, lastReadTime, sourceArtifactId, sourceRepositoryId, sourceRepositoryKind, sourceSystemId, sourceSystemKind, targetRepositoryId, targetRepositoryKind, targetSystemId, targetSystemKind);
		} catch (Exception e) {
			// TODO Throw an exception?
			log.error("During the artifact retrieval process from QC, an error occured",e);
			return null;
		}
		
		if (defectRows==null || defectRows.size() == 0) {

			// Return an empty generic artifact
			// TODO: Should we fill in the latest from and to time?
			Document document = null;
			GenericArtifact emptyGenericArtifact = new GenericArtifact();
			emptyGenericArtifact = populateRequiredFields(emptyGenericArtifact, connection);
			
			try {
				document = GenericArtifactHelper.createGenericArtifactXMLDocument(emptyGenericArtifact);
			}
			catch (GenericArtifactParsingException gape) {
				// TODO: Handle this appropriately
				log.error("GenericArtifactParsingException while creating a GenericArtifact XML from an empty GenericArtifact object");
			}
			
			return new Object[]{};
		}
		 
		for (GenericArtifact defectRow: defectRows) {
			Document document = null;
			try {
				document = GenericArtifactHelper.createGenericArtifactXMLDocument(defectRow);
			}
			catch (GenericArtifactParsingException gape) {
				// TODO: Handle this appropriately
				log.error("GenericArtifactParsingException while creating a GenericArtifact XML from the GenericArtifact object");
			}

			if (document != null) {
				log.error(document.asXML());
			}
			else {
				log.error("DOCUMENT IS NULL");
			}
			
			dataRows.add(document);
			
		}		
		retObj.add(dataRows);
		//s.writeObject(retObj);		
		/*}
		catch (IOException e) {
		log.error("IOException in QCReader caught " + e);	
		}		
		*/
		return dataRows.toArray();
		//return retObj.toArray();
	}
	
	@SuppressWarnings("unchecked")
	public void validate(List exceptions) {
		//super.validate(exceptions);
		// Capture the return exception list and validate the exceptions
		
		defectHandler = new QCDefectHandler();
		attachmentHandler = new QCAttachmentHandler();
	}
	public String getUserName() {
		//return super.getUserName();
		return userName;
	}
	
	/**
	 * To assign values to mandatory artifact properties in the case of an empty artifact.
	 * 
	 * @param emptyGenericArtifact
	 * 					The GenericArtifact Java object to be populated
	 * @param connection
	 * 					The IConnection object
	 * @return GenericArtifact after populating the required fields.
	 * 
	 */
	public GenericArtifact populateRequiredFields(GenericArtifact emptyGenericArtifact, IConnection connection) {
		
		emptyGenericArtifact = QCConfigHelper.getSchemaFields(connection);
		emptyGenericArtifact.setArtifactMode(GenericArtifact.ArtifactModeValue.UNKNOWN);
		emptyGenericArtifact.setArtifactType(GenericArtifact.ArtifactTypeValue.UNKNOWN);
		emptyGenericArtifact.setArtifactAction(GenericArtifact.ArtifactActionValue.UNKNOWN);
		
		return emptyGenericArtifact;
	}
	/**
	 * To convert a string into QC specific timestamp
	 * 
	 * @param string newFromTime
	 * 					The string to be converted
	 * @return Timestamp 
	 * 					The converted timestamp corresponding to string
	 * 
	 */
	public Timestamp convertIntoTimestamp(String newFromTime) {
		Timestamp fromDateInTimestamp=null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = sdf.parse(newFromTime); //like 2008-01-11 22:09:54
			fromDateInTimestamp = new Timestamp(fromDate.getTime());
			log.info("After parsing, the timestamp is:"+fromDateInTimestamp);
			}
		catch(Exception e) {
			log.error("Exception while parsing the string into Date"+e);
			//throw new RuntimeException(e);
		}
		return fromDateInTimestamp;
	}
	/**
	 * To convert a QC specific timestamp into a string
	 * 
	 * @param String fromTimestamp 
	 * 					The timestamp to be converted into its corresponding string value
	 * @return string 
	 * 					The string representation of the incoming timeStamp
	 */
	public String convertIntoString(String fromTimeStamp) {
		String finalString=null;
		try {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date fromDate = sdf.parse(fromTimeStamp);
		finalString=ft.format(fromDate);
		System.out.println(finalString);
		}
		catch(Exception e) {
			log.error("Exception while parsing the string into Date"+e);
			//throw new RuntimeException(e);
		}
		return finalString;		
	}
	
	
	/**
	 * To extract values from the incoming Document
	 * 
	 * @param Document 
	 * 					
	 * @return String 
	 * 				The required value from the Document. For example, the getVersion method extracts VERSION from the Document.
	 */
	
	@SuppressWarnings("unused")
	private String getVersion(Document document) {
		Node node= document.selectSingleNode("//VERSION");
		if (node==null)
			return null;
		return node.getText();
	}
	private String getTransactionId(Document document) {
		Node node= document.selectSingleNode("//TRANSACTION_ID");
		if (node==null)
			return null;
		return node.getText();
	}	
	public String getFromTime(Document document) {
		Node node= document.selectSingleNode("//FROM_TIME");
		if (node==null)
			return null;
		return node.getText();
	}
	public String getSourceArtifactId(Document document) {
		Node node= document.selectSingleNode("//SOURCE_ARTIFACT_ID");
		if (node==null)
			return null;
		return node.getText();
	}

	public String getSourceRepositoryId(Document document) {
		Node node= document.selectSingleNode("//SOURCE_REPOSITORY_ID");
		if (node==null)
			return null;
		return node.getText();
	}
	public String getSourceRepositoryKind(Document document) {
		Node node= document.selectSingleNode("//SOURCE_REPOSITORY_KIND");
		if (node==null)
			return null;
		return node.getText();
	}

	public String getSourceSystemId(Document document) {
		Node node= document.selectSingleNode("//SOURCE_SYSTEM_ID");
		if (node==null)
			return null;
		return node.getText();
	}
	public String getSourceSystemKind(Document document) {
		Node node= document.selectSingleNode("//SOURCE_SYSTEM_KIND");
		if (node==null)
			return null;
		return node.getText();
	}
		
	public String getTargetRepositoryId(Document document) {
		Node node= document.selectSingleNode("//TARGET_REPOSITORY_ID");
		if (node==null)
			return null;
		return node.getText();
	}
	public String getTargetRepositoryKind(Document document) {
		Node node= document.selectSingleNode("//TARGET_REPOSITORY_KIND");
		if (node==null)
			return null;
		return node.getText();
	}

	public String getTargetSystemId(Document document) {
		Node node= document.selectSingleNode("//TARGET_SYSTEM_ID");
		if (node==null)
			return null;
		return node.getText();
	}
	public String getTargetSystemKind(Document document) {
		Node node= document.selectSingleNode("//TARGET_SYSTEM_KIND");
		if (node==null)
			return null;
		return node.getText();
	}

	
	public void reset(Object context) {
	}

	public QCReader() {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
