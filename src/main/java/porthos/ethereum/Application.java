package porthos.ethereum;

import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.ethereum.Web3jManager.Blockchain;
import porthos.ethereum.callback.CallbackHandler;
import porthos.ethereum.comms.CommsChannel;
import porthos.ethereum.comms.LogEventListener;
import porthos.ethereum.contracts.generated.Ethereum_1;
import porthos.ethereum.contracts.generated.Ethereum_2;
import porthos.ethereum.contracts.generated.Gateway;

public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		try {
			// Start by deploying a new instance of the default contracts on Ethereum 1: EventGenerator and CallbackManager
			String gateway1 = deployGatewayContract(Blockchain.ETHEREUM_1);
			log.info("Gateway framework contract deployed on Ethereum 1");

			// Start by deploying a new instance of the default contracts on Etheruem 2: EventGenerator and CallbackManager
			String gateway2 = deployGatewayContract(Blockchain.ETHEREUM_2);
			log.info("Gateway framework contract deployed on Ethereum 2");

			// initialise the callback handler on Ethereum 1
			log.info("Initialising Callback Handler on Chain 1");
			new CallbackHandler(Blockchain.ETHEREUM_1, gateway1);

			// initialise the callback handler on Ethereum 2
			log.info("Initialising Callback Handler on Chain 2");
			new CallbackHandler(Blockchain.ETHEREUM_2, gateway2);

			// initialise the log event listenres on Ethereum 1
			log.info("Initialising Log Event Listener on Chain 1");
			new LogEventListener(Blockchain.ETHEREUM_1, gateway1);

			// initialise the log event listenres on Ethereum 1
			log.info("Initialising Log Event Listener on Chain 2");
			new LogEventListener(Blockchain.ETHEREUM_2, gateway2);

			// initialise the comms channel
			CommsChannel comms = new CommsChannel();
			comms.registerGateway(Blockchain.ETHEREUM_1, gateway1);
			comms.registerGateway(Blockchain.ETHEREUM_2, gateway2);

			runClient(gateway1, gateway2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void runClient(String gateway1, String gateway2) throws Exception {

		Credentials[] credChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1);
		Credentials[] credChain2 = Web3jManager.getCredentials(Blockchain.ETHEREUM_2);

		// deploy contract on Ethereum 1
		log.info("Deploying T2Eur contract on Ethereum 1");
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

		// deploy contract on Ethereum 2
		log.info("Deploying T2Gbp contract on Ethereum 2");
		@SuppressWarnings("deprecation")
		Ethereum_2 t2Contract2 = Ethereum_2.deploy(
				Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_2).getWeb3j(), 
				Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_2).getCredentials(),
				BigInteger.valueOf(20), BigInteger.valueOf(5_300_000),
				credChain2[0].getAddress(), 
				credChain2[1].getAddress(), 
				credChain2[2].getAddress(), 
				gateway2,
				Blockchain.ETHEREUM_2.toString()).send();		
		log.info("Contract Ethereum_2 deployed at {}", t2Contract2.getContractAddress());

		// create EUR asset register on Chain 1
		t2Contract1.addAssetRegister("EUR").send();	    

		// create GBP asset register on Chain 2
		t2Contract2.addAssetRegister("GBP").send();	    

		// register asset managers on Chain 1
		log.info("Registering asset managers on Chain 1");
		t2Contract1.addAssetManager("EUR", Blockchain.ETHEREUM_1.toString(), t2Contract1.getContractAddress()).send();
		t2Contract1.addAssetManager("GBP", Blockchain.ETHEREUM_2.toString(), t2Contract2.getContractAddress()).send();

		// register GBP asset manager on Chain 2
		log.info("Registering asset managers on Chain 2");
		t2Contract2.addAssetManager("EUR", Blockchain.ETHEREUM_1.toString(), t2Contract1.getContractAddress()).send();
		t2Contract2.addAssetManager("GBP", Blockchain.ETHEREUM_2.toString(), t2Contract2.getContractAddress()).send();

		// give assets to alice, bob and charlie
		String aliceChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[0].getAddress();
		String bobChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[1].getAddress();
		String charlieChain1 = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[2].getAddress();
		String aliceChain2 = Web3jManager.getCredentials(Blockchain.ETHEREUM_2)[0].getAddress();
		String bobChain2 = Web3jManager.getCredentials(Blockchain.ETHEREUM_2)[1].getAddress();
		String charlieChain2 = Web3jManager.getCredentials(Blockchain.ETHEREUM_2)[2].getAddress();

		t2Contract1.issueAsset("EUR", new BigInteger("100"), aliceChain1).send(); 
		t2Contract1.issueAsset("EUR", new BigInteger("100"), bobChain1).send(); 
		t2Contract1.issueAsset("EUR", new BigInteger("100"), charlieChain1).send(); 
		t2Contract2.issueAsset("GBP", new BigInteger("100"), aliceChain2).send(); 
		t2Contract2.issueAsset("GBP", new BigInteger("100"), bobChain2).send(); 
		t2Contract2.issueAsset("GBP", new BigInteger("100"), charlieChain2).send(); 

		// let contracts know who is the master contract
		t2Contract1.setMaster(Blockchain.ETHEREUM_1.toString(), t2Contract1.getContractAddress()).send();
		t2Contract2.setMaster(Blockchain.ETHEREUM_1.toString(), t2Contract1.getContractAddress()).send();

		// start a timer to print balances every 30 seconds
		String[] accountNames = {"alice", "bob", "charlie"};
		String[] eurAccounts = {aliceChain1, bobChain1, charlieChain1};
		String[] gbpAccounts = {aliceChain2, bobChain2, charlieChain2};
		Application app = new Application();
		
		// Timer for EUR balances
		TimerTask timerTask = app.new EurBalanceTask(accountNames,	eurAccounts, t2Contract1, "EUR");
		//running timer task as daemon thread
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, 15*1000);

		// Timer for GBP balances
		timerTask = app.new GbpBalanceTask(accountNames, gbpAccounts, t2Contract2, "GBP");
		//running timer task as daemon thread
		timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 1000, 15*1000);

		
		// start the execution
		log.info("Starting execution on Main Contract");
		t2Contract1.start().send();	    

		//	    log.info("Sending a cross chain call from Ethereum_1 to Ethereum_2");
		//	    t2Contract1.initiateCrossChainCall(Blockchain.ETHEREUM_2.toString(), t2Contract2.getContractAddress()).send();
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

	private class EurBalanceTask extends TimerTask {

		private String[] names;
		private String[] accounts;
		private Ethereum_1 chain;
		private String assetType;

		private EurBalanceTask(String[] _names, String[] _accounts, Ethereum_1 _chain, String _assetType) {
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
	
	private class GbpBalanceTask extends TimerTask {

		private String[] names;
		private String[] accounts;
		private Ethereum_2 chain;
		private String assetType;

		private GbpBalanceTask(String[] _names, String[] _accounts, Ethereum_2 _chain, String _assetType) {
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
					log.info("{} Balance of {} is {}{}", assetType, names[i], assetType, balance.toString());
				} catch (Exception e) {
					log.error("Unable to fetch balance for account {}", names[i], e);
				}
			}
		}
	}
}

