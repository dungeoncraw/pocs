data Shape
    = Circle Double
    | Rectangle Double Double
    | Triangle Double Double
    deriving Show

area :: Shape -> Double
area (Circle r) = pi * r * r
area (Rectangle w h) =  w * h
area (Triangle base height) = base * height / 2

main :: IO ()
main = do
    let circle = Circle 5
    let rectangle = Rectangle 4 6
    let triangle = Triangle 3 4

    print $ area circle
    print $ area rectangle
    print $ area triangle