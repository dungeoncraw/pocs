data User = User
  { userId :: Int
  , userName :: String
  , userEmail :: String
  } deriving Show

splitOn :: Char -> String -> [String]
splitOn _ "" = [""]
splitOn delimiter (c:cs)
  | c == delimiter = "" : rest
  | otherwise = (c : head rest) : tail rest
  where
    rest = splitOn delimiter cs

parseUser :: String -> Maybe User
parseUser line =
  case splitOn ',' line of
    [idText, name, email] -> Just (User (read idText) name email)
    _ -> Nothing

main :: IO ()
main = do
  let csvLine = "1,April,apr@email.com"
  print (parseUser csvLine)