newtype EventLog = EventLog [String]
  deriving Show

instance Semigroup EventLog where 
    EventLog a <> EventLog b = 
        EventLog (a ++ b)

instance Monoid EventLog where
    mempty = EventLog []

logInfo :: String -> EventLog
logInfo msg = 
    EventLog ["INFO: " ++ msg ]

logError :: String -> EventLog
logError msg = 
    EventLog ["ERROR: " ++ msg ]

main :: IO ()
main = do
    putStrLn "Starting application..."
    let log1 = logInfo "Application started"
    let log2 = logError "An error occurred"
    let log3 = logInfo "Application finished"

    let combinedLog = log1 <> log2 <> log3
    print combinedLog
    
    let emptyLog = mempty :: EventLog
    print emptyLog