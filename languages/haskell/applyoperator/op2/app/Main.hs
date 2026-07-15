module Main where

import Common.State

increment :: State Int Int
increment =
  State $ \current ->
    let next = current + 1
    in (next, next)
    
op2 :: State Int Int
op2 =
  pure (*2) <*> increment


main :: IO ()
main = 
    print (runState op2 5)
