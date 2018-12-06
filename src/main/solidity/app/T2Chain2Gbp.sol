pragma solidity ^0.4.24;

import "../framework/PorthosContract.sol";

contract T2Chain2Gbp is PorthosContract {
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

  function c2_commit(string _assetType, uint _quantity, address _recipient) public
  {
    if(msg.sender != alice)
      return;
    if(_recipient != bob)
      return;
    if(!isGateOpen("c2"))
      return;
    addCommitment(Commitment({tagId: "", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));
    closeGate("GBP", "c2", false);
  }

  function c2_timeout() public
  {
    if(block.number < 200)
      return;
    if(!isGateOpen("c2"))
      return;
    closeGate("GBP", "c2", true);
  }

}
