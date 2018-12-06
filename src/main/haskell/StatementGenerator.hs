{-# LANGUAGE GADTs #-}
module StatementGenerator where

import           Data.IORef
import           Porthos
import           System.IO.Unsafe

{-# NOINLINE idCounter #-}
idCounter :: IORef Integer
idCounter = unsafePerformIO (newIORef 1)

{-# NOINLINE bothCounter #-}
bothCounter :: IORef Integer
bothCounter = unsafePerformIO (newIORef 1)

{-# NOINLINE ifCounter #-}
ifCounter :: IORef Integer
ifCounter = unsafePerformIO (newIORef 1)

-- generate a new label every time
nextId :: () -> Integer
nextId () = unsafePerformIO $ do
  p <- readIORef idCounter
  writeIORef idCounter (p Prelude.+ 1)
  return p

nextBothRef :: () -> Integer
nextBothRef () = unsafePerformIO $ do
  p <- readIORef bothCounter
  writeIORef bothCounter (p Prelude.+ 1)
  return p

nextIfRef :: () -> Integer
nextIfRef () = unsafePerformIO $ do
  p <- readIORef ifCounter
  writeIORef ifCounter (p Prelude.+ 1)
  return p


resetCounters :: IO ()
resetCounters = do
  writeIORef idCounter 1
  writeIORef bothCounter 1
  writeIORef ifCounter 1
  return ()

type State = Integer
type MethodName = String

data AccessModifier = Public | Private
  deriving (Eq)

instance Show AccessModifier where
  show Public  = "public"
  show Private = "private"

data PreCondition where
  PcOwner :: PreCondition
  PcFilter :: TxFilterExpr -> PreCondition
  PcTimeout :: Timeout -> PreCondition
  PcSemaphore :: String -> PreCondition
  PcGate :: String -> PreCondition

instance Show PreCondition where
  show PcOwner         = "PcOwner"
  show (PcFilter tf)   = "PcFilter " ++ show tf
  show (PcTimeout t)   = "PcTimeout " ++ show t
  show (PcSemaphore s) = "PcSemaphore " ++ show s
  show (PcGate s)      = "PcGate " ++ show s

data Method = Method {chain         :: Blockchain,
                      modifier      :: AccessModifier,
                      methodName    :: MethodName,
                      methodType    :: MethodType,
                      preconditions :: [PreCondition],
                      statements    :: [Statement]}

mainChain :: String
mainChain = "Ethereum_1"

privateMethod, publicMethod :: Method
privateMethod = Method {chain=mainChain, modifier=Private, methodName="", methodType=MTOther, preconditions=[], statements=[]}
publicMethod  = Method {chain=mainChain, modifier=Public, methodName="", methodType=MTOther, preconditions=[], statements=[]}

instance Show Method where
  show m = "Method" ++ 
           "\n   chain=" ++ show (chain m) ++
           "\n   modifier=" ++ show (modifier m) ++ 
           "\n   methodName=" ++ show (methodName m) ++
           "\n   methodType=" ++ show (methodType m) ++
           "\n   preconditions=" ++ show (preconditions m) ++
           "\n   statements=" ++ show (statements m) ++ "\n"

data MethodType = MTCommit |
                 MTCancel |
                 MTClaim |
                 MTAutoCancel |
                 MTRelease |
                 MTCommitTimeout |
                 MTCancelTimeout |
                 MTClaimTimeout |
                 MTConstructor |
                 MTOther | 
                 MTCompleteTimeout |
                 MTCompleteUserAction
  deriving (Show, Eq)

data Statement where 
  S_AddCommitment :: Statement
  S_ReleaseCommitment :: Commitment -> Statement
  S_AutoCancelCommitment :: Commitment -> Statement
  S_ReleaseAllCommitments ::  Statement
  S_AutoCancelAll :: Statement
  S_FireEvent :: String -> Statement
  S_IfThenElse :: CBool -> [Statement] -> [Statement] -> Statement
  S_InitSemaphore :: String -> Statement
  S_CompleteSemaphore :: String -> Statement
  S_ContinueWith :: String -> Statement
  S_OpenGate :: (AssetType t) => t -> String -> Statement
  S_CloseGate :: (AssetType t) => t -> String -> Bool -> Statement

instance Show Statement where
  show S_AddCommitment = "S_AddCommitment"
  show (S_ReleaseCommitment c) = "S_ReleaseCommitment " ++ show c
  show (S_AutoCancelCommitment c) = "S_AutoCancelCommitment " ++ show c
  show S_ReleaseAllCommitments = "S_ReleaseAllCommitments "
  show S_AutoCancelAll = "S_AutoCancelAll "
  show (S_FireEvent s) = "S_FireEvent " ++ show s
  show (S_IfThenElse b ss1 ss2) = "S_IfThenElse (" ++ show b ++ ") then (" ++ show ss1 ++ ") else (" ++ show ss2 ++ ")"
  show (S_InitSemaphore s) = "S_InitSemaphore " ++ show s
  show (S_CompleteSemaphore s)  = "S_CompleteSemaphore " ++ show s
  show (S_ContinueWith s) = "S_ContinueWith " ++ show s
  show (S_OpenGate t g) = "S_OpenGate " ++ show t ++ " " ++ show g
  show (S_CloseGate at g t)  = "S_CloseGate " ++ show at ++ " " ++ show g ++ " " ++ show t

generateStatements :: Method -> Contract -> [Method]
generateStatements currentMethod Null = [currentMethod]
generateStatements currentMethod (UserAction actionName assetType txf _ c t tc) =
          currentMethod' : methods1 ++ methods2
  where
    currentMethod' = addToMethod currentMethod (S_OpenGate assetType actionName)

    methods1 
      | chainOf assetType == mainChain = generateStatements cMethod c
      | otherwise                      = cMethod : generateStatements newCMethod c
    methods2 
      | chainOf assetType == mainChain = generateStatements tMethod tc
      | otherwise                      = tMethod : generateStatements newTMethod c

    newCMethod = publicMethod {chain=mainChain,
                              methodName=actionName, 
                              methodType=MTCompleteUserAction,
                              preconditions=[PcOwner]}

    newTMethod = publicMethod {chain=mainChain,
                              methodName=actionName, 
                              methodType=MTCompleteTimeout,
                              preconditions=[PcOwner]}

    cMethod = publicMethod {chain=chainOf assetType, 
                      methodName=actionName,
                      methodType=MTCommit,
                      preconditions=[PcFilter txf, PcGate actionName],
                      statements=[S_AddCommitment, S_CloseGate assetType actionName False]}
    tMethod = publicMethod {chain=chainOf assetType,
                      methodName=actionName, 
                      methodType=MTCommitTimeout,
                      preconditions=[PcTimeout t, PcGate actionName],
                      statements=[S_CloseGate assetType actionName True]}
generateStatements currentMethod (RepeatUserAction actionName assetType txf _ t tc) =
            currentMethod' : cMethod : methods
  where
    methods = generateStatements tMethod tc

    currentMethod' = addToMethod currentMethod (S_OpenGate assetType actionName)

    cMethod = publicMethod {methodName=actionName,
                      methodType=MTCommit,
                      preconditions=[PcFilter txf, PcGate actionName],
                      statements=[S_AddCommitment]}
    tMethod = publicMethod {methodName=actionName,
                      methodType=MTCommitTimeout,
                      preconditions=[PcTimeout t, PcGate actionName],
                      statements=[S_CloseGate assetType actionName True]}
generateStatements currentMethod (AutoAction tx c) = methods
  where
    methods = generateStatements currentMethod' c

    currentMethod' = addToMethod currentMethod (getS tx)

    getS :: AutoTx -> Statement
    getS ReleaseAll    = S_ReleaseAllCommitments
    getS AutoCancelAll = S_AutoCancelAll
    getS (Release commitment)   = S_ReleaseCommitment commitment
    getS (Cancel commitment)    = S_AutoCancelCommitment commitment

generateStatements currentMethod (FollowedBy c1 c2) = m2 ++ tail m1
  where
    m1 = generateStatements currentMethod c1
    m2 = generateStatements (head m1) c2
generateStatements currentMethod (FireEvent e c) = methods
  where
    methods = generateStatements currentMethod' c

    currentMethod' = addToMethod currentMethod (S_FireEvent e)

generateStatements currentMethod (IfThenElse b c1 c2) = currentMethod' : trueOtherM ++ falseOtherM
  where
    nextIfId = nextBothRef ()

    trueName = "ifTrue" ++ show nextIfId
    falseName = "ifFalse" ++ show nextIfId

    trueMethod = privateMethod {methodName=trueName, methodType=MTOther}
    falseMethod =  privateMethod {methodName=falseName, methodType=MTOther}

    ifTrue  = generateStatements trueMethod c1
    ifFalse = generateStatements falseMethod c2

    trueOtherM = [x | x <- ifTrue, methodName x /= trueName]
    falseOtherM = [x | x <- ifFalse, methodName x /= falseName]

    trueStatements  = head [statements x | x <- ifTrue, methodName x == trueName]
    falseStatements = head [statements x | x <- ifFalse, methodName x == falseName]

    currentMethod' = addToMethod currentMethod (S_IfThenElse b trueStatements falseStatements)

generateStatements currentMethod (OneOf c1 c2) = continueMethod : currentMethod' : leftM'' ++ rightM''
  where
    oneOfId = nextBothRef ()

    leftName  = "oneOfLeft" ++ show oneOfId
    rightName = "oneOfRight" ++ show oneOfId

    semaphore  = "semaphore" ++ show oneOfId

    left  = privateMethod {methodName=leftName,  methodType=MTOther} 
    right = privateMethod {methodName=rightName, methodType=MTOther}

    leftM  = generateStatements left c1
    rightM = generateStatements right c2

    newStatementsL = [S_CompleteSemaphore semaphore, S_ContinueWith contWithName]
    newStatementsR = [S_CompleteSemaphore semaphore, S_ContinueWith contWithName]

    leftM1'  = [addToMethod2 x newStatementsL | x <- leftM,  methodName x /= leftName, chain x == mainChain]
    rightM1' = [addToMethod2 x newStatementsR | x <- rightM, methodName x /= rightName, chain x == mainChain]
    leftM2'  = [x | x <- leftM,  methodName x /= leftName, chain x /= mainChain]
    rightM2' = [x | x <- rightM, methodName x /= rightName, chain x /= mainChain]

    leftStatements  = head [statements x | x <- leftM,  methodName x == leftName]
    rightStatements = head [statements x | x <- rightM, methodName x == rightName]

    leftM''  = leftM1' ++ leftM2'
    rightM'' = rightM1' ++ rightM2'

    currentMethod' = addToMethod2 currentMethod
                       (leftStatements ++ rightStatements)

    contWithName = "continue_" ++ show oneOfId
    continueMethod = privateMethod {methodName=contWithName,
                             methodType=MTOther,
                             preconditions=[PcSemaphore semaphore],
                             statements=[]}
generateStatements currentMethod (Both c1 c2) = continueMethod : currentMethod' : leftM'' ++ rightM''
  where
    bothId = nextBothRef ()

    leftName  = "bothLeft" ++ show bothId
    rightName = "bothRight" ++ show bothId

    semLeft  = "semLeft" ++ show bothId
    semRight = "semRight" ++ show bothId

    left  = privateMethod {methodName=leftName,  methodType=MTOther} 
    right = privateMethod {methodName=rightName, methodType=MTOther}

    leftM  = generateStatements left c1
    rightM = generateStatements right c2

    newStatementsL = [S_CompleteSemaphore semLeft,  S_ContinueWith contWithName]
    newStatementsR = [S_CompleteSemaphore semRight,  S_ContinueWith contWithName]

    leftM1'  = [addToMethod2 x newStatementsL | x <- leftM,  methodName x /= leftName, chain x == mainChain]
    rightM1' = [addToMethod2 x newStatementsR | x <- rightM, methodName x /= rightName, chain x == mainChain]
    leftM2'  = [x | x <- leftM,  methodName x /= leftName, chain x /= mainChain]
    rightM2' = [x | x <- rightM, methodName x /= rightName, chain x /= mainChain]

    leftStatements  = head [statements x | x <- leftM,  methodName x == leftName]
    rightStatements = head [statements x | x <- rightM, methodName x == rightName]

    leftM''  = leftM1' ++ leftM2'
    rightM'' = rightM1' ++ rightM2'

    currentMethod' = addToMethod2 currentMethod
                       ([S_InitSemaphore semLeft,
                        S_InitSemaphore semRight] ++ leftStatements ++ rightStatements)

    contWithName = "continue_" ++ show bothId
    continueMethod = privateMethod {methodName=contWithName,
                             methodType=MTOther,
                             preconditions=[PcSemaphore semLeft, PcSemaphore semRight],
                             statements=[]}


addToMethod :: Method -> Statement -> Method
addToMethod (Method c m n t preC ss) s = Method c m n t preC (ss ++ [s])

addToMethod2 :: Method -> [Statement] -> Method
addToMethod2 = foldl addToMethod

addToMethods :: [Method] -> Statement -> [Method]
addToMethods [] _     = []
addToMethods (m:mm) s = addToMethod m s : addToMethods mm s

addToMethods2 :: [Method] -> [Statement] -> [Method]
addToMethods2 [] _      = []
addToMethods2 (m:mm) ss = addToMethod2 m ss : addToMethods2 mm ss

-- filterToPreConditions :: TxFilterExpr -> [PreCondition]
-- filterToPreConditions NoTxFilter = []
-- filterToPreConditions (AndTF t1 t2) = filterToPreConditions t1 ++ filterToPreConditions t2
-- filterToPreConditions (OrTF t1 t2) = filterToPreConditions t1 ++ filterToPreConditions t2
-- filterToPreConditions (Sender p)   =
