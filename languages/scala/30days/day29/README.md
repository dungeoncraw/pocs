# Scala Fat Jar vs Native Image Example

This project demonstrates how to package a Scala application in two different ways:
1. **Fat Jar (Assembly)**: Using `sbt-assembly`.
2. **Native Image**: Using GraalVM and `sbt-native-packager`.

---

## 1. Fat Jar (sbt-assembly)

A fat jar contains your application code along with all its dependencies in a single `.jar` file.

### Setup
1. **Add sbt-assembly plugin** (`project/plugins.sbt`):
   ```scala
   addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")
   ```

2. **Configure build.sbt**:
   ```scala
   assembly / mainClass := Some("com.tetokeguii.day29.main")
   assembly / assemblyJarName := "day29-fatjar.jar"
   ```

### Generating the Fat Jar
```bash
sbt assembly
```
The jar will be at `target/scala-3.7.4/day29-fatjar.jar`.

### Running
```bash
java -jar target/scala-3.7.4/day29-fatjar.jar -o test.txt
```

---

## 2. Native Image (GraalVM)

A native image is a standalone executable compiled specifically for your operating system. It does not require a JVM to run.

### Setup
1. **Add sbt-native-packager plugin** (`project/plugins.sbt`):
   ```scala
   addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0")
   ```

2. **Enable GraalVM in build.sbt**:
   ```scala
   .enablePlugins(GraalVMNativeImagePlugin)
   ```

### Generating the Native Image
Ensure you have GraalVM installed and `JAVA_HOME` pointing to it.
```bash
sbt graalvm-native-image:packageBin
```
The executable will be located at `target/graalvm-native-image/day29`.

### Running
```bash
./target/graalvm-native-image/day29 -o test.txt
```

---

## Comparison: sbt-assembly vs GraalVM Native Image

| Feature | sbt-assembly (Fat Jar) | GraalVM (Native Image) |
| :--- | :--- | :--- |
| **Output** | Single `.jar` file | Standalone executable |
| **Runtime Requirement** | JVM (Java Runtime) | None (Standalone) |
| **Startup Speed** | Standard JVM startup | Extremely Fast (Instant) |
| **Memory Footprint** | Higher (JVM overhead) | Lower (Optimized) |
| **Build Time** | Fast | Very Slow (AOT compilation) |
| **Portability** | High (Anywhere JVM runs) | Low (OS/Architecture specific) |
| **Complexity** | Simple | More complex (Reflection, Dynamic Proxy) |

---
