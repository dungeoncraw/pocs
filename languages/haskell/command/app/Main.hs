data Command
  = Set String String
  | Get String
  | Del String
  | Exists String
  deriving Show

parseCommand :: String -> Either String Command
parseCommand input =
-- words splits the input string into a list of words, which we can then pattern match on to determine the command type and its arguments.
  case words input of
    ["SET", key, value] -> Right (Set key value)
    ["GET", key] -> Right (Get key)
    ["DEL", key] -> Right (Del key)
    ["EXISTS", key] -> Right (Exists key)
    _ -> Left "Invalid command"

main :: IO ()
main = do
  print (parseCommand "SET name Abigail")
  print (parseCommand "GET name")
  print (parseCommand "INVALID test")