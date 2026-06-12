classify :: Int -> String
classify n
    | n < 0    = "Negative"
    | n == 0   = "Zero"
    | even n   = "Positive Even"
    | otherwise = "Positive Odd"

main :: IO ()
main = do
    print (classify (-10))
    print (classify 0)
    print (classify 7)
    print (classify 12)