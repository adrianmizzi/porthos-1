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
    if (true) 
    {
      fireEvent("c1");
    } else 
    { 
      fireEvent("c2");
    }
  }
}
