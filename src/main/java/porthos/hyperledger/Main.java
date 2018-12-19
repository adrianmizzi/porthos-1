package porthos.hyperledger;

public class Main {

	public static void main(String[] args) {
		try {
			CreateChannel.main(args);
			DeployInstantiateChaincode.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
