module Lib where

cToF :: Double -> Double
cToF c = c * 9 / 5 + 32

fToC :: Double -> Double
fToC f = (f - 32) * 5 / 9
