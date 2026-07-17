module Main where
import Common.State

statefulFunction :: State Int (Int -> Int)
statefulFunction =
    State $ \state ->
        ((+ state), state + 1)

statefulValue :: State Int Int
statefulValue =
    State $ \state ->
        (state * 10, state + 1)

op4 :: State Int Int
op4 =
    -- so apply first the statefulFunction to State, then pass it to statefulValue to run and apply the function to the value
    statefulFunction <*> statefulValue

main :: IO ()
main = 
    print (runState op4 2)
