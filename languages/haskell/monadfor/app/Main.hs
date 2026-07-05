parseInt :: String -> Maybe Int
parseInt text =
  case reads text of
    [(number, "")] -> Just number
    _ -> Nothing

halfIfEven :: Int -> Maybe Int
halfIfEven number
  | even number = Just (number `div` 2)
  | otherwise = Nothing

parseAndHalf :: String -> Maybe Int
parseAndHalf text = do
  number <- parseInt text
  halfIfEven number

main :: IO ()
main = do

  print (parseAndHalf "20")
  print (parseAndHalf "15")
  print (parseAndHalf "abc")