idk :: (Ord a, Num a) => a -> a
idk x =
    case (x < 10) of
        True -> (negate x)
        False -> (x + 10)

main :: IO()
main =  putStrLn (show (idk 3))