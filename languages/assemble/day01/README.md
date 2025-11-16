# Assembly

This is a simple set for using assembly on MAC os, it runs using nasm arm64 architecture


## How to run

install nasm (not required, can use clang)

```bash
brew install nasm
```

Then compile the hello world

```bash
clang -o hello hello.s -Wl,-e,_start -Wl,-no_pie
./hello
```

