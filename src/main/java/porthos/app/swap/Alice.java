package porthos.app.swap;

import java.math.BigInteger;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.Constants.Blockchain;
import porthos.ethereum.Web3jManager;
import porthos.ethereum.contracts.generated.Ethereum_1;

public class Alice {
	private static final Logger log = LoggerFactory.getLogger(Alice.class);

	
	public static void main(String[] args) throws Exception {
		System.out.print("Please enter the contract address for Alice on Chain 1: ");
		Scanner in = new Scanner(System.in);
		String contractAddress = in.nextLine();
		in.close();
		
		Credentials aliceCredentials = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[0];
		String bobAddress = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[1].getAddress();
		
		Web3j web3j = Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_1).getWeb3j();
		
		Ethereum_1 contract = Ethereum_1.load(contractAddress, web3j, aliceCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
		
		contract.p1Commit_commit("Apple", new BigInteger("1"), bobAddress).send();
		log.info("Alice's commit is complete");
	}
}
