-- funny how to create a simple mapping from bool to string
boolToString :: Bool -> String
boolToString True = "True"
boolToString False = "False"

sameThreeAround :: [Int] -> Bool
sameThreeAround list =
    let firstThree = take 3 list
        lastThree = reverse (take 3 (reverse list))
    in firstThree == lastThree

main :: IO()
main = putStrLn (boolToString (sameThreeAround [1,2,3,4,5,1,2,3]))