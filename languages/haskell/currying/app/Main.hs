double :: Int -> Int
double x = x * 2

fullName :: String -> String -> String
fullName firstName lastName = firstName ++ " " ++ lastName

add :: Int -> Int -> Int
add x y = x + y

addTen :: Int -> Int
addTen = add 10

main :: IO ()
main = do
    putStrLn "Currying in Haskell"
    putStrLn $ "Double of 5: " ++ show (double 5)
    putStrLn $ "Full name: " ++ fullName "John" "Doe"
    putStrLn $ "Add 10 to 5: " ++ show (addTen 5)