package trial2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import org.openadaptor.auxil.connector.jdbc.JDBCConnection;
import org.openadaptor.core.IDataProcessor;

import com.collabnet.ccf.core.ga.GenericArtifact;

public class MappingDBUpdater implements IDataProcessor{
	JDBCConnection jdbcConnection = null;
	String sql = "UPDATE REPOSITORY_MAPPING SET LAST_READ_TIME=? WHERE ID=(SELECT REPOSITORY_MAPPING.ID" 
					+" FROM REPOSITORY_MAPPING, REPOSITORY_INFO A, REPOSITORY_INFO B, ARTIFACT_MAPPING WHERE"
					+" A.ID=REPOSITORY_MAPPING.SOURCE_REPOSITORY_INFO_ID AND"
					+" B.ID=REPOSITORY_MAPPING.TARGET_REPOSITORY_INFO_ID AND"
					+" A.REPOSITORY_ID=? AND"
					+" B.REPOSITORY_ID=? AND"
					+" REPOSITORY_MAPPING.ID=ARTIFACT_MAPPING.MAPPING_ID AND"
					+" ARTIFACT_MAPPING.SOURCE_ARTIFACT_ID=? AND"
					+" ARTIFACT_MAPPING.TARGET_ARTIFACT_ID=?)";
			
	public Object[] process(Object data) {
		// I will expect a Generic Artifact object
		if(data instanceof GenericArtifact){
			GenericArtifact ga = (GenericArtifact) data;
			String lastModifiedDateString = ga.getArtifactLastModifiedDate();
			String sourceRepositoryID = ga.getSourceRepositoryId();
			String targetRepositoryID = ga.getTargetRepositoryId();
			String sourceArtifactID = ga.getSourceArtifactId();
			String targetArtifactID = ga.getTargetArtifactId();
			java.util.Date lastModifiedDate = null;
			lastModifiedDate = DateUtil.parse(lastModifiedDateString);
			java.sql.Timestamp time = new java.sql.Timestamp(lastModifiedDate.getTime());
			java.sql.Date sqlDate = new java.sql.Date(lastModifiedDate.getTime());
			PreparedStatement pstmt = null;;
			try {
				if (!jdbcConnection.isConnected()) {
					jdbcConnection.connect();
				}
				Connection dbConnection = jdbcConnection.getConnection();
				dbConnection.setAutoCommit(false);
				pstmt = dbConnection.prepareStatement(sql);
				pstmt.setTimestamp(1, time);
				pstmt.setString(2, sourceRepositoryID);
				pstmt.setString(3, targetRepositoryID);
				pstmt.setString(4, sourceArtifactID);
				pstmt.setString(5, targetArtifactID);
				int numRecordsAffected = pstmt.executeUpdate();
				if(numRecordsAffected != 1){
					dbConnection.rollback();
					throw new RuntimeException("How come I am updating two repository mapping....?");
				}
				else{
					dbConnection.commit();
					dbConnection.setAutoCommit(true);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			finally{
				if(pstmt != null){
					try {
						pstmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			String message = "The Mapping updater needs a GenericArtifact object";
			message += " But it got something else.";
			throw new RuntimeException(message);
		}
		// TODO Auto-generated method stub
		return null;
	}

	public void reset(Object context) {
		// TODO Auto-generated method stub
		
	}

	public void validate(List exceptions) {
		// TODO Auto-generated method stub
		
	}

	public JDBCConnection getJdbcConnection() {
		return jdbcConnection;
	}

	public void setJdbcConnection(JDBCConnection dbConnection) {
		this.jdbcConnection = dbConnection;
	}

}
