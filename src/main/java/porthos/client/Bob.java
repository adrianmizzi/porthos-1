package porthos.client;

import java.math.BigInteger;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.ethereum.Web3jManager;
import porthos.ethereum.Web3jManager.Blockchain;
import porthos.ethereum.contracts.generated.Ethereum_1;

public class Bob {
	private static final Logger log = LoggerFactory.getLogger(Bob.class);

	
	public static void main(String[] args) throws Exception {
		System.out.print("Please enter the contract address for Bob on Chain 1: ");
		Scanner in = new Scanner(System.in);
		String t2Address = in.nextLine();
		in.close();
		
		Credentials bobCredentials = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[1];
		String aliceAddress = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[0].getAddress();
		
		Web3j web3j = Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_1).getWeb3j();
		
		Ethereum_1 t2 = Ethereum_1.load(t2Address, web3j, bobCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
		
		t2.c1_commit("EUR", new BigInteger("100"), aliceAddress).send();
		
		log.info("Bob's commit is complete");
	}
}
