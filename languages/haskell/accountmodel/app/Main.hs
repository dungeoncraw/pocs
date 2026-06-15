data Account = Account
    { accountId :: Int
    , owner :: String
    , balance :: Double
    } deriving Show

deposit :: Double -> Account -> Account
deposit amount account = 
    account { balance = balance account + amount}

withdraw :: Double -> Account -> Either String Account
withdraw amount account
    | amount <= 0 = Left "Withdrawal amount must be positive."
    | amount > balance account = Left "Insufficient funds."
    | otherwise = Right account {balance = balance account - amount}

main :: IO ()
main = do
    let account1 = Account 1 "Alice" 1000.0
    print account1

    let account2 = deposit 500.0 account1
    print account2

    case withdraw 200.0 account2 of
        Left errorMsg -> putStrLn $ "Error: " ++ errorMsg
        Right updatedAccount -> print updatedAccount

    case withdraw 2500.0 account2 of
        Left errorMsg -> putStrLn $ "Error: " ++ errorMsg
        Right updatedAccount -> print updatedAccount