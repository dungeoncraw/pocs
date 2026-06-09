module Main where

import Test.HUnit
import System.Exit (exitFailure)
import Lib (cToF, fToC)

-- Test cases for cToF
testCToFZero :: Test
testCToFZero = TestCase (assertEqual "0°C should be 32°F" 32.0 (cToF 0))

testCToFNegative :: Test
testCToFNegative = TestCase (assertEqual "-40°C should be -40°F" (-40.0) (cToF (-40)))

testCToFPositive :: Test
testCToFPositive = TestCase (assertEqual "100°C should be 212°F" 212.0 (cToF 100))

-- Test cases for fToC
testFToCZero :: Test
testFToCZero = TestCase (assertEqual "32°F should be 0°C" 0.0 (fToC 32))

testFToCNegative :: Test
testFToCNegative = TestCase (assertEqual "-40°F should be -40°C" (-40.0) (fToC (-40)))

testFToCPositive :: Test
testFToCPositive = TestCase (assertEqual "212°F should be 100°C" 100.0 (fToC 212))

-- Test round-trip conversion
testRoundTrip :: Test
testRoundTrip = TestCase (assertEqual "Round trip conversion should work" 25.0 (fToC (cToF 25)))

-- Combine all tests
allTests :: Test
allTests = TestList
  [ TestLabel "cToF 0°C = 32°F" testCToFZero
  , TestLabel "cToF -40°C = -40°F" testCToFNegative
  , TestLabel "cToF 100°C = 212°F" testCToFPositive
  , TestLabel "fToC 32°F = 0°C" testFToCZero
  , TestLabel "fToC -40°F = -40°C" testFToCNegative
  , TestLabel "fToC 212°F = 100°C" testFToCPositive
  , TestLabel "Round trip conversion" testRoundTrip
  ]

main :: IO ()
main = do
  result <- runTestTT allTests
  if failures result > 0 || errors result > 0
    then exitFailure
    else return ()
