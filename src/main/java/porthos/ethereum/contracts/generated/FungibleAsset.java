package porthos.ethereum.contracts.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class FungibleAsset extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50604051602080610391833981016040525160008054600160a060020a03909216600160a060020a031990921691909117905561033f806100526000396000f3006080604052600436106100615763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166331e0e74681146100665780634d667cf414610089578063beabacc8146100ad578063f8b2cb4f146100d7575b600080fd5b34801561007257600080fd5b50610087600160a060020a036004351661010a565b005b34801561009557600080fd5b50610087600160a060020a0360043516602435610176565b3480156100b957600080fd5b50610087600160a060020a03600435811690602435166044356101ea565b3480156100e357600080fd5b506100f8600160a060020a03600435166102f8565b60408051918252519081900360200190f35b600160a060020a03811660008181526001602090815260408083208390558051938452908301829052828101829052606083018290526080830191909152517f34ae7779d4c90619282b3368b4dd0b74822ca4317934b4acd6e520a3835c15f99181900360a00190a150565b600160a060020a0382166000818152600160209081526040808320805486019081905581518481529283019490945281810185905260608201929092526080810192909252517f34ae7779d4c90619282b3368b4dd0b74822ca4317934b4acd6e520a3835c15f99181900360a00190a15050565b600160a060020a03831660009081526001602052604090205481111561027157604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152601260248201527f4e6f7420656e6f7567682062616c616e63650000000000000000000000000000604482015290519081900360640190fd5b600160a060020a0383811660008181526001602090815260408083208054879003815594871680845292819020805487019081905594548151948552918401929092528282018590526060830152608082019290925290517f34ae7779d4c90619282b3368b4dd0b74822ca4317934b4acd6e520a3835c15f99181900360a00190a1505050565b600160a060020a0316600090815260016020526040902054905600a165627a7a723058200e252da4aeb6581b41a550f95fd07748bdbc5641db542b74d7077e9495dfbe4e0029";

    public static final String FUNC_REVOKEALLASSETS = "revokeAllAssets";

    public static final String FUNC_ISSUEASSETS = "issueAssets";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final Event ASSETREGISTEREVENT_EVENT = new Event("AssetRegisterEvent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected FungibleAsset(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FungibleAsset(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FungibleAsset(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FungibleAsset(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> revokeAllAssets(String _recipient) {
        final Function function = new Function(
                FUNC_REVOKEALLASSETS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> issueAssets(String _recipient, BigInteger _amount) {
        final Function function = new Function(
                FUNC_ISSUEASSETS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _sender, String _recipient, BigInteger _amount) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_sender), 
                new org.web3j.abi.datatypes.Address(_recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getBalance(String _user) {
        final Function function = new Function(FUNC_GETBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<FungibleAsset> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)));
        return deployRemoteCall(FungibleAsset.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<FungibleAsset> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)));
        return deployRemoteCall(FungibleAsset.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FungibleAsset> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)));
        return deployRemoteCall(FungibleAsset.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FungibleAsset> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)));
        return deployRemoteCall(FungibleAsset.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<AssetRegisterEventEventResponse> getAssetRegisterEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ASSETREGISTEREVENT_EVENT, transactionReceipt);
        ArrayList<AssetRegisterEventEventResponse> responses = new ArrayList<AssetRegisterEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AssetRegisterEventEventResponse typedResponse = new AssetRegisterEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.balance_sender = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.balance_recipient = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AssetRegisterEventEventResponse> assetRegisterEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, AssetRegisterEventEventResponse>() {
            @Override
            public AssetRegisterEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ASSETREGISTEREVENT_EVENT, log);
                AssetRegisterEventEventResponse typedResponse = new AssetRegisterEventEventResponse();
                typedResponse.log = log;
                typedResponse.sender = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.balance_sender = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.balance_recipient = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<AssetRegisterEventEventResponse> assetRegisterEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ASSETREGISTEREVENT_EVENT));
        return assetRegisterEventEventObservable(filter);
    }

    @Deprecated
    public static FungibleAsset load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FungibleAsset(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FungibleAsset load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FungibleAsset(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FungibleAsset load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FungibleAsset(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FungibleAsset load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FungibleAsset(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class AssetRegisterEventEventResponse {
        public Log log;

        public String sender;

        public String recipient;

        public BigInteger amount;

        public BigInteger balance_sender;

        public BigInteger balance_recipient;
    }
}
