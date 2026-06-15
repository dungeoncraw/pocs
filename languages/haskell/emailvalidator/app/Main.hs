validateEmail :: String -> Maybe String
validateEmail email
    | '@' `elem` email = Just email
    | otherwise        = Nothing

validatePassword :: String -> Maybe String
validatePassword password
    | length password >= 8 = Just password
    | otherwise           = Nothing

main :: IO ()
main = do
    putStrLn "Enter your email:"
    email <- getLine
    putStrLn "Enter your password:"
    password <- getLine
    print $ validateEmail email
    print $ validatePassword password