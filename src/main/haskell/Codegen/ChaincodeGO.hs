module Codegen.ChaincodeGO where

import           Porthos
import           StatementGenerator

generateChaincode :: [Method] -> String
generateChaincode methods = intro methods ++ generateCode methods

-- justStatements :: Contract -> String
-- justStatements c = show $ generateStatements m c
--     where
--       m = publicMethod{ methodName="constructor", 
--                         methodType=MTConstructor}

intro :: [Method] -> String
intro mm = "package main\n\n" ++
        "import (\n" ++
        "  \"bytes\"\n" ++
        "  \"encoding/json\"\n" ++
        "  \"fmt\"\n" ++
        "  \"strconv\"\n\n" ++
        "  \"github.com/hyperledger/fabric/core/chaincode/shim\"\n" ++
        "  sc \"github.com/hyperledger/fabric/protos/peer\"\n" ++
        ")\n\n" ++

        "type SmartContract struct {\n" ++
        "}\n\n" ++

        "func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {\n" ++ 
        "  return shim.Success(nil)\n" ++
        "}\n\n" ++
        "func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {\n" ++
        "  // Retrieve the requested Smart Contract function and arguments\n" ++
        "  function, args := APIstub.GetFunctionAndParameters()\n" ++
        "  // Route to the appropriate handler function to interact with the ledger appropriately\n" ++
        invokeMethod mm ++ 
        "  return shim.Error(\"Invalid Smart Contract function name.\")\n" ++
        "}\n"

invokeMethod :: [Method] -> String
invokeMethod [] = ""
invokeMethod (m:mm) =         
        "  if function == \"" ++ n ++ "\" {\n" ++
        "    return s." ++ n ++ "(APIstub, args)\n" ++
        invokeMethod' mm ++ 
        "  }\n\n"
  where
    n
      | methodType m == MTCommit = methodName m ++ "_commit"
      | methodType m == MTCommitTimeout = methodName m ++ "_timeout"
      | methodType m == MTCompleteUserAction = methodName m ++ "_complete_onuseraction"
      | methodType m == MTCompleteTimeout = methodName m ++ "_complete_ontimeout"
      | otherwise = methodName m

invokeMethod' :: [Method] -> String
invokeMethod' [] = ""
invokeMethod' (m:mm) = 
        "  } else if function == \"" ++ n ++ "\" {\n" ++
        "    return s." ++ n ++ "(APIstub, args)\n"
  where
    n
      | methodType m == MTCommit = methodName m ++ "_commit"
      | methodType m == MTCommitTimeout = methodName m ++ "_timeout"
      | methodType m == MTCompleteUserAction = methodName m ++ "_complete_onuseraction"
      | methodType m == MTCompleteTimeout = methodName m ++ "_complete_ontimeout"
      | otherwise = methodName m


-- func (s *SmartContract) createCommitment(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

-- 	if len(args) != 3 {
-- 		return shim.Error("Incorrect number of arguments. Expecting 3")
-- 	}

-- 	var commitment = Commitment{A: args[1], B: args[2]}

-- 	commitmentAsBytes, _ := json.Marshal(commitment)
-- 	APIstub.PutState(args[0], commitmentAsBytes)
-- 	APIstub.SetEvent("CommitmentCreationEvent", commitmentAsBytes);

-- 	return shim.Success(nil)
-- }

generateCode :: [Method] -> String
generateCode []     = ""
generateCode (m:mm) = generateMethodCode m ++ generateCode mm

