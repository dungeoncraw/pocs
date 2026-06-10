isVowel :: Char -> Bool
isVowel c = c `elem` "aeiouAEIOU"

countVowels :: String -> Int
countVowels text = length (filter isVowel text)

analyze :: String -> (Int, Int, Int)
analyze text = 
    (length text, length (words text), countVowels text)

main :: IO ()
main = do
    putStrLn "Analyze string: (total length, qtdy words, qtdy vowels)"
    print(analyze "Hello from Haskell")