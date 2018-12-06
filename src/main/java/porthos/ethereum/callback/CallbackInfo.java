package porthos.ethereum.callback;

import java.math.BigInteger;

public class CallbackInfo {
	private String contractAddress;
	private String methodName;
	private BigInteger timeout;
	
	public CallbackInfo(String _contractAddress, String _methodName, BigInteger _timeout) {
		contractAddress = _contractAddress;
		methodName = _methodName;
		timeout = _timeout;
	}

	public BigInteger getTimeout() {
		return timeout;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getContractAddress() {
		return contractAddress;
	}
}
