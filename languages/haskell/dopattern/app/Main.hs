data Config = Config
  { host :: String
  , port :: Int
  , debug :: Bool
  } deriving Show

parseBool :: String -> Either String Bool
parseBool "true" = Right True
parseBool "false" = Right False
parseBool other = Left ("Invalid boolean: " ++ other)

parseConfig :: [(String, String)] -> Either String Config
parseConfig pairs = do
  h <- lookupEither "host" pairs
  p <- lookupEither "port" pairs
  d <- lookupEither "debug" pairs
  parsedDebug <- parseBool d
  pure (Config h (read p) parsedDebug)

lookupEither :: String -> [(String, String)] -> Either String String
lookupEither key pairs =
  case lookup key pairs of
    Just value -> Right value
    Nothing -> Left ("Missing key: " ++ key)

main :: IO ()
main = do
  print (parseConfig [("host", "localhost"), ("port", "6379"), ("debug", "true")])