package org.cyk.utility.log.jul;

import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.log.Log;
import org.cyk.utility.log.LogLevel;
import org.cyk.utility.test.arquillian.AbstractArquillianUnitTest;
import org.cyk.utility.test.arquillian.AbstractArquillianUnitTestWithDefaultDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class LogJulUnitTest extends AbstractArquillianUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void logWithoutParameter() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).getMessageBuilder(Boolean.TRUE).addParameter("this is info without parameters").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.INFO).setMessage("this is info without parameters"), logEventRepository);
	}
	
	@Test
	public void logWithParameter() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).getMessageBuilder(Boolean.TRUE).addParameter("this is info with parameter").addParameter("p1", "myvalue").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.INFO).setMessage("this is info with parameter , p1=myvalue"), logEventRepository);
	}
	
	@Test
	public void logInfo() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).setLevel(LogLevel.INFO).getMessageBuilder(Boolean.TRUE).addParameter("this a info").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.INFO).setMessage("this a info"), logEventRepository);
	}
	
	//@Test TODO make it support marker. An idea is to include marker in message itself which can affect readability
	public void logInfoWithMarker() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).setLevel(LogLevel.INFO).addMarkers("M01").getMessageBuilder(Boolean.TRUE).addParameter("this is info with marker").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.INFO).setMarker("M01").setMessage("this is info with marker"), logEventRepository);
	}
	
	//@Test TODO make it support marker. An idea is to include marker in message itself which can affect readability
	public void logInfoWithMarkers() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).setLevel(LogLevel.INFO).addMarkers("MASTER","DETAIL","SPECIFIC").getMessageBuilder(Boolean.TRUE).addParameter("this is info with markers").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.INFO).setMarker("SPECIFIC").setMessage("this is info with markers"), logEventRepository);
	}
	
	@Test
	public void logInfoUsingMessageOnly() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).executeInfo("this a info message");
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.INFO).setMessage("this a info message"), logEventRepository);
	}

	@Test
	public void logTrace() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).setLevel(LogLevel.TRACE).getMessageBuilder(Boolean.TRUE).addParameter("this a trace").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.TRACE).setMessage("this a trace"), logEventRepository);
	}
	
	@Test
	public void logDebug() {
		assertionHelper.assertEqualsLogEventCount(0, logEventRepository);
		__inject__(Log.class).setLevel(LogLevel.DEBUG).getMessageBuilder(Boolean.TRUE).addParameter("this a debug").getParent().execute();
		assertionHelper.assertEqualsLastLogEventProperties(new Properties().setLogLevel(LogLevel.DEBUG).setMessage("this a debug"), logEventRepository);
	}
	
	/* Deployment */

	@Deployment
	public static JavaArchive createDeployment() {
		return AbstractArquillianUnitTestWithDefaultDeployment.createJavaArchiveDeployment(/*"org/cyk/utility/log/jul/beans-with-alternatices.xml"*/);
	}

}
