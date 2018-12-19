package porthos.hyperledger;


import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porthos.Constants.Blockchain;
import porthos.GateManager;
import porthos.hyperledger.app.client.CAClient;
import porthos.hyperledger.app.client.ChannelClient;
import porthos.hyperledger.app.client.FabricClient;
import porthos.hyperledger.app.user.UserContext;

public class HyperledgerEventListener extends Thread {

	private static final Logger log = LoggerFactory.getLogger(HyperledgerEventListener.class);

	public static void main(String[] args) {
		new HyperledgerEventListener().start();
	}
	
	public HyperledgerEventListener() {
		
		try {
			String caUrl = ConfigPorthos.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);

			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(ConfigPorthos.ADMIN);
			adminUserContext.setAffiliation(ConfigPorthos.ORG1);
			adminUserContext.setMspId(ConfigPorthos.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(ConfigPorthos.ADMIN, ConfigPorthos.ADMIN_PASSWORD);

			FabricClient fabClient = new FabricClient(adminUserContext);

			ChannelClient channelClient = fabClient.createChannelClient(ConfigPorthos.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			Peer peer = fabClient.getInstance().newPeer(ConfigPorthos.ORG1_PEER_0, ConfigPorthos.ORG1_PEER_0_URL);
			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(ConfigPorthos.ORDERER_NAME, ConfigPorthos.ORDERER_URL);
			channel.addPeer(peer);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();

			subscribeEvents(fabClient.getInstance(), channel);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void subscribeEvents(HFClient client, Channel channel) {

		try {
			final String channelName = channel.getName();
			log.info("Running channel " + channelName);

			channel.registerChaincodeEventListener(Pattern.compile(".*"),
					Pattern.compile(".*"),
					(handle, blockEvent, chaincodeEvent) -> {

						String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : blockEvent.getEventHub().getName();
						log.info("RECEIVED Chaincode event with handle: " + handle + "\n, chaincode Id: "+ chaincodeEvent.getChaincodeId() 
						+"\n, chaincode event name: "+chaincodeEvent.getEventName()+"\n, transaction id: "+chaincodeEvent.getTxId()+
						"\n, event payload: \""+new String(chaincodeEvent.getPayload())+"\"\n, from eventhub: " + es);
						
						if (chaincodeEvent.getEventName().equals("CCCEvent")) {
							JSONObject jsonobject = new JSONObject(chaincodeEvent.getPayload());
						    String bcname = jsonobject.getString("masterBcName");
						    String address = jsonobject.getString("masterContractAddress");
						    String methodName = jsonobject.getString("methodName");
							
							GateManager.getInstance().crosschainCall(Blockchain.valueOf(bcname), address, methodName);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
