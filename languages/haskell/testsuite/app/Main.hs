-- this is a generic function (Eq a, Show a), so any type that implements Eq and Show can be used with it
assertEqual :: (Eq a, Show a) => String -> a -> a -> IO ()
assertEqual testName expected actual =
  if expected == actual
    then putStrLn ("PASS: " ++ testName)
    else putStrLn ("FAIL: " ++ testName ++
                   " expected " ++ show expected ++
                   " but got " ++ show actual)

add :: Int -> Int -> Int
add a b = a + b

main :: IO ()
main = do
  assertEqual "add 1 2" 3 (add 1 2)
  assertEqual "add 10 5" 15 (add 10 5)