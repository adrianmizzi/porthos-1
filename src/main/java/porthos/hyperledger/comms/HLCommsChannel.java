package porthos.hyperledger.comms;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porthos.hyperledger.ConfigPorthos;
import porthos.hyperledger.app.client.CAClient;
import porthos.hyperledger.app.client.ChannelClient;
import porthos.hyperledger.app.client.FabricClient;
import porthos.hyperledger.app.user.UserContext;

public class HLCommsChannel {
	

	private static final Logger log = LoggerFactory.getLogger(HLCommsChannel.class);


	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	
	private static HLCommsChannel instance = new HLCommsChannel();
	
	private static FabricClient fabClient;
	private static Channel channel;
	private static ChaincodeID ccid;

	public static HLCommsChannel getInstance() {
		return instance;
	}

	// private constructor
	private HLCommsChannel() {
		
		try {
			String caUrl = ConfigPorthos.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(ConfigPorthos.ADMIN);
			adminUserContext.setAffiliation(ConfigPorthos.ORG1);
			adminUserContext.setMspId(ConfigPorthos.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(ConfigPorthos.ADMIN, ConfigPorthos.ADMIN_PASSWORD);

			fabClient = new FabricClient(adminUserContext);

			ChannelClient channelClient = fabClient.createChannelClient(ConfigPorthos.CHANNEL_NAME);
			channel = channelClient.getChannel();
			Peer peer = fabClient.getInstance().newPeer(ConfigPorthos.ORG1_PEER_0, ConfigPorthos.ORG1_PEER_0_URL);
			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(ConfigPorthos.ORDERER_NAME, ConfigPorthos.ORDERER_URL);
			channel.addPeer(peer);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();

			ccid = ChaincodeID.newBuilder().setName(ConfigPorthos.CHAINCODE_NAME).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openGate(String gateName)
			throws InvalidArgumentException, ProposalException, InterruptedException, ExecutionException {
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		request.setChaincodeID(ccid);

		request.setFcn("openGate");

		String[] arguments = { gateName };
		request.setArgs(arguments);

		request.setProposalWaitTime(180000);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
		// in transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.
		request.setTransientMap(tm2);

		Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);

		for (ProposalResponse pres : responses) {
			log.info(pres.getMessage());
			log.info(new String(pres.getChaincodeActionResponsePayload()));
		}

		CompletableFuture<TransactionEvent> cf = channel.sendTransaction(responses);
		cf.get();
		log.info(cf.toString());
	}

	
}
