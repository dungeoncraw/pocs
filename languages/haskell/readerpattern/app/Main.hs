data Config = Config
    { appName :: String
    , appVersion :: String
    , environment:: String
    } deriving Show

type App a = Config -> a

welcomeMessage :: App String
welcomeMessage config = 
    "Welcome to " ++ appName config

versionMessage :: App String
versionMessage config =
    "Version: " ++ appVersion config

environmentMessage :: App String
environmentMessage config =
    "Environment: " ++ environment config

fullMessage :: App String
fullMessage config =
    welcomeMessage config
    ++ "\n"
    ++ versionMessage config
    ++ "\n"
    ++ environmentMessage config

main :: IO ()
main = do
    let config = 
            Config
            { appName = "MyApp"
            , appVersion = "1.0.0"
            , environment = "production"
            }
    putStrLn (fullMessage config)