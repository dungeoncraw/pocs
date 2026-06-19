data JsonValue
  = JsonString String
  | JsonNumber Double
  | JsonBool Bool
  | JsonNull
  | JsonArray [JsonValue]
  | JsonObject [(String, JsonValue)]
  deriving Show

renderJson :: JsonValue -> String
renderJson (JsonString s) = show s
renderJson (JsonNumber n) = show n
renderJson (JsonBool True) = "true"
renderJson (JsonBool False) = "false"
renderJson JsonNull = "null"
renderJson (JsonArray values) =
  "[" ++ joinWith "," (map renderJson values) ++ "]"
renderJson (JsonObject fields) =
  "{" ++ joinWith "," (map renderField fields) ++ "}"
  where
    renderField (key, value) = show key ++ ":" ++ renderJson value

joinWith :: String -> [String] -> String
joinWith _ [] = ""
joinWith _ [x] = x
joinWith sep (x:xs) = x ++ sep ++ joinWith sep xs

main :: IO ()
main = do
  let json = JsonObject [("name", JsonString "Thiago"), ("age", JsonNumber 30)]
  putStrLn (renderJson json)