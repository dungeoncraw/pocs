# Zio-Quill

This example is for using zio-quill database config
Using SBT Native Packager to generate docker image and build

### Commands

`sbt docker:publishLocal`

This build a docker image that can be queried using `docker images`

`sbt docker:stage`

This shows a generated Dockerfile at target/docker repo