generateMethodCode :: Method -> String
generateMethodCode (Method _ m n t pp ss)
  | t == MTConstructor        = "\nfunc (s *SmartContract) start(APIstub shim.ChaincodeStubInterface, args []string) sc.Response " ++ prog
  | t == MTCommit             = "\nfunc (s *SmartContract) " ++ n ++ "_commit(APIstub shim.ChaincodeStubInterface, args []string) sc.Response " ++ prog
  | t == MTCommitTimeout      = "\nfunc (s *SmartContract) " ++ n ++ "_timeout(APIstub shim.ChaincodeStubInterface, args []string) sc.Response " ++ prog
  | t == MTCompleteUserAction = "\nfunc (s *SmartContract) " ++ n ++ "_complete_onuseraction(APIstub shim.ChaincodeStubInterface, args []string) sc.Response " ++ prog
  | t == MTCompleteTimeout    = "\nfunc (s *SmartContract) " ++ n ++ "_complete_ontimeout(APIstub shim.ChaincodeStubInterface, args []string) sc.Response " ++ prog
  | otherwise                 = "\nfunc (s *SmartContract) " ++ n ++ prog
  where
    prog = -- "(" ++ params ++ ") " ++ 
            "{" ++ generatePreConditions pp ++ generateStmtCode ss ++ "\n  return shim.Success(nil)\n}\n"
    -- params
    --   | t == MTCommit = "string _assetType, uint _quantity, address _recipient"
    --   | otherwise = ""

generatePreConditions :: [PreCondition] -> String
generatePreConditions [] = ""
generatePreConditions (p:pp) = generatePreCondition p ++ generatePreConditions pp

generatePreCondition :: PreCondition -> String
generatePreCondition (PcFilter f)            = generateFilterCode f
generatePreCondition (PcTimeout (Timeout t)) = "\n  if(block.number < " ++ show t ++ ")\n      return;"
generatePreCondition (PcSemaphore s)         = "\n  if(!semaphore[" ++ show s ++ "])\n      return;"
generatePreCondition (PcGate s)              = "\n  if !s.isGateOpen(APIstub, " ++ show s ++ ") {\n    return shim.Error(\"Gate is not open\")\n  }" -- if(!isGateOpen(" ++ show s ++ "))\n      return;"
generatePreCondition PcOwner                 = "\n  if(tx.origin != owner)\n      return;"

generateFilterCode :: TxFilterExpr -> String
generateFilterCode NoTxFilter      = ""
generateFilterCode (AndTF f1 f2)   =  generateFilterCode f1 ++ generateFilterCode f2
-- generateFilterCode (OrTF f1 f2)    = "\n  require((" ++ generateFilterCode f1 ++ ") || (" ++ generateFilterCode f2 ++ "));"
generateFilterCode (Sender p)      = "\n  if(msg.sender != " ++ name p ++ ")\n      return;"
generateFilterCode (Recipient p)   = "\n  if(_recipient != " ++ name p ++")\n      return;";
generateFilterCode (AssetIs a)     = "\n  if(!compareAssetType(_assetType, \"" ++ show (getAssetType a) ++ "\") || _quantity != " ++ show (getAssetQuantity a) ++ ")\n      return;"
generateFilterCode (AssetTypeIs t) = "\n  if(!compareAssetType(_assetType, \"" ++ show t ++ "\")\n      return;"

generateStmtCode :: [Statement] -> String
generateStmtCode []     = ""
generateStmtCode (s:ss) = generateStmtCode' s ++ generateStmtCode ss

