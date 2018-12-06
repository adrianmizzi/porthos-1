package porthos.ethereum.contracts.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class Gateway extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50611295806100206000396000f3006080604052600436106100ab5763ffffffff60e060020a6000350416630c79a4c381146100b057806311a724ef1461011957806344e1ce0d1461017657806347566a5f146101dd5780634a8b3917146102c15780636b46e3bc1461032a5780637eb6228e1461038357806380dc908e146103dc578063afdc91b414610481578063c06e8f9414610527578063dca4665f146105cd578063e2a6083a14610634578063f2749ddb1461069d575b600080fd5b3480156100bc57600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610117958335600160a060020a03169536956044949193909101919081908401838280828437509497506107439650505050505050565b005b34801561012557600080fd5b506040805160206004803580820135601f8101849004840285018401909552848452610117943694929360249392840191908190840183828082843750949750505050913515159250610825915050565b34801561018257600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610117958335600160a060020a03169536956044949193909101919081908401838280828437509497506108ce9650505050505050565b3480156101e957600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261011794369492936024939284019190819084018382808284375050604080516020601f818a01358b0180359182018390048302840183018552818452989b600160a060020a038b35169b909a90999401975091955091820193509150819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506109d89650505050505050565b3480156102cd57600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610117958335600160a060020a03169536956044949193909101919081908401838280828437509497505093359450610b5f9350505050565b34801561033657600080fd5b506040805160206004803580820135601f8101849004840285018401909552848452610117943694929360249392840191908190840183828082843750949750610c8b9650505050505050565b34801561038f57600080fd5b506040805160206004803580820135601f8101849004840285018401909552848452610117943694929360249392840191908190840183828082843750949750610d499650505050505050565b3480156103e857600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610117958335600160a060020a031695369560449491939091019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a999881019791965091820194509250829150840183828082843750949750610de59650505050505050565b34801561048d57600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261011794369492936024939284019190819084018382808284375050604080516020601f818a01358b0180359182018390048302840183018552818452989b600160a060020a038b35169b909a909994019750919550918201935091508190840183828082843750949750610f1a9650505050505050565b34801561053357600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261011794369492936024939284019190819084018382808284375050604080516020601f818a01358b0180359182018390048302840183018552818452989b600160a060020a038b35169b909a9099940197509195509182019350915081908401838280828437509497506110399650505050505050565b3480156105d957600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610117958335600160a060020a03169536956044949193909101919081908401838280828437509497506110b59650505050505050565b34801561064057600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610117958335600160a060020a0316953695604494919390910191908190840183828082843750949750509335945061112d9350505050565b3480156106a957600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261011794369492936024939284019190819084018382808284375050604080516020601f818a01358b0180359182018390048302840183018552818452989b600160a060020a038b35169b909a9099940197509195509182019350915081908401838280828437509497506111ed9650505050505050565b6040517f073e85b70000000000000000000000000000000000000000000000000000000081526020600482018181528351602484015283518593600160a060020a0385169363073e85b7938793909283926044909101919085019080838360005b838110156107bc5781810151838201526020016107a4565b50505050905090810190601f1680156107e95780820380516001836020036101000a031916815260200191505b5092505050600060405180830381600087803b15801561080857600080fd5b505af115801561081c573d6000803e3d6000fd5b50505050505050565b7f52babca7cb42723146dc2ea8db2c726ddc484aa9ec81a8a442474e75a61354148282604051808060200183151515158152602001828103825284818151815260200191508051906020019080838360005b8381101561088f578181015183820152602001610877565b50505050905090810190601f1680156108bc5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a15050565b81600160a060020a0316816040516020018082805190602001908083835b6020831061090b5780518252601f1990920191602091820191016108ec565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b6020831061096e5780518252601f19909201916020918201910161094f565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060e060020a90046040518163ffffffff1660e060020a0281526004016000604051808303816000875af19250505015156109d457600080fd5b5050565b7fb5c5043e1b2301fbef9a3d06f4e911717fda1cd87f647aae3071d904be95fc1984848484604051808060200185600160a060020a0316600160a060020a031681526020018060200180602001848103845288818151815260200191508051906020019080838360005b83811015610a5a578181015183820152602001610a42565b50505050905090810190601f168015610a875780820380516001836020036101000a031916815260200191505b50848103835286518152865160209182019188019080838360005b83811015610aba578181015183820152602001610aa2565b50505050905090810190601f168015610ae75780820380516001836020036101000a031916815260200191505b50848103825285518152855160209182019187019080838360005b83811015610b1a578181015183820152602001610b02565b50505050905090810190601f168015610b475780820380516001836020036101000a031916815260200191505b5097505050505050505060405180910390a150505050565b7ffbae48b012d13856174b7aee7b0f404f7ac7638a85d03bb2f18eb075f5ec6e348383836040518084600160a060020a0316600160a060020a0316815260200180602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610bde578181015183820152602001610bc6565b50505050905090810190601f168015610c0b5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a160408051436020820152328183015260608082526010908201527f526571756573742043616c6c6261636b00000000000000000000000000000000608082015290517faf85a14b6e06dc1f57f4f75fb7bf96f82980e065fe76c00976d1bfe6803266629181900360a00190a1505050565b7faf85a14b6e06dc1f57f4f75fb7bf96f82980e065fe76c00976d1bfe680326662814332604051808060200184815260200183600160a060020a0316600160a060020a03168152602001828103825285818151815260200191508051906020019080838360005b83811015610d0a578181015183820152602001610cf2565b50505050905090810190601f168015610d375780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a150565b7f0f15d4ce90f80ab33ee4e1a64813dfbdc6c41d7fa6427165ded81f8a3c619276816040518080602001828103825283818151815260200191508051906020019080838360005b83811015610da8578181015183820152602001610d90565b50505050905090810190601f168015610dd55780820380516001836020036101000a031916815260200191505b509250505060405180910390a150565b600083905080600160a060020a03166354155bd584846040518363ffffffff1660e060020a028152600401808060200180602001838103835285818151815260200191508051906020019080838360005b83811015610e4e578181015183820152602001610e36565b50505050905090810190601f168015610e7b5780820380516001836020036101000a031916815260200191505b50838103825284518152845160209182019186019080838360005b83811015610eae578181015183820152602001610e96565b50505050905090810190601f168015610edb5780820380516001836020036101000a031916815260200191505b50945050505050600060405180830381600087803b158015610efc57600080fd5b505af1158015610f10573d6000803e3d6000fd5b5050505050505050565b7f61f87e530e6dc0e912fa9a3d5a6c1ef6e4481fd205963633c706f95f67f265d1838383604051808060200184600160a060020a0316600160a060020a0316815260200180602001838103835286818151815260200191508051906020019080838360005b83811015610f97578181015183820152602001610f7f565b50505050905090810190601f168015610fc45780820380516001836020036101000a031916815260200191505b50838103825284518152845160209182019186019080838360005b83811015610ff7578181015183820152602001610fdf565b50505050905090810190601f1680156110245780820380516001836020036101000a031916815260200191505b509550505050505060405180910390a1505050565b7f789cb267811e16019b70baf9e73ebe13bc3e8f598f2af80fa56e8c01d8b1d5f3838383604051808060200184600160a060020a0316600160a060020a03168152602001806020018381038352868181518152602001915080519060200190808383600083811015610f97578181015183820152602001610f7f565b6040517fcff257280000000000000000000000000000000000000000000000000000000081526020600482018181528351602484015283518593600160a060020a0385169363cff2572893879390928392604490910191908501908083836000838110156107bc5781810151838201526020016107a4565b7f64feac9a4ecd5322419d9cd1ea242c183b3bc7694e5c5a8be25a099da5828fc48383836040518084600160a060020a0316600160a060020a0316815260200180602001838152602001828103825284818151815260200191508051906020019080838360005b838110156111ac578181015183820152602001611194565b50505050905090810190601f1680156111d95780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a1505050565b7f0fe9f05da86a17b80a51ac782202839e77dded56de5cff6fc73709e86d3a66cc838383604051808060200184600160a060020a0316600160a060020a03168152602001806020018381038352868181518152602001915080519060200190808383600083811015610f97578181015183820152602001610f7f5600a165627a7a72305820edb6d937f51c193b270ea5d1babed08c3b0aea7ad28c0df6d19c8d203f46d9020029";

    public static final String FUNC_CANCELALLCOMMITMENTSCALL = "cancelAllCommitmentsCall";

    public static final String FUNC_GATECLOSED = "gateClosed";

    public static final String FUNC_CALL = "call";

    public static final String FUNC_CCOPENGATE = "ccOpenGate";

    public static final String FUNC_REQUESTCALLBACK = "requestCallback";

    public static final String FUNC_FIREMESSAGE = "fireMessage";

    public static final String FUNC_GATEOPENED = "gateOpened";

    public static final String FUNC_OPENGATECALL = "openGateCall";

    public static final String FUNC_CCRELEASECOMMITMENTS = "ccReleaseCommitments";

    public static final String FUNC_INITIATECCC = "initiateCCC";

    public static final String FUNC_RELEASEALLCOMMITMENTSCALL = "releaseAllCommitmentsCall";

    public static final String FUNC_CANCELCALLBACK = "cancelCallback";

    public static final String FUNC_CCCANCELCOMMITMENTS = "ccCancelCommitments";

    public static final Event LOG_EVENT = new Event("Log", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event CALLBACKREQUEST_EVENT = new Event("CallbackRequest", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event CALLBACKCANCEL_EVENT = new Event("CallbackCancel", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event CROSSCHAIN_EVENT = new Event("CrossChain", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event CCOPENGATE_EVENT = new Event("CCOpenGate", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event CCRELEASECOMMITMENTS_EVENT = new Event("CCReleaseCommitments", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event CCCANCELCOMMITMENTS_EVENT = new Event("CCCancelCommitments", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event GATEOPENED_EVENT = new Event("GateOpened", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    public static final Event GATECLOSED_EVENT = new Event("GateClosed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}));
    ;

    @Deprecated
    protected Gateway(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Gateway(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Gateway(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Gateway(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> cancelAllCommitmentsCall(String contractAddress, String assetType) {
        final Function function = new Function(
                FUNC_CANCELALLCOMMITMENTSCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> gateClosed(String _gateName, Boolean _timeout) {
        final Function function = new Function(
                FUNC_GATECLOSED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_gateName), 
                new org.web3j.abi.datatypes.Bool(_timeout)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> call(String contractAddress, String method) {
        final Function function = new Function(
                FUNC_CALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(method)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> ccOpenGate(String _bcSystem, String _contractAddress, String _assetType, String _gateName) {
        final Function function = new Function(
                FUNC_CCOPENGATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_bcSystem), 
                new org.web3j.abi.datatypes.Address(_contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(_assetType), 
                new org.web3j.abi.datatypes.Utf8String(_gateName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> requestCallback(String _contractAddress, String _methodName, BigInteger _timeRequested) {
        final Function function = new Function(
                FUNC_REQUESTCALLBACK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(_methodName), 
                new org.web3j.abi.datatypes.generated.Uint256(_timeRequested)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> fireMessage(String _message) {
        final Function function = new Function(
                FUNC_FIREMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_message)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> gateOpened(String _gateName) {
        final Function function = new Function(
                FUNC_GATEOPENED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_gateName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> openGateCall(String contractAddress, String assetType, String gateName) {
        final Function function = new Function(
                FUNC_OPENGATECALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(assetType), 
                new org.web3j.abi.datatypes.Utf8String(gateName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> ccReleaseCommitments(String _bcSystem, String _contractAddress, String _assetType) {
        final Function function = new Function(
                FUNC_CCRELEASECOMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_bcSystem), 
                new org.web3j.abi.datatypes.Address(_contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(_assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> initiateCCC(String _bcSystem, String _contractAddress, String _methodName) {
        final Function function = new Function(
                FUNC_INITIATECCC, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_bcSystem), 
                new org.web3j.abi.datatypes.Address(_contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(_methodName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> releaseAllCommitmentsCall(String contractAddress, String assetType) {
        final Function function = new Function(
                FUNC_RELEASEALLCOMMITMENTSCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> cancelCallback(String _contractAddress, String _methodName, BigInteger _timeRequested) {
        final Function function = new Function(
                FUNC_CANCELCALLBACK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(_methodName), 
                new org.web3j.abi.datatypes.generated.Uint256(_timeRequested)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> ccCancelCommitments(String _bcSystem, String _contractAddress, String _assetType) {
        final Function function = new Function(
                FUNC_CCCANCELCOMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_bcSystem), 
                new org.web3j.abi.datatypes.Address(_contractAddress), 
                new org.web3j.abi.datatypes.Utf8String(_assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<LogEventResponse> getLogEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOG_EVENT, transactionReceipt);
        ArrayList<LogEventResponse> responses = new ArrayList<LogEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogEventResponse typedResponse = new LogEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.message = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogEventResponse> logEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogEventResponse>() {
            @Override
            public LogEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOG_EVENT, log);
                LogEventResponse typedResponse = new LogEventResponse();
                typedResponse.log = log;
                typedResponse.message = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.sender = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogEventResponse> logEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOG_EVENT));
        return logEventObservable(filter);
    }

    public List<CallbackRequestEventResponse> getCallbackRequestEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CALLBACKREQUEST_EVENT, transactionReceipt);
        ArrayList<CallbackRequestEventResponse> responses = new ArrayList<CallbackRequestEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CallbackRequestEventResponse typedResponse = new CallbackRequestEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.methodName = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timeRequested = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CallbackRequestEventResponse> callbackRequestEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CallbackRequestEventResponse>() {
            @Override
            public CallbackRequestEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CALLBACKREQUEST_EVENT, log);
                CallbackRequestEventResponse typedResponse = new CallbackRequestEventResponse();
                typedResponse.log = log;
                typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.methodName = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.timeRequested = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CallbackRequestEventResponse> callbackRequestEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CALLBACKREQUEST_EVENT));
        return callbackRequestEventObservable(filter);
    }

    public List<CallbackCancelEventResponse> getCallbackCancelEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CALLBACKCANCEL_EVENT, transactionReceipt);
        ArrayList<CallbackCancelEventResponse> responses = new ArrayList<CallbackCancelEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CallbackCancelEventResponse typedResponse = new CallbackCancelEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.methodName = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timeRequested = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CallbackCancelEventResponse> callbackCancelEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CallbackCancelEventResponse>() {
            @Override
            public CallbackCancelEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CALLBACKCANCEL_EVENT, log);
                CallbackCancelEventResponse typedResponse = new CallbackCancelEventResponse();
                typedResponse.log = log;
                typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.methodName = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.timeRequested = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CallbackCancelEventResponse> callbackCancelEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CALLBACKCANCEL_EVENT));
        return callbackCancelEventObservable(filter);
    }

    public List<CrossChainEventResponse> getCrossChainEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CROSSCHAIN_EVENT, transactionReceipt);
        ArrayList<CrossChainEventResponse> responses = new ArrayList<CrossChainEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CrossChainEventResponse typedResponse = new CrossChainEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.methodName = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CrossChainEventResponse> crossChainEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CrossChainEventResponse>() {
            @Override
            public CrossChainEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CROSSCHAIN_EVENT, log);
                CrossChainEventResponse typedResponse = new CrossChainEventResponse();
                typedResponse.log = log;
                typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.methodName = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CrossChainEventResponse> crossChainEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CROSSCHAIN_EVENT));
        return crossChainEventObservable(filter);
    }

    public List<CCOpenGateEventResponse> getCCOpenGateEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CCOPENGATE_EVENT, transactionReceipt);
        ArrayList<CCOpenGateEventResponse> responses = new ArrayList<CCOpenGateEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CCOpenGateEventResponse typedResponse = new CCOpenGateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetType = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.gateName = (String) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CCOpenGateEventResponse> cCOpenGateEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CCOpenGateEventResponse>() {
            @Override
            public CCOpenGateEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CCOPENGATE_EVENT, log);
                CCOpenGateEventResponse typedResponse = new CCOpenGateEventResponse();
                typedResponse.log = log;
                typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetType = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.gateName = (String) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CCOpenGateEventResponse> cCOpenGateEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CCOPENGATE_EVENT));
        return cCOpenGateEventObservable(filter);
    }

    public List<CCReleaseCommitmentsEventResponse> getCCReleaseCommitmentsEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CCRELEASECOMMITMENTS_EVENT, transactionReceipt);
        ArrayList<CCReleaseCommitmentsEventResponse> responses = new ArrayList<CCReleaseCommitmentsEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CCReleaseCommitmentsEventResponse typedResponse = new CCReleaseCommitmentsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetType = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CCReleaseCommitmentsEventResponse> cCReleaseCommitmentsEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CCReleaseCommitmentsEventResponse>() {
            @Override
            public CCReleaseCommitmentsEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CCRELEASECOMMITMENTS_EVENT, log);
                CCReleaseCommitmentsEventResponse typedResponse = new CCReleaseCommitmentsEventResponse();
                typedResponse.log = log;
                typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetType = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CCReleaseCommitmentsEventResponse> cCReleaseCommitmentsEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CCRELEASECOMMITMENTS_EVENT));
        return cCReleaseCommitmentsEventObservable(filter);
    }

    public List<CCCancelCommitmentsEventResponse> getCCCancelCommitmentsEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CCCANCELCOMMITMENTS_EVENT, transactionReceipt);
        ArrayList<CCCancelCommitmentsEventResponse> responses = new ArrayList<CCCancelCommitmentsEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CCCancelCommitmentsEventResponse typedResponse = new CCCancelCommitmentsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetType = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CCCancelCommitmentsEventResponse> cCCancelCommitmentsEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CCCancelCommitmentsEventResponse>() {
            @Override
            public CCCancelCommitmentsEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CCCANCELCOMMITMENTS_EVENT, log);
                CCCancelCommitmentsEventResponse typedResponse = new CCCancelCommitmentsEventResponse();
                typedResponse.log = log;
                typedResponse.blockchainSystem = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.contractAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetType = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CCCancelCommitmentsEventResponse> cCCancelCommitmentsEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CCCANCELCOMMITMENTS_EVENT));
        return cCCancelCommitmentsEventObservable(filter);
    }

    public List<GateOpenedEventResponse> getGateOpenedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(GATEOPENED_EVENT, transactionReceipt);
        ArrayList<GateOpenedEventResponse> responses = new ArrayList<GateOpenedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            GateOpenedEventResponse typedResponse = new GateOpenedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.gateName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<GateOpenedEventResponse> gateOpenedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, GateOpenedEventResponse>() {
            @Override
            public GateOpenedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(GATEOPENED_EVENT, log);
                GateOpenedEventResponse typedResponse = new GateOpenedEventResponse();
                typedResponse.log = log;
                typedResponse.gateName = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<GateOpenedEventResponse> gateOpenedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(GATEOPENED_EVENT));
        return gateOpenedEventObservable(filter);
    }

    public List<GateClosedEventResponse> getGateClosedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(GATECLOSED_EVENT, transactionReceipt);
        ArrayList<GateClosedEventResponse> responses = new ArrayList<GateClosedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            GateClosedEventResponse typedResponse = new GateClosedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.gateName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timeout = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<GateClosedEventResponse> gateClosedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, GateClosedEventResponse>() {
            @Override
            public GateClosedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(GATECLOSED_EVENT, log);
                GateClosedEventResponse typedResponse = new GateClosedEventResponse();
                typedResponse.log = log;
                typedResponse.gateName = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.timeout = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<GateClosedEventResponse> gateClosedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(GATECLOSED_EVENT));
        return gateClosedEventObservable(filter);
    }

    public static RemoteCall<Gateway> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Gateway.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Gateway> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Gateway.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Gateway> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Gateway.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Gateway> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Gateway.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static Gateway load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Gateway(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Gateway load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Gateway(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Gateway load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Gateway(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Gateway load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Gateway(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class LogEventResponse {
        public Log log;

        public String message;

        public BigInteger blockNumber;

        public String sender;
    }

    public static class CallbackRequestEventResponse {
        public Log log;

        public String contractAddress;

        public String methodName;

        public BigInteger timeRequested;
    }

    public static class CallbackCancelEventResponse {
        public Log log;

        public String contractAddress;

        public String methodName;

        public BigInteger timeRequested;
    }

    public static class CrossChainEventResponse {
        public Log log;

        public String blockchainSystem;

        public String contractAddress;

        public String methodName;
    }

    public static class CCOpenGateEventResponse {
        public Log log;

        public String blockchainSystem;

        public String contractAddress;

        public String assetType;

        public String gateName;
    }

    public static class CCReleaseCommitmentsEventResponse {
        public Log log;

        public String blockchainSystem;

        public String contractAddress;

        public String assetType;
    }

    public static class CCCancelCommitmentsEventResponse {
        public Log log;

        public String blockchainSystem;

        public String contractAddress;

        public String assetType;
    }

    public static class GateOpenedEventResponse {
        public Log log;

        public String gateName;
    }

    public static class GateClosedEventResponse {
        public Log log;

        public String gateName;

        public Boolean timeout;
    }
}
