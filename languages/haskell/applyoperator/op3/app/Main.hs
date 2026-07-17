module Main where
import Common.State


statefulAdder :: State Int (Int -> Int)
statefulAdder =
    State $ \current ->
        let generated = (+ current)
            newState = current + 1
        in (generated, newState)

op3 :: State Int Int
op3 =
    statefulAdder <*> pure 10

main :: IO ()
main = 
    print (runState op3 5)
