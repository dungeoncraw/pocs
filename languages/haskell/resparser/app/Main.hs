data Resp
  = SimpleString String
  | RespError String
  | RespInteger Int
  deriving Show

parseResp :: String -> Either String Resp
parseResp ('+':rest) = Right (SimpleString (takeUntilCrlf rest))
parseResp ('-':rest) = Right (RespError (takeUntilCrlf rest))
parseResp (':':rest) = Right (RespInteger (read (takeUntilCrlf rest)))
parseResp _ = Left "Invalid RESP input"

takeUntilCrlf :: String -> String
takeUntilCrlf [] = []
takeUntilCrlf ('\r':'\n':_) = []
takeUntilCrlf (c:cs) = c : takeUntilCrlf cs

main :: IO ()
main = do
  print (parseResp "+OK\r\n")
  print (parseResp "-ERR wrong\r\n")
  print (parseResp ":100\r\n")