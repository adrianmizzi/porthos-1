module Lang where

import           Porthos

end :: Contract
end = Null

-- claim :: (ActionName, TxFilterExpr, Commitment, Contract, Timeout, Contract) -> Contract
-- claim (n, tf, commitment, continueWith, timeout, c') =
--   UserAction n tf (Claim commitment) continueWith timeout c'

-- cancel :: (ActionName, TxFilterExpr, Commitment, Contract, Timeout, Contract) -> Contract
-- cancel (n, tf, commitment, continueWith, timeout, c') =
--   UserAction n tf (CancelCommit commitment) continueWith timeout c'

onUserCommit :: (AssetType t) => ActionName -> (t, TxFilterExpr) -> Contract -> (Timeout, Contract) -> Contract
onUserCommit n (t, x) c y = commitWithTagAndId n t x c y NoTag NoId

commitWithTag :: (AssetType t) => ActionName -> t -> TxFilterExpr -> Contract -> (Timeout, Contract) -> TagId -> Contract
commitWithTag n t x c y tag = commitWithTagAndId n t x c y tag NoId

commitWithId :: (AssetType t) => ActionName -> t -> TxFilterExpr -> Contract -> (Timeout, Contract) -> CommitmentId -> Contract
commitWithId n t x c y = commitWithTagAndId n t x c y NoTag

commitWithTagAndId :: (AssetType t) => ActionName -> t -> TxFilterExpr -> Contract -> (Timeout, Contract) -> TagId -> CommitmentId -> Contract
commitWithTagAndId n t tf continueWith (timeout, c) tag cId=
  UserAction n t tf (Commit cId tag) continueWith timeout c

repeatCommit :: (AssetType t) => ActionName -> (t, TxFilterExpr) -> (Timeout, Contract) -> Contract
repeatCommit n (at, tf) (timeout, c) =
  RepeatUserAction n at (tf .&. isAssetType at) (Commit NoId NoTag) timeout c

autoRelease :: Commitment -> Contract -> Contract
autoRelease commitment = AutoAction (Release commitment)

autoCancel :: Commitment -> Contract -> Contract
autoCancel commitment = AutoAction (Cancel commitment)

onTimeout :: Time -> Contract -> (Timeout, Contract)
onTimeout t c = (Timeout t, c)

releaseAll :: Contract -> Contract
releaseAll = AutoAction ReleaseAll

cancelAll :: Contract -> Contract
cancelAll = AutoAction AutoCancelAll

isCommitTo :: Participant -> TxFilterExpr
isCommitTo = Recipient

isCommitBy :: Participant -> TxFilterExpr
isCommitBy = Sender

isAsset :: (AssetType t) => Asset t -> TxFilterExpr
isAsset = AssetIs

isAssetType :: (AssetType t) => t -> TxFilterExpr
isAssetType = AssetTypeIs

ifThenElse :: CBool -> (Contract, Contract) -> Contract
ifThenElse cond (c1, c2) = IfThenElse cond c1 c2

fireEvent :: String -> Contract -> Contract
fireEvent = FireEvent

-- assetType :: (AssetType t) => Asset t -> t
-- assetType (Asset t _) = t
-- assetType (Sum t _)   = t
-- assetType (Add x _)   = assetType x

assetType :: (AssetType t) => Asset t -> t
assetType = Porthos.getAssetType

followedBy :: Contract -> Contract -> Contract
followedBy = FollowedBy

both :: (Contract, Contract) -> Contract
both (c1, c2) = Both c1 c2

oneOf :: (Contract, Contract) -> Contract
oneOf (c1, c2) = OneOf c1 c2

asset :: (AssetType t) => (t, Integer) -> Asset t
asset (t, q) = Asset t q

(.>>>.) :: Contract -> Contract -> Contract
a .>>>. b = a `followedBy` b

-- CBool
true :: CBool
true = CTrue

false :: CBool
false = CFalse

not :: CBool -> CBool
not = CNot

(.&&.) :: CBool -> CBool -> CBool
x .&&. y = CAnd x y

(.||.) :: CBool -> CBool -> CBool
x .||. y = COr x y

(.+.) :: (AssetType t) => Asset t -> Asset t -> Asset t
x .+. y = Add x y

exchange :: (AssetType tx, AssetType ty) => (tx, ty, Float) -> Asset tx -> Asset ty
exchange (_, b, f) (Asset _ q) = Asset b (round (f * fromIntegral q))
exchange (a, b, f) x = Convert (a, b, f) x

-- Commitments
allCommitments :: Commitment
allCommitments = AllCommitments

whereCommitterIs :: (Participant, Commitment) -> Commitment
whereCommitterIs (p, c) = WhereCommitter p c

whereRecipientIs :: (Participant, Commitment) -> Commitment
whereRecipientIs (p, c) = WhereRecipient p c

whereAssetTypeIs :: (AssetType t) => (t, Commitment) -> Commitment
whereAssetTypeIs (t, c) = WhereAssetType t c

orderByParticipant :: Order -> Commitment -> Commitment
orderByParticipant = OrderCF CParticipantField

asc :: Order
asc = ASC

desc :: Order
desc = DESC

selectAll :: (Commitment -> Commitment) -> Commitment -> Commitment
selectAll f = f

sumC :: (Show t, AssetType t) => (t, Commitment) -> Asset t
sumC (assetType, commitments) = Sum assetType commitments

countC :: Commitment -> N
countC = Count