generateStmtCode' :: Statement -> String
generateStmtCode' S_AddCommitment              = "\n  s.addCommitment(APIstub, args)" --addCommitment(Commitment({tagId: \"\", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));"
generateStmtCode' S_ReleaseAllCommitments      = "\n  releaseAllCommitments();"
generateStmtCode' S_AutoCancelAll              = "\n  cancelAllCommitments();"
generateStmtCode' (S_FireEvent s)              = "\n  fireEvent(" ++ show s ++ ");"
generateStmtCode' (S_IfThenElse b ss1 ss2)     = "\n  if (" ++ generateCBool b ++ ") then \n    {" ++ generateStmtCode ss1 ++ "\n    }\n    else \n    {" ++ generateStmtCode ss2 ++ "\n    }"
generateStmtCode' (S_InitSemaphore s)          = "\n  semaphore[" ++ show s ++ "] = false;"
generateStmtCode' (S_CompleteSemaphore s)      = "\n  semaphore[" ++ show s ++ "] = true;"
generateStmtCode' (S_ContinueWith s)           = "\n  " ++ s ++ "();"
generateStmtCode' (S_OpenGate at s)            = "\n  openGate(\"" ++ show at ++ "\", " ++ show s ++ ");"
generateStmtCode' (S_CloseGate at s t)         = "\n  s.closeGate(APIstub, "++ show s ++ ")" -- closeGate(\"" ++ show at ++ "\", " ++ show s ++ ", " ++ map toLower (show t) ++ ");"
generateStmtCode' (S_ReleaseCommitment c)      = "\n  releaseCommitments(" ++ generateCommitmentDSLCode c ++ ");"
generateStmtCode' (S_AutoCancelCommitment c)   = "\n  cancelCommitments(" ++ generateCommitmentDSLCode c ++ ");"


generateCommitmentDSLCode :: Commitment -> String
generateCommitmentDSLCode AllCommitments = "getAllCommitments()"
generateCommitmentDSLCode (WhereCommitter p c) = "filterCommitmentsBySender(" ++ show p ++ ", " ++ generateCommitmentDSLCode c ++ ")"
generateCommitmentDSLCode (WhereRecipient p c) = "filterCommitmentsByRecipient(" ++ show p ++ ", " ++ generateCommitmentDSLCode c ++ ")"
generateCommitmentDSLCode (WhereAssetType t c) = "filterCommitmentsByType(\"" ++ show t ++ "\", " ++ generateCommitmentDSLCode c ++ ")"
generateCommitmentDSLCode _              = undefined


generateCBool :: CBool -> String
generateCBool CTrue        = "true"
generateCBool CFalse       = "false"
generateCBool (CAnd c1 c2) = generateCBool c1 ++ " && " ++ generateCBool c2
generateCBool (COr c1 c2)  = generateCBool c1 ++ " || " ++ generateCBool c2
generateCBool (CNot c)     = "!" ++ generateCBool c
generateCBool (CEQN x y)    = generateCodeN x ++ " == " ++ generateCodeN y
generateCBool (CGTN x y)    = generateCodeN x ++ " > " ++ generateCodeN y
generateCBool (CLTN x y)    = generateCodeN x ++ " < " ++ generateCodeN y
generateCBool (CGTEN x y)    = generateCodeN x ++ " >= " ++ generateCodeN y
generateCBool (CLTEN x y)    = generateCodeN x ++ " <= " ++ generateCodeN y
generateCBool (CEQA x y)    = generateCodeA x ++ " == " ++ generateCodeA y
generateCBool (CGTA x y)    = generateCodeA x ++ " > " ++ generateCodeA y
generateCBool (CLTA x y)    = generateCodeA x ++ " < " ++ generateCodeA y
generateCBool (CGTEA x y)    = generateCodeA x ++ " >= " ++ generateCodeA y
generateCBool (CLTEA x y)    = generateCodeA x ++ " <= " ++ generateCodeA y

generateCodeN :: N -> String
generateCodeN (I x) = show x
generateCodeN (Count c) = "countCommitments(" ++ generateCommitmentDSLCode c ++ ")"
generateCodeN (AddN x y) = "(" ++ generateCodeN x ++ " + " ++ generateCodeN y ++ ")"
generateCodeN (NegN x) = "0 -" ++ generateCodeN x

generateCodeA :: (AssetType t) => Asset t -> String
generateCodeA (Sum x c)             = "sumCommitments(\"" ++ show x ++ "\", " ++ generateCommitmentDSLCode c++ ")"
generateCodeA (Add x y)             = "(" ++ generateCodeA x ++ " + " ++ generateCodeA y ++ ")"
generateCodeA (Asset _ i)           = show i
generateCodeA (Convert (_, _, f) x) = "(" ++ generateCodeA x ++ " * " ++ show f ++ ")"

