package porthos.ethereum.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.Constants.Blockchain;
import porthos.ethereum.Web3jInstance;
import porthos.ethereum.Web3jManager;
import porthos.ethereum.contracts.generated.Gateway;

public class LogEventListener {
	private static final Logger log = LoggerFactory.getLogger(LogEventListener.class);

	private static String gatewayAddress;
	private Gateway gateway;

	public LogEventListener(Blockchain bcSystem, String _gatewayAddress) throws Exception  {
		Web3jInstance web3jI = Web3jManager.getWeb3jInstance(bcSystem);
		gatewayAddress = _gatewayAddress;

		Web3j web3j = web3jI.getWeb3j(); 
		Credentials credentials = web3jI.getCredentials(); 

		// create an instance of the gateway
		gateway = Gateway.load(gatewayAddress,
				web3j, credentials,
				ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

		log.info("Filtering to receive latest events");
		EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gatewayAddress.substring(2));
		filter.addSingleTopic(EventEncoder.encode(Gateway.LOG_EVENT));

		// listen on log events
		gateway.logEventObservable(filter)  
		.subscribe(event -> {
			log.info("Received Log Event [{}]: {}", event.blockNumber, event.message);
			
		}, err  -> {
			log.error("Error on log event", err);
		});


		filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gatewayAddress.substring(2));
		filter.addSingleTopic(EventEncoder.encode(Gateway.GATEOPENED_EVENT));

		// listen on log events
		gateway.gateOpenedEventObservable(filter)  
		.subscribe(event -> {
			log.info("Gate {} is open on {}", event.gateName, bcSystem);
		}, err  -> {
			log.error("Error on log event", err);
		});

		filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gatewayAddress.substring(2));
		filter.addSingleTopic(EventEncoder.encode(Gateway.GATECLOSED_EVENT));

		// listen on log events
		gateway.gateClosedEventObservable(filter)  
		.subscribe(event -> {
			log.info("Gate {} is closed on {}", event.gateName, bcSystem);
		}, err  -> {
			log.error("Error on log event", err);
		});
	}
}


