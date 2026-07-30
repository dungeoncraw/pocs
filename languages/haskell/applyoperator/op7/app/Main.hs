module Main where
import Common.State

increment :: State Int Int
increment = 
    State $ \current ->
        let next = current + 1
        in (next, next)
-- both of these are equivalent
-- f <$> computation
-- fmap f computation
op7 :: State Int (Int, Int)
op7 = 
    (,) <$> increment <*> increment

main :: IO ()
main = 
    print (runState op7 2)
