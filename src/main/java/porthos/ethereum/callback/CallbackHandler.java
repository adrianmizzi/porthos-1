package porthos.ethereum.callback;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import porthos.ethereum.Web3jInstance;
import porthos.ethereum.Web3jManager;
import porthos.ethereum.Web3jManager.Blockchain;
import porthos.ethereum.contracts.generated.Gateway;


public class CallbackHandler {
    private static final Logger log = LoggerFactory.getLogger(CallbackHandler.class);

    private Vector<CallbackInfo> callbacks;
    private static String gatewayAddress;
    
    private Gateway gateway;

    @SuppressWarnings("deprecation")
	public CallbackHandler(Blockchain bcSystem, String _gatewayAddress) throws Exception  {
    	Web3jInstance web3jI = Web3jManager.getWeb3jInstance(bcSystem);
    	gatewayAddress = _gatewayAddress;
    	
    	callbacks = new Vector<CallbackInfo>();
    	
    	Web3j web3j = web3jI.getWeb3j(); 
    	Credentials credentials = web3jI.getCredentials(); 
    	
        // start listening for block numbers
        web3j.blockObservable(false).subscribe(event -> {
        	log.info("{} Block Number {}", bcSystem, event.getBlock().getNumber().toString());
        	try {
        		blockClock(event.getBlock().getNumber());
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        });
        
    	// create an instance of the callback manager
        gateway = Gateway.load(gatewayAddress,
        		web3j, credentials,
        		ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        
//        log.info("Filtering to receive latest events");
//        EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gatewayAddress.substring(2));
//        filter.addSingleTopic(EventEncoder.encode(Gateway.LOG_EVENT));
//        
//        // listen on log events
//        gateway.logEventObservable(filter)  
//        		.subscribe(event -> {
//        			log.info("Received Log Event [{}]: {}", event.blockNumber, event.message);
//        		}, err  -> {
//        			log.error("Error on log event", err);
//        		});
        
        EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gatewayAddress.substring(2));
        filter.addSingleTopic(EventEncoder.encode(Gateway.CALLBACKREQUEST_EVENT));

        // start listening for callback requests
    	// create an instance of the callback manager
        gateway.callbackRequestEventObservable(filter)
        		.subscribe(event -> {
        			log.info("Received callback request {}, {} at time {}", event.contractAddress, event.methodName, event.timeRequested);
        			this.addCallback(new CallbackInfo(event.contractAddress, event.methodName, event.timeRequested));
        		}, err  -> {
        			log.error("Error on callback request event", err);
        		});
        
        filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, gatewayAddress.substring(2));
        filter.addSingleTopic(EventEncoder.encode(Gateway.CALLBACKCANCEL_EVENT));

        // start listening for callback cancellations
        gateway.callbackCancelEventObservable(filter)
			.subscribe(event -> {
				log.info("Received callback cancellation {}, {} at time {}", event.contractAddress, event.methodName, event.timeRequested);
				this.cancelCallback(new CallbackInfo(event.contractAddress, event.methodName, event.timeRequested));
			}, err  -> {
    			log.error("Error on callback cancellation event", err);
			});

    }
    
    /**
     * Adds a callback 
     * @param cot
     */
    public void addCallback(CallbackInfo cot) {
    	synchronized(callbacks) {
	    	callbacks.add(cot);
			log.info("Number of callbacks: {}", callbacks.size());
    	}
    }
    
    /**
     * Cancels a callback
     * @param cot
     */
    public void cancelCallback(CallbackInfo cot) {
    	synchronized(callbacks) {
	    	callbacks.remove(cot);
			log.info("Number of callbacks: {}", callbacks.size());
    	}
    }

    /**
     * When the block time changes, this method goes through all the callbacks
     * to check if any of them is due for activation
     * @param blockTime
     */
    private synchronized void blockClock(BigInteger blockTime) {
    	synchronized (callbacks) { 
	    	Iterator<CallbackInfo> it = callbacks.iterator();
	    	while(it.hasNext()) {
	    		CallbackInfo cot = (CallbackInfo) it.next();
	
	    		// check if it is time for this callback to trigger
	    		if (cot.getTimeout().compareTo(blockTime) <= 0) {
	    			// trigger the callback
	    			try {
						triggerCallback(cot);
	
						// remove from the vector of callbacks
		    			it.remove();
					} catch (Exception e) {
						log.error("Unable to trigger callback", e);
					}	
	    		}
	    	}
    	}
    }
    
    /**
     * Triggers the callback on the smart contract
     * @param cot
     * @throws Exception
     */
    private void triggerCallback(CallbackInfo cot) throws Exception {
    	String contractAddress = cot.getContractAddress();
    	String methodName      = cot.getMethodName();
    	
    	log.info("Invoking {} on contract {}", methodName, contractAddress);
    	gateway.call(contractAddress, methodName).send();
    	log.info("Callback invocation complete");
    }
    
    
}
