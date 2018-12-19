package porthos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porthos.Constants.Blockchain;
import porthos.ethereum.comms.EthCommsChannel;
import porthos.hyperledger.comms.HLCommsChannel;

public class GateManager {

	private static final Logger log = LoggerFactory.getLogger(GateManager.class);

	private static GateManager instance = new GateManager();
	
	private GateManager() {}
	
	public static GateManager getInstance() {
		return instance;
	}
	
	public void openGate(Blockchain _blockchain, String _contractAddress, String _assetType, String _gateName) {
		try {
			log.info("GateManager: Open Gate Request");
			
			switch (_blockchain) {
				case ETHEREUM_1 :
				case ETHEREUM_2 :
					EthCommsChannel.getInstance().openGateCall(_blockchain, _contractAddress, _assetType, _gateName);
					break;
				case HYPERLEDGER :
					HLCommsChannel.getInstance().openGate(_gateName);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Unable to open gate", e);
		}
	}

	public void crosschainCall(Blockchain _blockchain, String _contractAddress, String _methodName) {
		try {
			log.info("GateManager: Crosschain Call Request");

			switch (_blockchain) {
				case ETHEREUM_1 :
				case ETHEREUM_2 :
					EthCommsChannel.getInstance().crosschainCall(_blockchain, _contractAddress, _methodName);
					break;
//				case HYPERLEDGER :
//					break;
				default:	
					throw new Error("Crosschain not implemented");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Unable to do cross-chain call", e);
		}
	}
	
	public void releaseAllCommitment(Blockchain _blockchain, String _contractAddress, String _methodName) {
		try {
			log.info("GateManager: Release All Commitments Request");

			switch (_blockchain) {
				case ETHEREUM_1 :
				case ETHEREUM_2 :
					EthCommsChannel.getInstance().releaseAllCommitment(_blockchain, _contractAddress, _methodName);
					break;
//				case HYPERLEDGER :
//					break;
				default:
					throw new Error("Release All Commitments not implemented");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Unable to do cross-chain call", e);
		}
	}
}
