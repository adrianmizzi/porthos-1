package porthos.ethereum.contracts.generated;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class PorthosContract extends Contract {
    private static final String BINARY = "";

    public static final String FUNC_CANCELALLCOMMITMENTS = "cancelAllCommitments";

    public static final String FUNC_SETMASTER = "setMaster";

    public static final String FUNC_RELEASEALLCOMMITMENTS = "releaseAllCommitments";

    public static final String FUNC_OPENGATE = "openGate";

    public static final String FUNC_ADDASSETREGISTER = "addAssetRegister";

    public static final String FUNC_ADDASSETMANAGER = "addAssetManager";

    public static final String FUNC_FETCHBALANCE = "fetchBalance";

    public static final String FUNC_GETCONTRACTSTATUS = "getContractStatus";

    public static final String FUNC_REVOKEALLASSETS = "revokeAllAssets";

    public static final String FUNC_FETCHCOMMITMENT = "fetchCommitment";

    public static final String FUNC_ISSUEASSET = "issueAsset";

    public static final String FUNC_FIREEVENT = "fireEvent";

    @Deprecated
    protected PorthosContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PorthosContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PorthosContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PorthosContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> cancelAllCommitments(String _assetType) {
        final Function function = new Function(
                FUNC_CANCELALLCOMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> cancelAllCommitments() {
        final Function function = new Function(
                FUNC_CANCELALLCOMMITMENTS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setMaster(String _masterName, String _master) {
        final Function function = new Function(
                FUNC_SETMASTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_masterName), 
                new org.web3j.abi.datatypes.Address(_master)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> releaseAllCommitments() {
        final Function function = new Function(
                FUNC_RELEASEALLCOMMITMENTS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> openGate(String _assetType, String _gateName) {
        final Function function = new Function(
                FUNC_OPENGATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetType), 
                new org.web3j.abi.datatypes.Utf8String(_gateName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addAssetRegister(String assetType) {
        final Function function = new Function(
                FUNC_ADDASSETREGISTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addAssetManager(String _assetType, String _blockchainSystem, String _contractAddress) {
        final Function function = new Function(
                FUNC_ADDASSETMANAGER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetType), 
                new org.web3j.abi.datatypes.Utf8String(_blockchainSystem), 
                new org.web3j.abi.datatypes.Address(_contractAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> fetchBalance(String _assetType, String _address) {
        final Function function = new Function(FUNC_FETCHBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetType), 
                new org.web3j.abi.datatypes.Address(_address)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getContractStatus() {
        final Function function = new Function(FUNC_GETCONTRACTSTATUS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> revokeAllAssets(String assetType, String recipient) {
        final Function function = new Function(
                FUNC_REVOKEALLASSETS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(assetType), 
                new org.web3j.abi.datatypes.Address(recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> releaseAllCommitments(String _assetType) {
        final Function function = new Function(
                FUNC_RELEASEALLCOMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple5<String, BigInteger, String, String, BigInteger>> fetchCommitment(BigInteger _commitmentId) {
        final Function function = new Function(FUNC_FETCHCOMMITMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_commitmentId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple5<String, BigInteger, String, String, BigInteger>>(
                new Callable<Tuple5<String, BigInteger, String, String, BigInteger>>() {
                    @Override
                    public Tuple5<String, BigInteger, String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<String, BigInteger, String, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> issueAsset(String assetType, BigInteger quantity, String recipient) {
        final Function function = new Function(
                FUNC_ISSUEASSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(quantity), 
                new org.web3j.abi.datatypes.Address(recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> fireEvent(String _message) {
        final Function function = new Function(
                FUNC_FIREEVENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_message)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<PorthosContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _blockchainSystem) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_blockchainSystem)));
        return deployRemoteCall(PorthosContract.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<PorthosContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _blockchainSystem) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_blockchainSystem)));
        return deployRemoteCall(PorthosContract.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PorthosContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _blockchainSystem) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_blockchainSystem)));
        return deployRemoteCall(PorthosContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PorthosContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _blockchainSystem) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_blockchainSystem)));
        return deployRemoteCall(PorthosContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static PorthosContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PorthosContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PorthosContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PorthosContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PorthosContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PorthosContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PorthosContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PorthosContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
