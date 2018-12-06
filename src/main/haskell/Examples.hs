module Examples where


import           General
import           Lang
import           Porthos


alice, bob, charlie :: Participant
alice = Participant {name="alice", address="0xf1c13a88cf28c4d06269a150dd8cdb2e3061d44f"}
bob = Participant {name="bob", address="0x4603bd7000aba82eab4aaea605df43a1e37ef2bb"}
charlie = Participant {name="charlie", address="0xb9f62ffe791ff9fa9c51722e3b833bf51db290de"}


mem :: ChainToLang
mem = update (update initMem ("Ethereum_1", Solidity)) ("Ethereum_2", Solidity)


data MyAssetType = Apple | Orange
  deriving (Show, Eq)

instance AssetType MyAssetType where
  chainOf _ = "Ethereum_1"

-- Atomic Swap
swap :: (Participant, Asset MyAssetType) -> (Participant, Asset MyAssetType) -> Contract
swap (p1, a1) (p2, a2) = onUserCommit "p1Commit" (EUR, (isCommitTo p2 .&. isAsset a1))
                           doP2Commit 
                           (onTimeout 10 end)
  where
    doP2Commit = onUserCommit "p2Commit" (GBP, (isCommitTo p1 .&. isAsset a2 .&. isCommitBy p2))
                    (releaseAll end)
                    (onTimeout 20 (cancelAll end))

-- Crowdfunding
crowdFunding :: Participant -> Asset Currency -> Contract
crowdFunding recipient target
          = repeatCommit "fund" (EUR, isCommitTo recipient)
              (onTimeout 100 closeCampaign)
  where
    aType = assetType target
    closeCampaign = ifThenElse (sumCommit .>. target)
                      (releaseAll (fireEvent "Campaign Successful" end),
                       cancelAll (fireEvent "Campagin Failed" end))
    sumCommit = sumC (aType,allCommitments)

-- Group Pay
groupPay :: [(Participant, Asset Currency)] -> Participant -> Contract
groupPay yy recipient = allOf (userCommits yy) `followedBy`
                          ifThenElse (countC(allCommitments) .==. liftN (length yy))
                            (releaseAll end,
                            cancelAll end)
  where
    userCommits = map (\x -> onUserCommit (name (fst x)) (EUR, txFilter x) end (onTimeout 100 end))
    txFilter (a, b) = isCommitTo recipient .&. isCommitBy a .&. isAsset b

allOf :: [Contract] -> Contract
allOf []     = Null
allOf [c]    = c
allOf (c:cc) = both (c, allOf cc)

-- Time-locked piggy bank
piggy :: Participant -> Time -> Contract
piggy recipient expiryTime = repeatCommit "save" (Apple, isCommitTo recipient)
                               (onTimeout expiryTime (releaseAll end))

-- Crowdfunding on multiple assets
crowdFunding2 :: Participant -> (Currency, Currency, Float) -> Asset Currency -> Contract
crowdFunding2 recipient (x, y, f) targetY = both (campaignX, campaignY) `followedBy` closeCampaign
  where
    campaignX = repeatCommit "fundX" (x, isCommitTo recipient)
                  (onTimeout 100 end)
    campaignY = repeatCommit "fundY" (y, isCommitTo recipient)
                  (onTimeout 100 end)
    closeCampaign = ifThenElse (totalY .>. targetY)
                      (releaseAll (fireEvent "Campaign Successful" end),
                       cancelAll (fireEvent "Campaign Failed" end))
    sumCommitX = sumC (x, allCommitments)
    sumCommitY = sumC (y, allCommitments)

    totalY = sumCommitY .+. exchange (x, y, f) sumCommitX

data Property = Property -- {ref :: String}
  deriving (Show, Eq)

instance AssetType Property where
  chainOf _ = "Ethereum_1"
  typeOf Property = "Property"

data Currency = USD | EUR | GBP
  deriving (Show)

