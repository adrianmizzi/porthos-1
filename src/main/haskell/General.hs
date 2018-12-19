module General where

import Codegen.Solidity
import Codegen.ChaincodeGO
import Porthos
import StatementGenerator
import Data.List

toScreen :: ChainToLang -> Contract -> IO[()]
toScreen c2l c = do
    resetCounters;
    let methods = contractToMethods c;
    let chains = extractChains methods;
    let f b = putStr $ "\n\n" ++ generateContractCode (c2l b) (extractMethods b methods) -- generateSolidity (extractMethods b methods);
    mapM f chains

toFiles :: ChainToLang -> Contract -> IO[()]
toFiles c2l c = do
        resetCounters;
        let methods = contractToMethods c;
        let chains = extractChains methods;
        let f b = writeFile (path b ++ b ++ fileExtension b) (generateContractCode (c2l b) (extractMethods b methods)) -- generateSolidity (extractMethods b methods))
        mapM f chains
    where
        path b = "/Users/adrian/Cloud Drives/Dropbox/Dev/porthos/src/main/" ++ langPath b ++ "/app/"

        langPath b
          | c2l b == Solidity  = "solidity"
          | c2l b == Chaincode = "golang"
          | otherwise          = "UnknownLang"

        fileExtension b 
          | c2l b == Solidity  = ".sol"
          | c2l b == Chaincode = ".go"
          | otherwise          = ".err"


-- main2 :: Contract -> IO()
-- main2 c = do
--     resetCounters;
--     putStr $ justStatements c


contractToMethods :: Contract -> [Method]
contractToMethods c = generateStatements mainMethod c
  where 
    mainMethod = publicMethod {methodName="constructor",
                  methodType=MTConstructor}

extractChains :: [Method] -> [Blockchain]
extractChains mm = nub [chain m | m <- mm]

extractMethods :: Blockchain -> [Method] -> [Method]
extractMethods b mm = [m | m <- mm, chain m == b]

generateContractCode :: Language -> [Method] -> String
generateContractCode Solidity mm = generateSolidity mm
generateContractCode Chaincode mm = generateChaincode mm