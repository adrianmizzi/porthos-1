module General where

import Codegen.Solidity
import Porthos
import StatementGenerator
import Data.List

toScreen :: ChainToLang -> Contract -> IO[()]
toScreen c2l c = do
    resetCounters;
    let methods = contractToMethods c;
    let chains = extractChains methods;
    let f b = putStr $ "\n\n" ++ generateSolidity (extractMethods b methods);
    mapM f chains

toFiles :: ChainToLang -> Contract -> IO[()]
toFiles c2l c = do
        resetCounters;
        let methods = contractToMethods c;
        let chains = extractChains methods;
        let f b = writeFile (path ++ b ++ ".sol") (generateSolidity (extractMethods b methods)) 
        mapM (f) chains
    where
        path = "/Users/adrian/Cloud Drives/Dropbox/Dev/porthos/src/main/solidity/app/"



main2 :: Contract -> IO()
main2 c = do
    resetCounters;
    putStr $ justStatements c


contractToMethods :: Contract -> [Method]
contractToMethods c = generateStatements mainMethod c
  where 
    mainMethod = publicMethod {methodName="constructor",
                  methodType=MTConstructor}

extractChains :: [Method] -> [Blockchain]
extractChains mm = nub [chain m | m <- mm]

extractMethods :: Blockchain -> [Method] -> [Method]
extractMethods b mm = [m | m <- mm, chain m == b]