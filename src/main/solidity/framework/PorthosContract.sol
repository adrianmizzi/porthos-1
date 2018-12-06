pragma solidity ^0.4.24;

import "./FungibleAsset.sol";
import "./strings.sol";
import "./Gateway.sol";
import "./Utils.sol";


contract PorthosContract {
  using strings for *;
  using Utils for *;

  struct Commitment {
      string tagId;
      address sender;
      address recipient;
      string assetType;
      uint quantity;
      uint status; // 0 = open; 1 = released; 2 = cancelled
  }

  struct AssetManager {
    string assetType;
    string blockchainSystem;
    address contractAddress;
  }

  mapping (string => bool) internal semaphore;
  mapping (string => uint8) private gateStatus; // 1 = open; 2 = closed
  mapping (string => FungibleAsset) internal assetRegisters;
  mapping (string => AssetManager) internal assetManager;
  uint internal contractStatus = 0;
  Commitment[] commitments;
  AssetManager[] managerArray;

  // stores information about own
  address internal owner;
  string internal ownName;

  // stored information about the master contract and in which blockchain system it is located
  address private master;
  string private masterBcName;

  Gateway internal gateway;

  function fireEvent(string _message) public {
    gateway.fireMessage(_message);
  }

  constructor (string _blockchainSystem) internal {
    owner = msg.sender;
    ownName = _blockchainSystem;
  }

  function setMaster(string _masterName, address _master) public {
    if (msg.sender != owner) 
      return;

    masterBcName = _masterName;
    master = _master;
  }

  function isMaster() private view returns (bool) {
    return ownName.compare(masterBcName);
  }

  /**
   * This function stores a mapping from asset type to which blockchain manages that asset.
   * Here we assume that one asset is only managed by one blockchain
   */
  function addAssetManager(string _assetType, string _blockchainSystem, address _contractAddress) public {
    assetManager[_assetType] = AssetManager({assetType : _assetType, blockchainSystem : _blockchainSystem, contractAddress : _contractAddress});
    managerArray.push(AssetManager({assetType : _assetType, blockchainSystem : _blockchainSystem, contractAddress : _contractAddress}));
  }

  /**
   * Adds an asset register to indicate that this asset is managed by this blockchain system
   */
  function addAssetRegister (string assetType) public returns (address) {
    assetRegisters[assetType] = new FungibleAsset(msg.sender);
    return assetRegisters[assetType];
  }

  /**
   * Revokes all assets of type assetType for the recipient
   */
  function revokeAllAssets(string assetType, address recipient) public {
    assetRegisters[assetType].revokeAllAssets(recipient);
  }

  /**
   * Issues an asset of type assetType, with the specific quantity to recipient
   */
  function issueAsset(string assetType, uint quantity, address recipient) public {
    if (msg.sender != owner) {
      return;
    }

    assetRegisters[assetType].issueAssets(recipient, quantity);
  }

  function addCommitment(Commitment c) internal {
    // transfer the ownership of the asset to this contract
    assetRegisters[c.assetType].transfer(c.sender, this, c.quantity);

    // add to commitments
    commitments.push(c);
  }

  function releaseCommitment(uint _commitmentId) internal {
    // fetch the commitment
    Commitment storage c = commitments[_commitmentId];

    // check that it is still open
    require(c.status == 0, "Commitment status must be Open (0) to allow release");

    // transfer the ownership of the asset (in the commitment) to the recipient
    assetRegisters[c.assetType].transfer(this, c.recipient, c.quantity);

    // mark commitment as released
    commitments[_commitmentId].status = 1;
  }

  function cancelCommitment(uint _commitmentId) internal {
    // fetch the commitment
    Commitment storage c = commitments[_commitmentId];

    // check that it is still open
    require(c.status == 0, "Commitment status must be Open (0) to allow cancel");


    // return the ownership of the asset (in the commitment) to the sender
    assetRegisters[c.assetType].transfer(this, c.sender, c.quantity);

    // mark commitment as cancelled
    commitments[_commitmentId].status = 2;
  }

  function fetchCommitment(uint _commitmentId) public view returns (string, uint, address, address, uint) {
    return (commitments[_commitmentId].assetType,
            commitments[_commitmentId].quantity,
            commitments[_commitmentId].sender,
            commitments[_commitmentId].recipient,
            commitments[_commitmentId].status);
  }

  function getContractStatus() public view returns (uint) {
    return contractStatus;
  }

  function setContractStatus(uint _status) internal {
    contractStatus = _status;
  }

  /**
   * Gate Management Function
   * If the asset, for which gate is being opened, is on this blockchain system, then we simply set the gate status to OPEN (1)
   * If the asset is on another blockchain, then we throw an event that will be caught and relayed by the router
   */
  function openGate(string _assetType, string _gateName) public {
    AssetManager storage am = assetManager[_assetType];
    if (am.blockchainSystem.compare(ownName)) {
      // we are managing this asset
      gateStatus[_gateName] = 1;

      gateway.gateOpened(_gateName);
    } else {
      // another blockchain system is managing the asset so we throw an event
      gateway.ccOpenGate(am.blockchainSystem, am.contractAddress, _assetType, _gateName);
    }
  }

  function closeGate(string _assetType, string _gateName, bool _timeout) internal {
    AssetManager storage am = assetManager[_assetType];
    if (am.blockchainSystem.compare(ownName)) {
      // we are managing this asset
      gateStatus[_gateName] = 2;
    } 

    gateway.gateClosed(_gateName, _timeout);

    // send a message to the master to continue computation
    if (!isMaster()) {
      if (_timeout) {
        gateway.initiateCCC(masterBcName, master, _gateName.strConcat("_complete_ontimeout()"));
      } else {
        gateway.initiateCCC(masterBcName, master, _gateName.strConcat("_complete_onuseraction()"));
      }
    }
  }

  function isGateOpen(string _gateName) internal view returns (bool) {
    return gateStatus[_gateName] == 1;
  }

  function isGateClosed(string _gateName) internal view returns (bool) {
    return gateStatus[_gateName] == 2;
  }

  function releaseCommitments(string _commitments) internal {
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    uint[] memory parts = new uint[](s.count(delim) + 1);

    for (uint i = 0; i < parts.length; i++) {
      parts[i] = s.split(delim).toString().stringToUint();
    }

    uint arrayLength = parts.length;

    for (uint j = 0; j < arrayLength; j++) {
      if (commitments[parts[j]].status == 0) {
        releaseCommitment(parts[j]);
      }
    }
  }

  function cancelCommitments(string _commitments) internal {
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    uint[] memory parts = new uint[](s.count(delim) + 1);

    for (uint i = 0; i < parts.length; i++) {
      parts[i] = s.split(delim).toString().stringToUint();
    }

    uint arrayLength = parts.length;

    for (uint j = 0; j < arrayLength; j++) {
      if (commitments[parts[j]].status == 0) {
        cancelCommitment(parts[j]);
      }
    }
  }

  function getAllCommitments() internal view returns (string) {
    uint arrayLength = commitments.length;

    string memory result = "";
    for (uint i = 0; i < arrayLength; i++) {
      result = string(abi.encodePacked(result, ",", i.uintToString()));
    }

    return result;
  }

  function fetchBalance(string _assetType, address _address) public view returns (uint) {
    return assetRegisters[_assetType].getBalance(_address);
  }

  function releaseAllCommitments() public {
    uint arrayLength = managerArray.length;

    for (uint i = 0; i < arrayLength; i++) {
      releaseAllCommitments(managerArray[i].assetType);
    }
  }

  function cancelAllCommitments() public {
    uint arrayLength = managerArray.length;

    for (uint i = 0; i < arrayLength; i++) {
      cancelAllCommitments(managerArray[i].assetType);
    }
  }

  /**
   * This function releases all the commitments held by this contract, and sends a message to 
   * other contracts to release their own commitments
   */
  function releaseAllCommitments(string _assetType) public {
    // lookup who the asset manager is for this type of asset
    AssetManager storage am = assetManager[_assetType];
    
    if (am.blockchainSystem.compare(ownName)) {
      uint arrayLength = commitments.length;

      for (uint i = 0; i < arrayLength; i++) {
        if (commitments[i].status == 0 && commitments[i].assetType.compare(_assetType)) {
          releaseCommitment(i);
        }
      }    

      fireEvent("All commitments released for asset type ".strConcat(_assetType));
    } else {
      gateway.ccReleaseCommitments(am.blockchainSystem, am.contractAddress, am.assetType);
    }   
  }

  /**
   * This function cancels all the commitments held by this contract, and sends a message to 
   * other contracts to cancel their own commitments
   */
  function cancelAllCommitments(string _assetType) public {
    // lookup who the asset manager is for this type of asset
    AssetManager storage am = assetManager[_assetType];
    
    if (am.blockchainSystem.compare(ownName)) {
      uint arrayLength = commitments.length;

      for (uint i = 0; i < arrayLength; i++) {
        if (commitments[i].status == 0 && commitments[i].assetType.compare(_assetType)) {
          cancelCommitment(i);
        }
      }    

      fireEvent("All commitments cancelled for asset type ".strConcat(_assetType));
    } else {
      gateway.ccCancelCommitments(am.blockchainSystem, am.contractAddress, am.assetType);
    }   
  }

  function countCommitments (string _commitments) internal pure returns (uint){
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    return s.count(delim) + 1;
  }

  function sumCommitments (string _type, string _commitments) internal view returns (uint) {
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    uint[] memory parts = new uint[](s.count(delim) + 1);

    for (uint i = 0; i < parts.length; i++) {
      parts[i] = s.split(delim).toString().stringToUint();
    }

    uint arrayLength = parts.length;
    uint sumTotal = 0;

    for (uint j = 0; j < arrayLength; j++) {
      if (commitments[parts[j]].assetType.compare(_type)) {
        sumTotal += commitments[parts[j]].quantity;
      }
    }    

    return sumTotal;  
  }

  function filterCommitmentsByType(string _type, string _commitments) internal view returns (string) {
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    uint[] memory parts = new uint[](s.count(delim) + 1);

    for (uint i = 0; i < parts.length; i++) {
      parts[i] = s.split(delim).toString().stringToUint();
    }

    uint arrayLength = parts.length;
    string memory result = "";

    for (uint j = 0; j < arrayLength; j++) {
      if (commitments[parts[j]].assetType.compare(_type)) {
        result = string(abi.encodePacked(result, ",", i.uintToString()));
      }
    }    

    return result;
  }

  function filterCommitmentsByRecipient(address _recipient, string _commitments) internal view returns (string) {
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    uint[] memory parts = new uint[](s.count(delim) + 1);

    for (uint i = 0; i < parts.length; i++) {
      parts[i] = s.split(delim).toString().stringToUint();
    }

    uint arrayLength = parts.length;
    string memory result = "";

    for (uint j = 0; j < arrayLength; j++) {
      if (commitments[parts[j]].recipient == _recipient) {
        result = string(abi.encodePacked(result, ",", i.uintToString()));
      }
    }    

    return result;
  }

  function filterCommitmentsBySender(address _sender, string _commitments) internal view returns (string) {
    strings.slice memory s = _commitments.toSlice();
    strings.slice memory delim = ",".toSlice();
    uint[] memory parts = new uint[](s.count(delim) + 1);

    for (uint i = 0; i < parts.length; i++) {
      parts[i] = s.split(delim).toString().stringToUint();
    }

    uint arrayLength = parts.length;
    string memory result = "";

    for (uint j = 0; j < arrayLength; j++) {
      if (commitments[parts[j]].sender == _sender) {
        result = string(abi.encodePacked(result, ",", i.uintToString()));
      }
    }    

    return result;
  }

  function compareAssetType(string _type1, string _type2) internal pure returns (bool){
    return _type1.compare(_type2);
  }

}
