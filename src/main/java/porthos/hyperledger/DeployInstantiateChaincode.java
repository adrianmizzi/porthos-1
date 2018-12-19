package porthos.hyperledger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porthos.hyperledger.app.client.FabricClient;
import porthos.hyperledger.app.user.UserContext;
import porthos.hyperledger.app.util.Util;

public class DeployInstantiateChaincode {

	private static final Logger log = LoggerFactory.getLogger(DeployInstantiateChaincode.class);

	public static void main(String[] args) throws Exception {
		DeployInstantiateChaincode dic = new DeployInstantiateChaincode();
		dic.deployAndInstantiate();
	}

	public void deployAndInstantiate() throws Exception {
		HFClient client = HFClient.createNewInstance();
		CryptoSuite cs = CryptoSuite.Factory.getCryptoSuite();
		client.setCryptoSuite(cs);

		Util.cleanUp();

		// Enrollment of Admin Org2
		UserContext admin = new UserContext();

		File pkFolder1 = new File(ConfigPorthos.ORG1_USR_ADMIN_PK);
		File[] pkFiles1 = pkFolder1.listFiles();
		File certFolder = new File(ConfigPorthos.ORG1_USR_ADMIN_CERT);
		File[] certFiles = certFolder.listFiles();
		Enrollment enrollOrg1Admin = Util.getEnrollment(ConfigPorthos.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
				ConfigPorthos.ORG1_USR_ADMIN_CERT, certFiles[0].getName());
		admin.setEnrollment(enrollOrg1Admin);
		admin.setMspId(ConfigPorthos.ORG1_MSP);
		admin.setName(ConfigPorthos.ADMIN);

		// Enrollment of Admin Org2
		UserContext admin2 = new UserContext();
		File pkFolder2 = new File(ConfigPorthos.ORG2_USR_ADMIN_PK);
		File[] pkFiles2 = pkFolder2.listFiles();
		File certFolder2 = new File(ConfigPorthos.ORG2_USR_ADMIN_CERT);
		File[] certFiles2 = certFolder2.listFiles();
		Enrollment enrollOrg2Admin = Util.getEnrollment(ConfigPorthos.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
				ConfigPorthos.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
		admin2.setEnrollment(enrollOrg2Admin);
		admin2.setMspId(ConfigPorthos.ORG2_MSP);
		admin2.setName(ConfigPorthos.ADMIN);

		// Create Fabric Client
		FabricClient fabClient = new FabricClient(admin);

		Peer peer0_org1 = fabClient.getInstance().newPeer(ConfigPorthos.ORG1_PEER_0, ConfigPorthos.ORG1_PEER_0_URL);
		Peer peer1_org1 = fabClient.getInstance().newPeer(ConfigPorthos.ORG1_PEER_1, ConfigPorthos.ORG1_PEER_1_URL);
		Peer peer0_org2 = fabClient.getInstance().newPeer(ConfigPorthos.ORG2_PEER_0, ConfigPorthos.ORG2_PEER_0_URL);
		Peer peer1_org2 = fabClient.getInstance().newPeer(ConfigPorthos.ORG2_PEER_1, ConfigPorthos.ORG2_PEER_1_URL);


		List<Peer> org1Peers = new ArrayList<Peer>();
		org1Peers.add(peer0_org1);
		org1Peers.add(peer1_org1);

		List<Peer> org2Peers = new ArrayList<Peer>();
		org2Peers.add(peer0_org2);
		org2Peers.add(peer1_org2);

		List<Peer> allPeers = new ArrayList<Peer>();
		allPeers.addAll(org1Peers);
		allPeers.addAll(org2Peers);

		Channel mychannel = fabClient.getInstance().newChannel(ConfigPorthos.CHANNEL_NAME);

		Orderer orderer = fabClient.getInstance().newOrderer(ConfigPorthos.ORDERER_NAME, ConfigPorthos.ORDERER_URL);
		mychannel.addOrderer(orderer);
		for (Peer peer : allPeers) {
			mychannel.addPeer(peer);
		}
		mychannel.initialize();

		// Deploy Chain Code on Org1
		Collection<ProposalResponse> response = fabClient.deployChainCode(ConfigPorthos.CHAINCODE_NAME,
				ConfigPorthos.CHAINCODE_PATH, ConfigPorthos.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
				ConfigPorthos.CHAINCODE_VERSION, org1Peers);

		for (ProposalResponse res : response) {
			log.info(ConfigPorthos.CHAINCODE_NAME + "- Chain code deployment " + res.getStatus());
		}

		// Change admin to org2
		fabClient.getInstance().setUserContext(admin2);

		// Deploy Chain Code on Org2
		response = fabClient.deployChainCode(ConfigPorthos.CHAINCODE_NAME,
				ConfigPorthos.CHAINCODE_PATH, ConfigPorthos.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
				ConfigPorthos.CHAINCODE_VERSION, org2Peers);

		for (ProposalResponse res : response) {
			log.info(ConfigPorthos.CHAINCODE_NAME + "- Chain code deployment " + res.getStatus());
		}

		Thread.sleep(3000);

		instantiateChaincode(fabClient, mychannel);
	}

	private void instantiateChaincode(FabricClient fabClient, Channel mychannel)
			throws InvalidArgumentException, ProposalException, Exception {
		String[] arguments = { "" };

		InstantiateProposalRequest instantiateProposalRequest = fabClient.getInstance().newInstantiationProposalRequest();
		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(ConfigPorthos.CHAINCODE_NAME)
				.setVersion(ConfigPorthos.CHAINCODE_VERSION).setPath(ConfigPorthos.CHAINCODE_PATH);
		ChaincodeID ccid = chaincodeIDBuilder.build();

		log.info("Instantiating Chaincode ID " + ConfigPorthos.CHAINCODE_NAME + " on channel " + mychannel.getName());

		instantiateProposalRequest.setProposalWaitTime(180000);
		instantiateProposalRequest.setChaincodeID(ccid);
		instantiateProposalRequest.setChaincodeLanguage(Type.GO_LANG);
		instantiateProposalRequest.setFcn("init");

		instantiateProposalRequest.setArgs(arguments);

		Collection<ProposalResponse> responses = mychannel.sendInstantiationProposal(instantiateProposalRequest);
		for (ProposalResponse res : responses) {
			if (res.getStatus() != ChaincodeResponse.Status.SUCCESS) {
				log.error(res.getMessage());
				throw new Exception(res.getMessage());
			}
		}

		CompletableFuture<TransactionEvent> cf = mychannel.sendTransaction(responses);

		log.info("Chaincode " + ConfigPorthos.CHAINCODE_NAME + " on channel " + mychannel.getName() + " instantiation " + cf);

		cf.get();

		for (ProposalResponse res : responses) {
			log.info(ConfigPorthos.CHAINCODE_NAME + "- Chain code instantiation " + res.getStatus() + " " + res.getMessage());
		}
	}
}
