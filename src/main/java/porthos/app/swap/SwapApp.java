package porthos.app.swap;

import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.Constants.Blockchain;
import porthos.ethereum.Web3jManager;
import porthos.ethereum.callback.CallbackHandler;
import porthos.ethereum.comms.EthCommsChannel;
import porthos.ethereum.comms.LogEventListener;
import porthos.ethereum.contracts.generated.Ethereum_1;
import porthos.ethereum.contracts.generated.Gateway;
import porthos.hyperledger.CreateChannel;
import porthos.hyperledger.DeployInstantiateChaincode;
import porthos.hyperledger.HyperledgerEventListener;
import porthos.hyperledger.InvokeQuery;

public class SwapApp {

	private static final Logger log = LoggerFactory.getLogger(SwapApp.class);

	
	public static void main(String[] args) {
		try {
			// Setup and deploy on the Hyperledger Chain
			setupHyperledgerChain();

			// Setup and deploy on the Ethereum Chain
			String gateway = setupEthereumChain();
			
			// Deploy the contract on the Ethereum Chain
			String contractAddress = runEthereumClient(gateway);
			
			// Set the address of the Ethereum contract in the Hyperledger contract
			InvokeQuery iq = new InvokeQuery();
			iq.setCounterparty(Blockchain.ETHEREUM_1.toString(), contractAddress);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void setupHyperledgerChain() throws Exception {
		// Create a channel
		CreateChannel c = new CreateChannel();
		c.createChannel();
		

		// Deploy and Instantiate Chaincode
		DeployInstantiateChaincode dic = new DeployInstantiateChaincode();
		dic.deployAndInstantiate();
		
		// Initiate Event Listener
		new HyperledgerEventListener().start();
		
		// Invoke the methods on the contract
//		InvokeQuery iq = new InvokeQuery();
//		iq.invokeContract();
	}
	
	private static String setupEthereumChain() throws Exception {
		// Start by deploying a new instance of the default contracts on Ethereum 1: EventGenerator and CallbackManager
		String gateway1 = deployGatewayContract(Blockchain.ETHEREUM_1);
		log.info("Gateway framework contract deployed on Ethereum 1");

		// initialise the callback handler on Ethereum 1
		log.info("Initialising Callback Handler on Chain 1");
		new CallbackHandler(Blockchain.ETHEREUM_1, gateway1);

		// initialise the log event listenres on Ethereum 1
		log.info("Initialising Log Event Listener on Chain 1");
		new LogEventListener(Blockchain.ETHEREUM_1, gateway1);

		// initialise the comms channel
		EthCommsChannel comms = EthCommsChannel.getInstance();
		comms.registerGateway(Blockchain.ETHEREUM_1, gateway1);

		return gateway1;
	}
	
	private static String runEthereumClient(String gateway1) throws Exception {

		Credentials[] credChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1);

		// deploy contract on Ethereum 1
		log.info("Deploying contract on Ethereum 1");
		@SuppressWarnings("deprecation")
		Ethereum_1 t2Contract1 = Ethereum_1.deploy(
				Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_1).getWeb3j(), 
				Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_1).getCredentials(),
				BigInteger.valueOf(20), BigInteger.valueOf(5_000_000),
				credChain1[0].getAddress(), 
				credChain1[1].getAddress(), 
				credChain1[2].getAddress(), 
				gateway1,
				Blockchain.ETHEREUM_1.toString()).send();
		log.info("Contract Ethereum_1 deployed at {}", t2Contract1.getContractAddress());

		// create asset register on Chain 1
		t2Contract1.addAssetRegister("Apple").send();	    

		// register asset managers on Chain 1
		log.info("Registering asset managers on Chain 1");
		t2Contract1.addAssetManager("Apple", Blockchain.ETHEREUM_1.toString(), t2Contract1.getContractAddress()).send();
		t2Contract1.addAssetManager("Orange", Blockchain.HYPERLEDGER.toString(), t2Contract1.getContractAddress()).send();

		// give assets to alice, bob and charlie
		String aliceChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[0].getAddress();
		String bobChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[1].getAddress();
		String charlieChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[2].getAddress();

		t2Contract1.issueAsset("Apple", new BigInteger("100"), aliceChain1).send(); 
		t2Contract1.issueAsset("Apple", new BigInteger("100"), bobChain1).send(); 
		t2Contract1.issueAsset("Apple", new BigInteger("100"), charlieChain1).send(); 

		// let contracts know who is the master contract
		t2Contract1.setMaster(Blockchain.ETHEREUM_1.toString(), t2Contract1.getContractAddress()).send();

		// start a timer to print balances every 30 seconds
		String[] accountNames = {"alice", "bob", "charlie"};
		String[] chain1Accounts = {aliceChain1, bobChain1, charlieChain1};
		SwapApp app = new SwapApp();
		
		// Timer for Apple balances
		TimerTask timerTask = app.new BalanceTask(accountNames,	chain1Accounts, t2Contract1, "Apple");
		//running timer task as daemon thread
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, 60*1000);

		// start the execution
		log.info("Starting execution on Main Contract");
		t2Contract1.start().send();	    
		
		return t2Contract1.getContractAddress();
	}
	
	private static String deployGatewayContract(Blockchain bcSystem) throws Exception {
		Web3j web3j = Web3jManager.getWeb3jInstance(bcSystem).getWeb3j(); 

		// We provide a private key to create credentials
		Credentials credentials = Web3jManager.getWeb3jInstance(bcSystem).getCredentials();

		// Now lets deploy the EventGenerator smart contract
		log.info("Deploying smart contract: Gateway");
		@SuppressWarnings("deprecation")
		Gateway contract = Gateway.deploy(
				web3j, credentials,
				ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();

		log.info("Smart contract (Gateway) deployed at address " + contract.getContractAddress());

		return contract.getContractAddress();
	}


	private class BalanceTask extends TimerTask {

		private String[] names;
		private String[] accounts;
		private Ethereum_1 chain;
		private String assetType;

		private BalanceTask(String[] _names, String[] _accounts, Ethereum_1 _chain, String _assetType) {
			names = _names;
			accounts = _accounts;
			chain = _chain;
			assetType = _assetType;
		}

		@Override
		public void run() {
			int arrayLength = accounts.length;
			
			for(int i = 0; i < arrayLength; i++) {
				String a = accounts[i];
				try {
					BigInteger balance = chain.fetchBalance(assetType, a).send();
					log.info("{} Balance of {} is {}{} ({})", assetType, names[i], assetType, balance.toString(), a);
				} catch (Exception e) {
					log.error("Unable to fetch balance for account {}", names[i], e);
				}
			}
		}
	}
	
}
