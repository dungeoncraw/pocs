module Main where

import Common.State

increment :: State Int Int
increment =
  State $ \current ->
    let next = current + 1
    in (next, next)

op6 :: State Int [Int]
op6 =
  pure (\a b c -> [a, b, c])
    <*> increment
    <*> increment
    <*> increment

main :: IO ()
main = 
    print (runState op6 2)