instance AssetType Currency where
  chainOf USD = "Ethereum_1"
  chainOf EUR = "Ethereum_1"
  chainOf GBP = "Ethereum_2"
  typeOf = show 

data Vote = ApprovedByNotary | RejectedByNotary
  deriving (Show)

instance AssetType Vote where
  chainOf _ = "Ethereum_1"
  typeOf = show

-- Property Sale
propSale :: (Participant, Asset Property)
              -> Participant -> Asset Currency -> Asset Currency
              -> Participant -> Contract
propSale (seller, property) buyer deposit balance notary =
            onUserCommit "commitProperty" (getAssetType property, isCommitTo buyer .&. isCommitBy seller .&. isAsset property)
              doBuyerCommit
              (onTimeout 10 end)
  where
    doBuyerCommit   = onUserCommit "payDeposit" (EUR, isCommitBy buyer .&. isCommitTo seller .&. isAsset deposit)
                        doBalanceCommit
                        (onTimeout 20 (cancelAll end))
    doBalanceCommit = onUserCommit "payBalance" (EUR, isCommitTo seller .&. isAsset balance) -- buyer or bank submits balance
                        (oneOf (notaryApproval, notaryRejection))
                        (onTimeout 100 sellerTakesAll)
    sellerTakesAll  = autoRelease (whereRecipientIs(seller, allCommitments))
                        (autoCancel (whereCommitterIs(seller, allCommitments))
                          end)
    notaryApproval  = onUserCommit "approved" (ApprovedByNotary, isCommitBy notary .&. isCommitTo notary)
                       (releaseAll end)
                       (onTimeout 200 (cancelAll end))
    notaryRejection = onUserCommit "rejected" (RejectedByNotary, isCommitBy notary .&. isCommitTo notary)
                        (cancelAll end)
                        (onTimeout 200 (cancelAll end))

notaryApprove :: Asset Vote
notaryApprove = asset(ApprovedByNotary, 1)

notaryReject :: Asset Vote
notaryReject = asset(RejectedByNotary, 1)

hamrunFlat :: Asset Property
hamrunFlat = asset(Property, 1) -- {ref="1,High Street, Hamrun"}

deposit, balance :: Asset Currency
deposit = asset(EUR, 10000)
balance = asset(EUR, 90000)

t1 :: Contract
t1 = both (fireEvent "event 1" end, fireEvent "event 2" end)

t2 :: Contract
t2 = both (c1, c2) .>>>. fireEvent "ready" c3
  where
    c1 = fireEvent "starting c1"
           (onUserCommit "c1" (EUR, isCommitBy bob .&. isCommitTo alice) end
             (onTimeout 200 end))
    c2 = fireEvent "starting c2"
           (onUserCommit "c2" (GBP, isCommitBy alice .&. isCommitTo bob) end
             (onTimeout 200 end))
    c3 = onUserCommit "c3" (EUR, isCommitBy charlie .&. isCommitTo bob) (releaseAll end)
          (onTimeout 200 (cancelAll end))

t3 :: Contract
t3 = both (c1, c2) .>>>. releaseAll end
  where
    c1 = onUserCommit "c1" (EUR, isCommitBy bob .&. isCommitTo alice) end
             (onTimeout 200 end)
    c2 = onUserCommit "c2" (GBP, isCommitBy alice .&. isCommitTo bob) end
             (onTimeout 200 end)

t4 :: Contract
t4 = ifThenElse (sumC(USD, allCommitments) .>. asset(USD, 100)) (c1, c2)
    where
      c1 = fireEvent "c1" end
      c2 = fireEvent "c2" end

t5 :: Contract
t5 = ifThenElse (sumC(USD, commitments) .<=. asset (USD, 100)) (c1, c2)
    where
      c1 = fireEvent "c1" end
      c2 = fireEvent "c2" end

      commitments = whereCommitterIs(alice, allCommitments)

-- just an example on how to instantiate contracts
runExample :: IO[()]
runExample = toScreen mem t3





liftN :: Int -> N
liftN x = I (toInteger x)


