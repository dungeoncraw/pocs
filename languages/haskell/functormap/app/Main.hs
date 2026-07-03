incrementMaybe :: Maybe Int -> Maybe Int
incrementMaybe = fmap (+ 1)

reverseNames :: [String] -> [String]
reverseNames = fmap reverse

addPrefixMaybe :: Maybe String -> Maybe String
addPrefixMaybe = fmap ("Prefix: " ++)

main :: IO ()
main = do
  print (incrementMaybe (Just 10))
  print (incrementMaybe Nothing)

  print (reverseNames ["Haskell", "Scala", "Clojure"])

  print (addPrefixMaybe (Just "Someone"))
  print (addPrefixMaybe Nothing)