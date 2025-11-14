# Assembly

This is a simple set for using assembly on MAC os, it runs using nasm arm64 architecture


## How to run

install nasm

```bash
brew install nasm
```

Then compile the hello world

```bash
nasm -f macho64 hello.asm -o hello.o
clang -o hello hello.s -Wl,-e,_start -Wl,-no_pie
./hello
```

