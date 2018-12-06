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
import porthos.ethereum.contracts.generated.Ethereum_2;

public class Alice {
	private static final Logger log = LoggerFactory.getLogger(Alice.class);

	
	public static void main(String[] args) throws Exception {
		System.out.print("Please enter the contract address for Alice on Chain 2: ");
		Scanner in = new Scanner(System.in);
		String t2Address = in.nextLine();
		in.close();
		
		Credentials aliceCredentials = Web3jManager.getCredentials(Blockchain.ETHEREUM_2)[0];
		String bobAddress = Web3jManager.getCredentials(Blockchain.ETHEREUM_2)[1].getAddress();
		
		Web3j web3j = Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_2).getWeb3j();
		
		Ethereum_2 t2 = Ethereum_2.load(t2Address, web3j, aliceCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
		
		t2.c2_commit("GBP", new BigInteger("100"), bobAddress).send();
		log.info("Alice's commit is complete");

	}

}
