pragma solidity ^0.4.24;

import "./PorthosContract.sol";

contract T2 is PorthosContract {

  address alice = 0xed5356d32d00de3eb32dbafd96a04af60f49fa08;
  address bob = 0x088f3ea1e40afb3ac90a78738c5ab97071ed8c6c;
  address charlie = 0xc1a39c99ff252bcea57f0c2b8843a4ab445ab75d;

function continue_1() private
{
  if(!semaphore["semLeft1"]) 
    return;
  if(!semaphore["semRight1"]) 
    return;
  emit LogEvent("ready");
  openGate(EUR, "c3");
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
  closeGate("c3");
  releaseCommitments(getAllCommitments());
}

function c3_timeout() public
{
  if(block.number < 200) 
    return;
  if(!isGateOpen("c3")) 
    return;
  closeGate("c3");
  cancelCommitments(getAllCommitments());
}

constructor() public
{
  semaphore["semLeft1"] = false;
  semaphore["semRight1"] = false;
  emit LogEvent("starting c1");
  openGate(EUR, "c1");
  emit LogEvent("starting c2");
  openGate(GBP, "c2");
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
  closeGate("c1");
  semaphore["semLeft1"] = true;
  continue_1();
}

function c1_timeout() public
{
  if(block.number < 200) 
    return;
  if(!isGateOpen("c1")) 
    return;
  closeGate("c1");
  semaphore["semLeft1"] = true;
  continue_1();
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
  closeGate("c2");
  semaphore["semRight1"] = true;
  continue_1();
}

function c2_timeout() public
{
  if(block.number < 200) 
    return;
  if(!isGateOpen("c2")) 
    return;
  closeGate("c2");
  semaphore["semRight1"] = true;
  continue_1();
}

}