myMap :: (a -> b) -> [a] -> [b]
myMap _ [] = []
myMap f (x:xs) = f x : myMap f xs

myFilter :: (a -> Bool) -> [a] -> [a]
myFilter _ [] = []
myFilter predicate (x:xs)
    | predicate x = x : myFilter predicate xs
    | otherwise = myFilter predicate xs

myFoldl :: (b -> a -> b) -> b -> [a] -> b
myFoldl _ acc [] = acc
myFoldl f acc (x:xs) = myFoldl f (f acc x) xs

main :: IO ()
main = do
  print (myMap (*2) [1,2,3])
  print (myFilter even [1,2,3,4])
  print (myFoldl (+) 0 [1,2,3,4])