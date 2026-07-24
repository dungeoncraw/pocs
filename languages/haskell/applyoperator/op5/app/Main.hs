module Main where

import Common.State

increment :: State Int Int
increment =
  State $ \current ->
    let next = current + 1
    in (next, next)
    
op5 :: State Int Int
op5 =
  pure (+) <*> increment <*> increment

main :: IO ()
main = 
    print (runState op5 2)