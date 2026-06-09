calculate :: Double -> String -> Double -> Either String Double
calculate a "+" b = Right (a + b)
calculate a "-" b = Right (a - b)
calculate a "*" b = Right (a * b)
calculate _ "/" 0 = Left "Cannot divide by zero"
calculate a "/" b = Right (a / b)
calculate _ op _  = Left ("Invalid operator: " ++ op)

main :: IO ()
main = do
  print (calculate 10 "+" 5)
  print (calculate 10 "/" 0)