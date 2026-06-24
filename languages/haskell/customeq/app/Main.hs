data User = User
    {
    userId :: Int
    , userName :: String
    }

instance Eq User where
    u1 == u2 = userId u1 == userId u2

instance Show User where
    show user = "User #" ++ show (userId user) ++ " -- " ++ userName user

main :: IO ()
main = do
    let user1 = User 1 "Alice"
    let user2 = User 2 "Bob"
    let user3 = User 1 "Charlie"

    print user1
    print user2
    print (user1 == user2) -- Should be False
    print (user1 == user3) -- Should be True