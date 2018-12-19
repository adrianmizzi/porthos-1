package porthos.ethereum;

import java.util.HashMap;
import java.util.Map;

import org.web3j.crypto.Credentials;

import porthos.Constants.Blockchain;


public class Web3jManager {

	private static Map<Blockchain, Web3jInstance> systems = new HashMap<Blockchain, Web3jInstance>();


	public static Web3jInstance getWeb3jInstance(Blockchain bcSystem) throws Exception {
		if (systems.get(bcSystem) == null) {
			Web3jInstance i;
			switch (bcSystem) {
			case ETHEREUM_1 : 
				i = new Web3jInstance("http://localhost:7545/","d474253311b4bb8b6b67375a4cd5539063f6346f63d900eb97b8f57e139d2dd8");
				break;
			case ETHEREUM_2 : 
				i = new Web3jInstance("http://localhost:6545/","dd4802ccb9ba68077d5c59abfa7530f5e8d842bcf02fe939335ad1d81827c76d");
				break;
			default :
				throw new Exception("Blockchain system not supported: " + bcSystem);

			}

			// add the blockchain system to the Map
			systems.put(bcSystem, i);

			return i;
		} else {
			return systems.get(bcSystem);
		}
	}

	public static Credentials[] getCredentials(Blockchain bcSystem) {
		switch (bcSystem) {
		case ETHEREUM_1 :
			return new Credentials[] {Credentials.create("b8d4de201dff8afb44f66f8d8f55b1dd48c2b26254b120fa0571ee2c6c0d1c00"), 
					Credentials.create("349977c16f81fee4638ff2aa7ccb8fbe7b1dea829fb8ee180678f7dd128111cd"),
					Credentials.create("c78dacf38197b46dc2947e60d4120c287bbc687ad386f08c8312b1fd10d6adca")};
		case ETHEREUM_2 :
			return new Credentials[] {Credentials.create("9f0aa4cb3600b24cac7bf81c29d6cc5302a1aee260ffcbc4352d113c19276efe"), 
					Credentials.create("b9b559a6a7c8db714df9df78741b8b8a2fee4b31e7cf31d0c7818b41f66aebc8"),
					Credentials.create("6948c480cd699326d34fac44ead67fe7d2c6abc42c2f942f2820d32b7fbef106")};
		}

		return null;
	}

}
