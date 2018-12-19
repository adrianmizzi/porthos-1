package main

import (
  "bytes"
  "encoding/json"
  "fmt"
  "strconv"

  "github.com/hyperledger/fabric/core/chaincode/shim"
  sc "github.com/hyperledger/fabric/protos/peer"
)

type SmartContract struct {
}

func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
  return shim.Success(nil)
}

func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {
  // Retrieve the requested Smart Contract function and arguments
  function, args := APIstub.GetFunctionAndParameters()
  // Route to the appropriate handler function to interact with the ledger appropriately
  if function == "p2Commit_commit" {
    return s.p2Commit_commit(APIstub, args)
  } else if function == "p2Commit_timeout" {
    return s.p2Commit_timeout(APIstub, args)
  }

  return shim.Error("Invalid Smart Contract function name.")
}

func (s *SmartContract) p2Commit_commit(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
  if(_recipient != alice)
      return;
  if(!compareAssetType(_assetType, "Orange") || _quantity != 2)
      return;
  if(msg.sender != bob)
      return;
  if !s.isGateOpen(APIstub, "p2Commit") {
    return shim.Error("Gate is not open")
  }
  s.addCommitment(APIstub, args)
  s.closeGate(APIstub, "p2Commit")
  return shim.Success(nil)
}

func (s *SmartContract) p2Commit_timeout(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
  if(block.number < 20)
      return;
  if !s.isGateOpen(APIstub, "p2Commit") {
    return shim.Error("Gate is not open")
  }
  s.closeGate(APIstub, "p2Commit")
  return shim.Success(nil)
}
