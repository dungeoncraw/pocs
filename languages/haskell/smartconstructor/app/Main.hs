newtype Email = Email String
  deriving Show

mkEmail :: String -> Maybe Email
mkEmail value
  | '@' `elem` value = Just (Email value)
  | otherwise = Nothing

sendWelcomeEmail :: Email -> String
sendWelcomeEmail (Email address) =
  "Sending welcome email to " ++ address

main :: IO ()
main = do
  let goodEmail = mkEmail "good@email.com"
  let badEmail = mkEmail "not-an-email"

  print goodEmail
  print badEmail
  -- pattern match on the Maybe value returned by mkEmail
  case goodEmail of
    Just email ->
      putStrLn (sendWelcomeEmail email)

    Nothing ->
      putStrLn "Invalid email"