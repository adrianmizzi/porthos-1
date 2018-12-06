pragma solidity ^0.4.24;

import "../framework/PorthosContract.sol";

contract T2Chain1Eur is PorthosContract {
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

  function continue_1() private
  {
    if(!semaphore["semLeft1"])
      return;
    if(!semaphore["semRight1"])
      return;
    fireEvent("ready");
    openGate("EUR", "c3");
  }

  function c3_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(msg.sender != charlie)
      return;
    if(_recipient != bob)
      return;
    if(!isGateOpen("c3"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("EUR", "c3", false);
    releaseAllCommitments();
  }

  function c3_timeout() public
  {
    if(block.number < 200)
      return;
    if(!isGateOpen("c3"))
      return;
    closeGate("EUR", "c3", true);
    cancelAllCommitments();
  }

  function start() public
  {
    semaphore["semLeft1"] = false;
    semaphore["semRight1"] = false;
    fireEvent("starting c1");
    openGate("EUR", "c1");
    fireEvent("starting c2");
    openGate("GBP", "c2");
  }

  function c1_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(msg.sender != bob)
      return;
    if(_recipient != alice)
      return;
    if(!isGateOpen("c1"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("EUR", "c1", false);
    semaphore["semLeft1"] = true;
    continue_1();
  }

  function c1_timeout() public
  {
    if(block.number < 200)
      return;
    if(!isGateOpen("c1"))
      return;
    closeGate("EUR", "c1", true);
    semaphore["semLeft1"] = true;
    continue_1();
  }

  function c2_complete_onuseraction() public
  {
    if(tx.origin != owner)
      return;
    semaphore["semRight1"] = true;
    continue_1();
  }

  function c2_complete_ontimeout() public
  {
    if(tx.origin != owner)
      return;
    semaphore["semRight1"] = true;
    continue_1();
  }

}
