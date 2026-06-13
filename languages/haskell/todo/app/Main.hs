data Todo = Todo
  { todoId :: Int
  , title :: String
  , completed :: Bool
  } deriving Show

addTodo :: String -> [Todo] -> [Todo]
addTodo title todos =
  let newId = length todos + 1
      todo = Todo newId title False
  in todos ++ [todo]

completeTodo :: Int -> [Todo] -> [Todo]
completeTodo idToComplete = map completeIfMatch
  where
    completeIfMatch todo
      | todoId todo == idToComplete = todo { completed = True }
      | otherwise = todo

main :: IO ()
main = do
  let todos = []
  let todos2 = addTodo "Learn Haskell" todos
  let todos3 = completeTodo 1 todos2
  print todos3