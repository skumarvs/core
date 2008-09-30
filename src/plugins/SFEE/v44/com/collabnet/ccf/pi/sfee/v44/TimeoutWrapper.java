package com.collabnet.ccf.pi.sfee.v44;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collabnet.ccf.core.eis.connection.ConnectionManager;
import com.vasoftware.sf.soap44.fault.InvalidSessionFault;

public class TimeoutWrapper {

	private static final Log log = LogFactory.getLog(TimeoutWrapper.class);

	/**
	 * This method determines whether the exception is timeout related and will
	 * be handled in this method or whether it should be handled by another
	 * layer
	 * 
	 * @param e
	 *            exception that was intercepted
	 * @param numberOfTries
	 *            number this method was already called for the same method
	 *            call, this will help to determine the timeout
	 * @param connectionManager
	 *            this object is used to retrieve timeout related configuration
	 *            parameters
	 * @return true if exception was handled here and method call should be
	 *         retried, false if exception should be passed to next layer
	 */
	protected boolean handleException(RemoteException e, int numberOfTries,
			ConnectionManager<Connection> connectionManager) {
		Throwable cause = e.getCause();
		if (cause instanceof java.net.SocketException
				|| cause instanceof java.net.UnknownHostException) {
			if (numberOfTries == 1) {
				// first try, long error message
				log
						.warn(
								"Network related problem occurred while calling SFEE/CSFE webservice. Try operation again",
								e);
			} else if (numberOfTries < 7) {
				// error occurred again, short error message, go to sleep
				int timeOut = (int) Math.pow(2, numberOfTries);
				log.warn("Network related error occurred again ("
						+ e.getMessage() + "), sleeping for " + timeOut
						+ " seconds.");
				try {
					Thread.sleep(timeOut * 1000);
				} catch (InterruptedException e1) {
					log.error("Interrupted sleep in timeout method: ", e1);
				}
			} else {
				// error occurred more than 6 times, short error message, go to
				// sleep for two minutes
				log.warn("Network related error occurred again ("
						+ e.getMessage() + ") , sleeping for two minutes.");
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e1) {
					log.error("Interrupted sleep in timeout method: ", e1);
				}
			}
			return true;
		} else
			return false;
	}

	/**
	 * This method tries to retrieve the timed out connection and logs in again
	 * 
	 * @param sessionId
	 *            invalid session id
	 * @param e
	 *            detailed error message of time out login
	 * @param numberOfTries
	 *            number of times this method or another exception handling
	 *            method has been called
	 * @param connectionManager
	 *            object to retrieve connection object from
	 * @return new session id
	 * @throws RemoteException
	 */
	protected String retryLogin(String sessionId, InvalidSessionFault e,
			int numberOfTries, ConnectionManager<Connection> connectionManager)
			throws InvalidSessionFault {
		if (numberOfTries == 1) {
			// first try, long error message
			log
					.warn(
							"Login related problem occurred while calling SFEE/CSFE webservice. Try logging in again",
							e);
		} else {
			log.warn("Login related error occured, trying relogin: "
					+ e.getMessage());
		}
		// first try to retrieve connection associated with invalid session id
		Connection connection = connectionManager
				.lookupRegisteredConnection(sessionId);
		if (connection == null) {
			log
					.error("Could not retrieve connection associated with invalid session id, cannot login again.");
			throw e;
		}
		String newSessionId = connection.getSessionId();
		if (newSessionId.equals(sessionId)) {
			// it seems as if a previous call to this method already substituted
			// the session id
			// if this session id is not valid any more, we will be called again
			// after the next try
			return newSessionId;
		} else {
			// try to relogin
			// we have to catch exceptions here in order not to introduce
			// unknown exceptions
			try {
				newSessionId = connection.relogin();
				// register new session id
				connectionManager.registerConnection(newSessionId, connection);
			} catch (RemoteException e1) {
				log.error("While trying to relogin an error occured:", e1);
				// throw the exception we have not been able to fix
				throw e;
			}
			return newSessionId;
		}
	}
}
