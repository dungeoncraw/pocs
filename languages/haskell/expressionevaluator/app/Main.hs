data Expr
  = Number Int
  | Add Expr Expr
  | Sub Expr Expr
  | Mul Expr Expr
  | Div Expr Expr
  deriving Show

eval :: Expr -> Int
eval (Number n) = n
eval (Add a b) = eval a + eval b
eval (Sub a b) = eval a - eval b
eval (Mul a b) = eval a * eval b
eval (Div a b) = eval a `div` eval b

main :: IO ()
main = do
  let expr = Add (Number 10) (Mul (Number 2) (Number 3))
  print (eval expr)