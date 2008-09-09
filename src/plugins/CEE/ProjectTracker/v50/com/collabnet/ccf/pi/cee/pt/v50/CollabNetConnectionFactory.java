package com.collabnet.ccf.pi.cee.pt.v50;

import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;

import com.collabnet.ccf.core.eis.connection.ConnectionException;
import com.collabnet.ccf.core.eis.connection.ConnectionFactory;
import com.collabnet.tracker.core.TrackerClientManager;
import com.collabnet.tracker.core.TrackerWebServicesClient;

public class CollabNetConnectionFactory implements ConnectionFactory<TrackerWebServicesClient> {
	public static final String PARAM_DELIMITER = ":";
	public void closeConnection(TrackerWebServicesClient connection) throws ConnectionException {
		// TODO Auto-generated method stub
		// Seems nothing to do
	}

	public TrackerWebServicesClient createConnection(String systemId, String systemKind,
			String repositoryId, String repositoryKind, String connectionInfo,
			String credentialInfo) throws ConnectionException {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(repositoryId)){
			throw new IllegalArgumentException("Repository Id cannot be null");
		}
		
		String username = null;
		String password = null;
		if(credentialInfo != null){
			String[] splitCredentials = credentialInfo.split(PARAM_DELIMITER);
			if(splitCredentials != null){
				if(splitCredentials.length == 1){
					username = splitCredentials[0];
					password = "";
				}
				else if(splitCredentials.length == 2){
					username = splitCredentials[0];
					password = splitCredentials[1];
				}
				else {
					throw new IllegalArgumentException("Credentials info is not valid.");
				}
			}
		}
		String projectName = null;
		if(repositoryId != null){
			String[] splitProjectName = repositoryId.split(PARAM_DELIMITER);
			if(splitProjectName != null){
				if(splitProjectName.length >= 1){
					projectName = splitProjectName[0];
				}
				else {
					throw new IllegalArgumentException("Repository id is not valid."
							+" Could not extract project name from repository id");
				}
			}
		}
		String url = connectionInfo.substring(0, connectionInfo.indexOf("://")+3)+
					projectName + "." +
					connectionInfo.substring(connectionInfo.indexOf("://")+3);
		TrackerWebServicesClient twsclient = null;
		try {
			twsclient = TrackerClientManager.getInstance().createClient(url,
					username, password,null,null, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ConnectionException("Exception when trying to get the Web Services client", e);
		}
		return twsclient;
	}

	public boolean isAlive(TrackerWebServicesClient connection) {
		return true;
	}

}