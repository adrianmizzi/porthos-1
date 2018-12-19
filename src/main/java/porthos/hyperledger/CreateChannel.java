package porthos.hyperledger;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porthos.hyperledger.app.client.FabricClient;
import porthos.hyperledger.app.user.UserContext;
import porthos.hyperledger.app.util.Util;

public class CreateChannel {

	private static final Logger log = LoggerFactory.getLogger(CreateChannel.class);

	public static void main(String[] args) {
		CreateChannel c = new CreateChannel();
		c.createChannel();
	}

	public void createChannel() {
		try {
			CryptoSuite.Factory.getCryptoSuite();
			Util.cleanUp();
			// Construct Channel
			UserContext org1Admin = new UserContext();
			File pkFolder1 = new File(ConfigPorthos.ORG1_USR_ADMIN_PK);
			File[] pkFiles1 = pkFolder1.listFiles();
			File certFolder1 = new File(ConfigPorthos.ORG1_USR_ADMIN_CERT);
			File[] certFiles1 = certFolder1.listFiles();
			Enrollment enrollOrg1Admin = Util.getEnrollment(ConfigPorthos.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
					ConfigPorthos.ORG1_USR_ADMIN_CERT, certFiles1[0].getName());
			org1Admin.setEnrollment(enrollOrg1Admin);
			org1Admin.setMspId(ConfigPorthos.ORG1_MSP);
			org1Admin.setName(ConfigPorthos.ADMIN);

			UserContext org2Admin = new UserContext();
			File pkFolder2 = new File(ConfigPorthos.ORG2_USR_ADMIN_PK);
			File[] pkFiles2 = pkFolder2.listFiles();
			File certFolder2 = new File(ConfigPorthos.ORG2_USR_ADMIN_CERT);
			File[] certFiles2 = certFolder2.listFiles();
			Enrollment enrollOrg2Admin = Util.getEnrollment(ConfigPorthos.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
					ConfigPorthos.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
			org2Admin.setEnrollment(enrollOrg2Admin);
			org2Admin.setMspId(ConfigPorthos.ORG2_MSP);
			org2Admin.setName(ConfigPorthos.ADMIN);

			FabricClient fabClient = new FabricClient(org1Admin);

			// Create a new channel
			Orderer orderer = fabClient.getInstance().newOrderer(ConfigPorthos.ORDERER_NAME, ConfigPorthos.ORDERER_URL);
			ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(ConfigPorthos.CHANNEL_CONFIG_PATH));

			byte[] channelConfigurationSignatures = fabClient.getInstance()
					.getChannelConfigurationSignature(channelConfiguration, org1Admin);

			Channel mychannel = fabClient.getInstance().newChannel(ConfigPorthos.CHANNEL_NAME, orderer, channelConfiguration,
					channelConfigurationSignatures);

			Peer peer0_org1 = fabClient.getInstance().newPeer(ConfigPorthos.ORG1_PEER_0, ConfigPorthos.ORG1_PEER_0_URL);
			Peer peer1_org1 = fabClient.getInstance().newPeer(ConfigPorthos.ORG1_PEER_1, ConfigPorthos.ORG1_PEER_1_URL);
			Peer peer0_org2 = fabClient.getInstance().newPeer(ConfigPorthos.ORG2_PEER_0, ConfigPorthos.ORG2_PEER_0_URL);
			Peer peer1_org2 = fabClient.getInstance().newPeer(ConfigPorthos.ORG2_PEER_1, ConfigPorthos.ORG2_PEER_1_URL);

			mychannel.joinPeer(peer0_org1);
			mychannel.joinPeer(peer1_org1);
			
			mychannel.addOrderer(orderer);

			mychannel.initialize();
			
			fabClient.getInstance().setUserContext(org2Admin);
			mychannel = fabClient.getInstance().getChannel("mychannel");
			mychannel.joinPeer(peer0_org2);
			mychannel.joinPeer(peer1_org2);
			
			log.info("Channel created "+mychannel.getName());
            Collection peers = mychannel.getPeers();
            Iterator peerIter = peers.iterator();
            while (peerIter.hasNext())
            {
            	  Peer pr = (Peer) peerIter.next();
            	  log.info(pr.getName()+ " at " + pr.getUrl());
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
