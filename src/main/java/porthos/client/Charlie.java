package porthos.client;

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

public class Charlie {
	private static final Logger log = LoggerFactory.getLogger(Charlie.class);

	
	public static void main(String[] args) throws Exception {
		System.out.print("Please enter the contract address for Charlie on Chain 1: ");
		Scanner in = new Scanner(System.in);
		String t2Address = in.nextLine();
		in.close();
		
		Credentials charlieCredentials = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[2];
		String bobAddress = Web3jManager.getCredentials(Blockchain.ETHEREUM_1)[1].getAddress();
		
		Web3j web3j = Web3jManager.getWeb3jInstance(Blockchain.ETHEREUM_1).getWeb3j();
		
		Ethereum_1 t2 = Ethereum_1.load(t2Address, web3j, charlieCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
		
//		t2.c3_commit("EUR", new BigInteger("100"), bobAddress).send();
		
		log.info("Charlie's commit is complete");
	}
}
