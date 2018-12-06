pragma solidity ^0.4.24;

import "../framework/PorthosContract.sol";

contract Ethereum_1 is PorthosContract {
  address alice;
  address bob;
  address charlie;

  constructor(address _alice, address _bob, address _charlie, address _gateway, string _blockchainName)
    PorthosContract (_blockchainName) public
  {
    alice   = _alice;
    bob     = _bob;
    charlie = _charlie;
    gateway = Gateway(_gateway);
  }

  function start() public
  {
    openGate("Property", "commitProperty");
  }

  function commitProperty_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(_recipient != bob)
      return;
    if(msg.sender != alice)
      return;
    if(!compareAssetType(_assetType, "Property") || _quantity != 1)
      return;
    if(!isGateOpen("commitProperty"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("Property", "commitProperty", false);
    openGate("EUR", "payDeposit");
  }

  function payDeposit_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(msg.sender != bob)
      return;
    if(_recipient != alice)
      return;
    if(!compareAssetType(_assetType, "EUR") || _quantity != 10000)
      return;
    if(!isGateOpen("payDeposit"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("EUR", "payDeposit", false);
    openGate("EUR", "payBalance");
  }

  function continue_1() private
  {
    if(!semaphore["semaphore1"])
      return;
  }

  function payBalance_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(_recipient != alice)
      return;
    if(!compareAssetType(_assetType, "EUR") || _quantity != 90000)
      return;
    if(!isGateOpen("payBalance"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("EUR", "payBalance", false);
    openGate("ApprovedByNotary", "approved");
    openGate("RejectedByNotary", "rejected");
  }

  function approved_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(msg.sender != charlie)
      return;
    if(_recipient != charlie)
      return;
    if(!isGateOpen("approved"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("ApprovedByNotary", "approved", false);
    releaseAllCommitments();
    semaphore["semaphore1"] = true;
    continue_1();
  }

  function approved_timeout() public
  {
    if(block.number < 200)
      return;
    if(!isGateOpen("approved"))
      return;
    closeGate("ApprovedByNotary", "approved", true);
    cancelAllCommitments();
    semaphore["semaphore1"] = true;
    continue_1();
  }

  function rejected_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(msg.sender != charlie)
      return;
    if(_recipient != charlie)
      return;
    if(!isGateOpen("rejected"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("RejectedByNotary", "rejected", false);
    cancelAllCommitments();
    semaphore["semaphore1"] = true;
    continue_1();
  }

  function rejected_timeout() public
  {
    if(block.number < 200)
      return;
    if(!isGateOpen("rejected"))
      return;
    closeGate("RejectedByNotary", "rejected", true);
    cancelAllCommitments();
    semaphore["semaphore1"] = true;
    continue_1();
  }

  function payBalance_timeout() public
  {
    if(block.number < 100)
      return;
    if(!isGateOpen("payBalance"))
      return;
    closeGate("EUR", "payBalance", true);
    releaseCommitments(filterCommitmentsByRecipient(alice, getAllCommitments()));
    cancelCommitments(filterCommitmentsBySender(alice, getAllCommitments()));
  }

  function payDeposit_timeout() public
  {
    if(block.number < 20)
      return;
    if(!isGateOpen("payDeposit"))
      return;
    closeGate("EUR", "payDeposit", true);
    cancelAllCommitments();
  }

  function commitProperty_timeout() public
  {
    if(block.number < 10)
      return;
    if(!isGateOpen("commitProperty"))
      return;
    closeGate("Property", "commitProperty", true);
  }

}