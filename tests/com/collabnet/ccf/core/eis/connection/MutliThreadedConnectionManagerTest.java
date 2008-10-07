package com.collabnet.ccf.core.eis.connection;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;
import org.jmock.core.stub.CustomStub;

/**
 * @author madhusuthanan
 *
 */
public class MutliThreadedConnectionManagerTest extends MockObjectTestCase {
	private TestResult testResult = null;
	private Thread threads[] = null;
	protected void setUp() throws Exception {
		super.setUp();
	}
	 public void run(final TestResult result) {
	      testResult = result;
	      super.run(result);
	      testResult = null;
	}
	protected void runTestCaseRunnables (final TestCaseRunnable[] runnables) {
	      if(runnables == null) {
	         throw new IllegalArgumentException("runnables is null");
	      }
	      threads = new Thread[runnables.length];
	      for(int i = 0;i < threads.length;i++) {
	         threads[i] = new Thread(runnables[i]);
	      }
	      for(int i = 0;i < threads.length;i++) {
	         threads[i].start();
	      }
	      try {
	         for(int i = 0;i < threads.length;i++) {
	            threads[i].join();
	         }
	      }
	      catch(InterruptedException ignore) {
	         System.out.println("Thread join interrupted.");
	      }
	      threads = null;
	}
	@SuppressWarnings("unchecked")
	public void testMultipleGetConnectionWithMultiThreads(){
		Mock context = new Mock(ConnectionFactory.class);
		final String systemId = "sid";
		final String systemKind = "skind";
		final String repositoryId = "rid";
		final String repositoryKind = "rkind";
		final String connectionInfo = "cinfo";
		final String credentialInfo = "crinfo";
		int numberOfThreads = 25;

		final ConnectionManager<String> manager = new ConnectionManager<String>();
		final int maxConnections = 25;
		Stub connectionFactoryStub = new CustomStub("Returns a new string"){
			private int i = 0;
			private HashSet<String> connections = new HashSet<String>();
			public Object invoke(Invocation invocation) throws Throwable {
				Method invokedMethod = invocation.invokedMethod;
				String name = invokedMethod.getName();
				List params = invocation.parameterValues;
				String returnValue = "";
				if(name.contains("createConnection")){
					for(Object param:params){
						returnValue += param.toString();
					}
					returnValue += i++;
				}
				else if(name.contains("isAlive")){
					String connection = (String) params.get(0);
					if(connections.contains(connection)){
						return true;
					}
					else{
						return false;
					}
				}
				else if(name.contains("closeConnection")){
					String connection = (String) params.get(0);
					Object closedConnection = connections.remove(connection);
					if(closedConnection == null){
						throw new ConnectionException("Trying to close a non-existent connection...!");
					}
					return null;
				}
				return returnValue;
			}
		};
		manager.setMaxConnectionsPerPool(maxConnections);
		manager.setMaxIdleTimeForConnection(50);
		manager.setScavengerInterval(100);
		manager.setConnectionFactory((ConnectionFactory<String>)context.proxy());
		int numberOfConnectionsNeeded = maxConnections;
		context.expects(atLeastOnce()).method("createConnection").with(new Constraint[]{contains(systemId),
				contains(systemKind),
				contains(repositoryId),
				contains(repositoryKind),
				contains(connectionInfo),
				contains(credentialInfo)}).will(connectionFactoryStub);
		context.expects(atLeastOnce()).method("isAlive").will(connectionFactoryStub);
		context.expects(atLeastOnce()).method("closeConnection").will(connectionFactoryStub);
		
		final Random random = new Random(1058);
		final TestCaseRunnable[] threads = new TestCaseRunnable[numberOfThreads * numberOfConnectionsNeeded];
		for(int j=0; j < numberOfThreads; j++){
			final int jTmp = j;
			for(int i=0; i < numberOfConnectionsNeeded; i++){
				final int iTmp = i;
				threads[(jTmp+1)*(iTmp+1)-1] = 
					new TestCaseRunnable(){
					String connection = null;
						public void runTestCase() throws ConnectionException{
							try {
									long randomLong1 = random.nextInt(20)*1000;
									try {
										Thread.sleep(randomLong1);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									connection = manager.getConnectionToUpdateOrExtractArtifact(systemId+jTmp, systemKind+jTmp, repositoryId+jTmp,
										repositoryKind+jTmp, connectionInfo+jTmp, credentialInfo+jTmp);
									if(connection == null){
										fail("Connection is null for " +iTmp+" "+jTmp);
									}
									long randomLong2 = random.nextInt(20)*1000;
									try {
										Thread.sleep(randomLong2);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									manager.releaseConnection(connection);
							} catch (MaxConnectionsReachedException e) {
								fail("Max connections reached at "+(iTmp+1)
										+" although the max connections configured is "+maxConnections);
							}
						}
					};
			}
		}
		this.runTestCaseRunnables(threads);
	}
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	private void handleException(final Throwable t) {
	      synchronized(testResult) {
	         if(t instanceof AssertionFailedError) {
	            testResult.addFailure(this, (AssertionFailedError)t);
	         }
	         else {
	            testResult.addError(this, t);
	         }
	      }
	   }
	   public void interruptThreads(){
		   for(Thread t: threads){
			   t.interrupt();
		   }
	   }
	public abstract class TestCaseRunnable implements Runnable {
	      /**
	       * Override this to define the test*/
	      public abstract void runTestCase()
	                 throws Throwable;
	      /**
	       * Run the test in an environment where
	       * we can handle the exceptions generated by the test method.*/
	      public void run() {
	         try {
	            runTestCase();
	         }
	         catch(Throwable t) /* Any other exception we handle and then we interrupt the other threads.*/ {
	            handleException(t);
	            interruptThreads();
	         }
	      }
	   }
}
