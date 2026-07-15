module Common.State
  ( State(..)
  ) where

newtype State s a = State
  { runState :: s -> (a, s)
  }

instance Functor (State s) where
  fmap f stateValue =
    State $ \initialState ->
      let (value, newState) =
            runState stateValue initialState
      in (f value, newState)

instance Applicative (State s) where
  pure value =
    State $ \state ->
      (value, state)

  stateFunction <*> stateValue =
    State $ \initialState ->
      let (f, state1) =
            runState stateFunction initialState

          (value, state2) =
            runState stateValue state1
      in (f value, state2)

instance Monad (State s) where
  stateValue >>= nextFunction =
    State $ \initialState ->
      let (value, state1) =
            runState stateValue initialState
      in runState (nextFunction value) state1
