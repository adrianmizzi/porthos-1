module Codegen.Solidity where

import           Porthos
import           StatementGenerator
import           Data.Char


generateSolidity :: [Method] -> String
generateSolidity methods = intro (chain (head methods)) ++ generateCode methods ++ "\n}"

justStatements :: Contract -> String
justStatements c = show $ generateStatements m c
    where
      m = publicMethod{ methodName="constructor", 
                        methodType=MTConstructor}

intro :: Blockchain -> String
intro b = "pragma solidity ^0.4.24;\n\n" ++
        "import \"../framework/PorthosContract.sol\";\n\n" ++
        "contract " ++ b ++ " is PorthosContract {\n" ++
        "  address alice;\n" ++
        "  address bob;\n" ++
        "  address charlie;\n\n" ++ 
        "  constructor(address _alice, address _bob, address _charlie, address _gateway, string _blockchainName)\n" ++
        "    PorthosContract (_blockchainName) public\n" ++
        "  {\n" ++
        "    alice   = _alice;\n" ++
        "    bob     = _bob;\n" ++
        "    charlie = _charlie;\n" ++
        "    gateway = Gateway(_gateway);\n" ++
        "  }\n"


generateCode :: [Method] -> String
generateCode []     = ""
generateCode (m:mm) = generateMethodCode m ++ generateCode mm

generateMethodCode :: Method -> String
generateMethodCode (Method _ m n t pp ss)
  | t == MTConstructor        = "\n  function start" ++ prog
  | t == MTCommit             = "\n  function " ++ n ++ "_commit" ++ prog
  | t == MTCommitTimeout      = "\n  function " ++ n ++ "_timeout" ++ prog
  | t == MTCompleteUserAction = "\n  function " ++ n ++ "_complete_onuseraction" ++ prog
  | t == MTCompleteTimeout    = "\n  function " ++ n ++ "_complete_ontimeout" ++ prog
  | otherwise                 = "\n  function " ++ n ++ prog
  where
    prog = "(" ++ params ++ ") " ++ show m ++ "\n  {" ++
            generatePreConditions pp ++ generateStmtCode ss
            ++ "\n  }\n"
    params
      | t == MTCommit = "string _assetType, uint _quantity, address _recipient"
      | otherwise = ""

generatePreConditions :: [PreCondition] -> String
generatePreConditions [] = ""
generatePreConditions (p:pp) = generatePreCondition p ++ generatePreConditions pp

generatePreCondition :: PreCondition -> String
generatePreCondition (PcFilter f)            = generateFilterCode f
generatePreCondition (PcTimeout (Timeout t)) = "\n    if(block.number < " ++ show t ++ ")\n      return;"
generatePreCondition (PcSemaphore s)         = "\n    if(!semaphore[" ++ show s ++ "])\n      return;"
generatePreCondition (PcGate s)              = "\n    if(!isGateOpen(" ++ show s ++ "))\n      return;"
generatePreCondition PcOwner                 = "\n    if(tx.origin != owner)\n      return;"

generateFilterCode :: TxFilterExpr -> String
generateFilterCode NoTxFilter      = ""
generateFilterCode (AndTF f1 f2)   =  generateFilterCode f1 ++ generateFilterCode f2
-- generateFilterCode (OrTF f1 f2)    = "\n  require((" ++ generateFilterCode f1 ++ ") || (" ++ generateFilterCode f2 ++ "));"
generateFilterCode (Sender p)      = "\n    if(msg.sender != " ++ name p ++ ")\n      return;"
generateFilterCode (Recipient p)   = "\n    if(_recipient != " ++ name p ++")\n      return;";
generateFilterCode (AssetIs a)     = "\n    if(!compareAssetType(_assetType, \"" ++ show (getAssetType a) ++ "\") || _quantity != " ++ show (getAssetQuantity a) ++ ")\n      return;"
generateFilterCode (AssetTypeIs t) = "\n    if(!compareAssetType(_assetType, \"" ++ show t ++ "\")\n      return;"

generateStmtCode :: [Statement] -> String
generateStmtCode []     = ""
generateStmtCode (s:ss) = generateStmtCode' s ++ generateStmtCode ss

generateStmtCode' :: Statement -> String
generateStmtCode' S_AddCommitment              = "\n    addCommitment(Commitment({tagId: \"\", sender: msg.sender, recipient: _recipient, assetType: _assetType, quantity: _quantity, status: 0}));"
generateStmtCode' S_ReleaseAllCommitments      = "\n    releaseAllCommitments();"
generateStmtCode' S_AutoCancelAll              = "\n    cancelAllCommitments();"
generateStmtCode' (S_FireEvent s)              = "\n    fireEvent(" ++ show s ++ ");"
generateStmtCode' (S_IfThenElse b ss1 ss2)     = "\n    if (" ++ generateCBool b ++ ") then \n    {" ++ generateStmtCode ss1 ++ "\n    }\n    else \n    {" ++ generateStmtCode ss2 ++ "\n    }"
generateStmtCode' (S_InitSemaphore s)          = "\n    semaphore[" ++ show s ++ "] = false;"
generateStmtCode' (S_CompleteSemaphore s)      = "\n    semaphore[" ++ show s ++ "] = true;"
generateStmtCode' (S_ContinueWith s)           = "\n    " ++ s ++ "();"
generateStmtCode' (S_OpenGate at s)            = "\n    openGate(\"" ++ show at ++ "\", " ++ show s ++ ");"
generateStmtCode' (S_CloseGate at s t)         = "\n    closeGate(\"" ++ show at ++ "\", " ++ show s ++ ", " ++ map toLower (show t) ++ ");"
generateStmtCode' (S_ReleaseCommitment c)      = "\n    releaseCommitments(" ++ generateCommitmentDSLCode c ++ ");"
generateStmtCode' (S_AutoCancelCommitment c)   = "\n    cancelCommitments(" ++ generateCommitmentDSLCode c ++ ");"


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

-- generateCommitmentCode :: Commitment -> String
-- generateCommitmentCode AllCommitments = "getAllCommitments()"
-- -- generateCommitmentCode (AndCF c1 c2) = "(" ++ generateCommitmentCode c1 ++ ") and (" ++ generateCommitmentCode c2 ++ ")"
-- -- generateCommitmentCode (OrCF c1 c2)  = "(" ++ generateCommitmentCode c1 ++ ") or (" ++ generateCommitmentCode c2 ++ ")"
-- generateCommitmentCode (WhereCommitter p c) = "Where committer is " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- generateCommitmentCode (WhereRecipient p c) = "Where recipient is " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- generateCommitmentCode (WhereAssetType p c) = "Where assetType is " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereDate p c) = "Where date is " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereDateBefore p c) = "Where date before " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereDateAfter p c) = "Where date after " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereAssetQuantity p c) = "Where quantity is " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereAssetQuantityLess p c) = "Where quantity less than  " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereAssetQuantityGreater p c) = "Where quantity greater than " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (WhereId p c) = "Where id is " ++ show p ++ "(" ++ generateCommitmentCode c ++ ")"
-- -- generateCommitmentCode (OrderCF p o c) = "order by  " ++ show p ++ " " ++ show o ++ "(" ++ generateCommitmentCode c ++ ")"
-- generateCommitmentCode _ = undefined


