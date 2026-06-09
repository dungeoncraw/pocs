module Main where

import Lib (cToF, fToC)

main :: IO ()
main = do
    putStrLn $ "0°C in Fahrenheit: " ++ show (cToF 0)
    putStrLn $ "32°F in Celsius: " ++ show (fToC 32)
    putStrLn $ "100°C in Fahrenheit: " ++ show (cToF 100)
    putStrLn $ "212°F in Celsius: " ++ show (fToC 212)
