module Main where
import Common.State

increment :: State Int Int
increment =
    State $ \current ->
        let next = current + 1
        in (next, next)

chooseResult :: Bool -> State Int Int
chooseResult firstChoose = 
    pure (\first second -> if firstChoose then first else second)
    <*> increment
    <*> increment


main :: IO ()
main = 
    print (runState (chooseResult False) 0)
