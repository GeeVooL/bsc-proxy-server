# ProxyServer

This is a prototype application that handles following use cases:

- pass all unencrypted HTTP connections to final server (proxy)
- cache responses and return them to client on next request to the same resource
- case-sensitive words filtering in HTML respones - find a word and make it red and bold

This is a POC app. It is not suitable for production use.

## Build

This project is Gradle-based. All outputs are in `build/` directory. To build run:
```bash
./gradlew assemble
``` 

To create a distributable archive file run:
```bash
./gradlew distZip
``` 
Archive will be placed in `build/distributions/`.

## Run

Program takes one argument - absolute path to configuration file in the following format:
```
PROXY_PORT=8080
WORDS=Example;SKJ;Bomb;Website
CACHE_DIR="/tmp/cache_dir"
```
`PROXY_PORT` is a port number used by server. 
`WORD` is a semicolon separated list of filtered words.
`CACHE_DIR` is a path to directory where cached files will be saved.

Use scripts placed in generated archive to run the server. For example, on Unix platforms run:
```bash
./ProxyServer /path/to/config/file
```

You can also run the server using Gradle directly:
```bash
./gradlew run --args /path/to/config/file
```
