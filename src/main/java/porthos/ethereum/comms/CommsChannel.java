package porthos.ethereum.comms;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.ethereum.Web3jManager;
import porthos.ethereum.Web3jManager.Blockchain;
import porthos.ethereum.contracts.generated.Gateway;

public class CommsChannel {
	private static final Logger log = LoggerFactory.getLogger(CommsChannel.class);

	private Map<Blockchain, Gateway> gateways;

	public CommsChannel() {
		gateways = new HashMap<Blockchain, Gateway>();
	}

	public void registerGateway(Blockchain system, String gatewayAddress) throws Exception {
		log.info("Registering a new gateway on system {}", system);
		Web3j web3j = Web3jManager.getWeb3jInstance(system).getWeb3j();
		Credentials credentials = Web3jManager.getWeb3jInstance(system).getCredentials();

		// Connect to a previously deployed contract - CallbackManager
		log.info("Connecting to previously deployed smart contract");
		@SuppressWarnings("deprecation")
		Gateway gateway = Gateway.load(gatewayAddress,
				web3j, credentials,
				ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

		// add to our map
		gateways.put(system, gateway);
		
		EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gateway.getContractAddress().substring(2));
        filter.addSingleTopic(EventEncoder.encode(Gateway.CROSSCHAIN_EVENT));

		gateway.crossChainEventObservable(filter)  
		.subscribe(event -> {
			log.info("Received Cross Chain Call {} / {}", event.blockchainSystem, event.methodName);
			try {
				crosschainCall(Blockchain.valueOf(event.blockchainSystem), event.contractAddress, event.methodName);
			} catch (Exception e) {
				log.error("Unable to execute cross chain call", e);
			}
		});
		
		filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gateway.getContractAddress().substring(2));
        filter.addSingleTopic(EventEncoder.encode(Gateway.CCOPENGATE_EVENT));

		gateway.cCOpenGateEventObservable(filter)  
		.subscribe(event -> {
			log.info("{} Received Cross Chain: Open Gate Call {}: {}-{}", system, event.blockchainSystem, event.assetType, event.gateName);
			try {
				openGateCall(Blockchain.valueOf(event.blockchainSystem), event.contractAddress, event.assetType, event.gateName);
			} catch (Exception e) {
				log.error("Unable to execute cross chain call", e);
			}
		});
		
		filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gateway.getContractAddress().substring(2));
        filter.addSingleTopic(EventEncoder.encode(Gateway.CCRELEASECOMMITMENTS_EVENT));

		gateway.cCReleaseCommitmentsEventObservable(filter)  
		.subscribe(event -> {
			log.info("{} Received Cross Chain: Release All Commitments Call {}: {}", system, event.blockchainSystem, event.assetType);
			try {
				releaseAllCommitment(Blockchain.valueOf(event.blockchainSystem), event.contractAddress, event.assetType);
			} catch (Exception e) {
				log.error("Unable to execute cross chain call", e);
			}
		});

	}
	
	private void crosschainCall(Blockchain _system, String _contractAddress, String _methodName) throws Exception {
		Gateway gateway = gateways.get(_system);
		log.info("Initiating cross chain call {} {}", _contractAddress, _methodName);
		gateway.call(_contractAddress, _methodName).send();
	}
	
	private void openGateCall(Blockchain _system, String _contractAddress, String _assetType, String _gateName) throws Exception {
		Gateway gateway = gateways.get(_system);
		log.info("Initiating cross chain open gate call {} {} for gate {}", _system.toString(), _assetType, _gateName);
		gateway.openGateCall(_contractAddress, _assetType, _gateName).send();
		log.info("Open Gate Call complete");
	}
	
	private void releaseAllCommitment(Blockchain _system, String _contractAddress, String _assetType) throws Exception {
		Gateway gateway = gateways.get(_system);
		log.info("Initiating cross chain release all commitments call {} {}", _system.toString(), _assetType);
		log.info("Calling contract {}", _contractAddress);
		gateway.releaseAllCommitmentsCall(_contractAddress, _assetType).send();
		log.info("Release All Commitments Call complete");
	}
}
