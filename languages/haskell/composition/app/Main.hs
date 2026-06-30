import Data.Char (toLower)

trimSpaces :: String -> String
trimSpaces text =
  unwords (words text)

lowercase :: String -> String
lowercase = map toLower

normalize :: String -> String
normalize =
  trimSpaces . lowercase

reverseNormalized :: String -> String
reverseNormalized =
  reverse . normalize

main :: IO ()
main = do
  putStrLn "Function composition using dot operator"

  putStrLn (normalize "   HELLO     HASKELL   ")
  putStrLn (reverseNormalized "   HELLO     HASKELL   ")