package porthos.app.swap;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porthos.hyperledger.ConfigPorthos;
import porthos.hyperledger.InvokeQuery;
import porthos.hyperledger.app.client.CAClient;
import porthos.hyperledger.app.client.ChannelClient;
import porthos.hyperledger.app.client.FabricClient;
import porthos.hyperledger.app.user.UserContext;
import porthos.hyperledger.app.util.Util;

public class Bob {
	private static final Logger log = LoggerFactory.getLogger(Bob.class);
	
	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	private static FabricClient fabClient;
	private static Channel channel;
	private static ChaincodeID ccid;
	
	public static void main(String[] args) throws Exception {
		// This class is Bob's action made programmatically for the swap contract
		// Bob's action is to commit Oranges towards Alice on the Hyperledger blockchain

		invokeContract();
		
		log.info("Bob's commit is complete");
	}
	
	
	private static void invokeContract()
			throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException,
			CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException, Exception,
			TransactionException, ProposalException, InterruptedException, ExecutionException {
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

		issueAsset("A1", "Orange", "A bag full of oranges", "Bob"); 

		invoke();
	}

	private static void invoke()
			throws InvalidArgumentException, ProposalException, InterruptedException, ExecutionException {
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		request.setChaincodeID(ccid);

		request.setFcn("p2Commit_commit");
		String[] arguments = { "C1", "Bob", "Alice", "A1" };

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
		log.info(cf.toString());
		cf.get();
	}

	private static void issueAsset(String ref, String type, String desc, String owner)
			throws InvalidArgumentException, ProposalException, InterruptedException, ExecutionException {
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		request.setChaincodeID(ccid);

		request.setFcn("issueAsset");

		String[] arguments = { ref, type, desc, owner};
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
		log.info(cf.toString());
		cf.get();
	}
}
