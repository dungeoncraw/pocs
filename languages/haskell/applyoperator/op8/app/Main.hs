module Main where
import Common.State

increment :: State Int Int
increment = 
    State $ \current ->
        let next = current + 1
        in (next, next)
getState :: State s s
getState = 
    State $ \current -> (current, current)

data Snapshot = Snapshot 
    {
        previous :: Int,
        current :: Int
    } deriving (Show)

op8 :: State Int Snapshot
op8 = 
    Snapshot <$> getState <*> increment

main :: IO ()
main = 
    print (runState op8 2)
