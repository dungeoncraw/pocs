data LoginError
    = UserNotFound
    | IncorrectPassword
    | UserLocked
    deriving Show
login :: String -> String -> Either LoginError String
login username password
    | username /= "admin" = Left UserNotFound
    | password /= "password" = Left IncorrectPassword
    | otherwise = Right "success-token-jwt"

main :: IO ()
main = do
    print(login "admin" "password")
    print(login "admin" "wrongpassword")
    print(login "user" "password")