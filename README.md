# Bookshop POS

## Build and run

This project uses Maven. The Maven build file should be named `pom.xml` in the project root.

### 1. Rename `pom.xl` to `pom.xml`

```bash
cd /home/anneo/bookshop-sf-testing/bookshop
mv pom.xl pom.xml
```

### 2. Compile

```bash
mvn compile
```

### 3. Run tests

```bash
mvn test
```

### 4. Package

```bash
mvn package
```

## Notes

- There is currently no `main()` class, so there is no runnable application entry point yet.
- After adding a `Main` class, you can run the application with:

```bash
java -cp target/bookshop-pos-1.0-SNAPSHOT.jar bookshop.Main
```

or by configuring Maven to build an executable jar.
