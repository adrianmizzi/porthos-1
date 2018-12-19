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
    openGate("Apple", "p1Commit");
  }

  function p1Commit_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(_recipient != bob)
      return;
    if(!compareAssetType(_assetType, "Apple") || _quantity != 1)
      return;
    if(!isGateOpen("p1Commit"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("Apple", "p1Commit", false);
    openGate("Orange", "p2Commit");
  }

  function p2Commit_complete_onuseraction() public
  {
    if(tx.origin != owner)
      return;
    releaseAllCommitments();
  }

  function p2Commit_complete_ontimeout() public
  {
    if(tx.origin != owner)
      return;
    releaseAllCommitments();
  }

  function p1Commit_timeout() public
  {
    if(block.number < 10)
      return;
    if(!isGateOpen("p1Commit"))
      return;
    closeGate("Apple", "p1Commit", true);
  }

}