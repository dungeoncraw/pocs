module Main where

import Common.State

op1 :: State Int Int
op1 =
  pure (+1) <*> pure 10

main :: IO ()
main =
  print (runState op1 100)