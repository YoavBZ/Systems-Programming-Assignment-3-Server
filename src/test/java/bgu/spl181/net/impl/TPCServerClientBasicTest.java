package bgu.spl181.net.impl;

import org.junit.Test;

public class TPCServerClientBasicTest extends TPCServerClientTestTemplate {
	@Test(timeout = 50000)
	public void TPCServerClientBasicTest() {
		server = A.initiateServer("DemoTPC", A.serverPort);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Thread[] clients = A.initiateClients(1, A.serverIp, A.serverPort,
				"./src/test/resources/Basic_Server_Client_Test/", null, null);

		A.waitForClients(clients);
	}
}
