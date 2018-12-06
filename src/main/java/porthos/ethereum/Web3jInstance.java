package porthos.ethereum;

import java.io.IOException;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class Web3jInstance {

    private static final Logger log = LoggerFactory.getLogger(Web3jInstance.class);
	private Web3j web3j;
	private Credentials credentials;
	
	public Web3jInstance (String _url, String _credentials) throws Exception {
		web3j = Web3j.build(new HttpService(_url));
		log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
		credentials = Credentials.create(_credentials);
	}

	public Web3j getWeb3j() {
		return web3j;
	}

	public Credentials getCredentials() {
		return credentials; 
	}
	
    public BigInteger getCurrentBlockNumber() throws IOException {
    	return web3j.ethBlockNumber().send().getBlockNumber();
    }

	
}
