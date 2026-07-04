data Validation e a
  = Failure e
  | Success a
  deriving Show

instance Functor (Validation e) where
  fmap f (Success value) =
    Success (f value)

  fmap _ (Failure err) =
    Failure err

instance Semigroup e => Applicative (Validation e) where

  Success f <*> Success value =
    Success (f value)

  Failure err1 <*> Failure err2 =
    Failure (err1 <> err2)

  Failure err <*> _ =
    Failure err

  _ <*> Failure err =
    Failure err
  pure = Success

data User = User
  { userName :: String
  , userAge :: Int
  , userEmail :: String
  } deriving Show

validateName :: String -> Validation [String] String
validateName name
  | length name >= 2 = Success name
  | otherwise = Failure ["Name is too short"]

validateAge :: Int -> Validation [String] Int
validateAge age
  | age >= 18 = Success age
  | otherwise = Failure ["User must be at least 18"]

validateEmail :: String -> Validation [String] String
validateEmail email
  | '@' `elem` email = Success email
  | otherwise = Failure ["Email is invalid"]

makeUser :: String -> Int -> String -> Validation [String] User
makeUser name age email =
  User
    <$> validateName name
    <*> validateAge age
    <*> validateEmail email

main :: IO ()
main = do

  print (makeUser "Tenebris" 30 "trrbreis@email.com")

  print (makeUser "T" 15 "invalid-email")