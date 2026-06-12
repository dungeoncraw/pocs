sumList :: [Int] -> Int
sumList [] = 0
sumList (x:xs) = x + sumList xs

factorial :: Int -> Int
factorial 0 = 1
factorial n = n * factorial (n - 1)

fibonacci :: Int -> Int
fibonacci 0 = 0
fibonacci 1 = 1
fibonacci n = fibonacci (n - 1) + fibonacci (n - 2)

reverseList :: [a] -> [a]
reverseList [] = []
reverseList (x:xs) = reverseList xs ++ [x]

main :: IO ()
main = do
    print (sumList [1, 2, 3, 4, 5])
    print (factorial 5)
    print (fibonacci 10)
    print (reverseList [1, 2, 3, 4, 5])