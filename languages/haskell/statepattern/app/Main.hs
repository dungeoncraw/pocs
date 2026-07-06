newtype State s a = State
  { runState :: s -> (a, s)
  }

instance Functor (State s) where
  fmap f state =
    State $ \initialState ->
      let (value, newState) = runState state initialState
      in (f value, newState)
instance Applicative (State s) where
  pure value =
    State $ \state ->
      (value, state)

  stateFunction <*> stateValue =
    State $ \initialState ->
      let (f, state1) = runState stateFunction initialState
          (value, state2) = runState stateValue state1
      in (f value, state2)

instance Monad (State s) where
  stateValue >>= nextFunction =
    State $ \initialState ->
      let (value, state1) = runState stateValue initialState
      in runState (nextFunction value) state1

increment :: State Int Int
increment =
  State $ \current ->
    let next = current + 1
    in (next, next)

program :: State Int [Int]
program = do
  a <- increment
  b <- increment
  c <- increment
  pure [a, b, c]

main :: IO ()
main = do

  let initialState = 0

  let (values, finalState) =
        runState program initialState

  print values
  print finalState