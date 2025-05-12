# simple mn-aws-graalvm-poc

Simple micronaut application with cli that uses AWS SSM and compiles into native image using GraalVM.

# Prerequisites

- graalvm 24

# Build

* create python virtual env
```
python3 -m venv .venv
. .venv/bin/activate
```
*
* install `awslocal`
```
pip install awslocal
```

* start local stack
```
docker compose up -d
```

* create aws ssm parameters
```
./scripts/localstack-ssm.sh create
```
* list aws ssm parameters
```
./scripts/localstack-ssm.sh list
```
* install graalvm jvm sdk
```
sdk install java 24.0.1-graal
```

* select graalvm jvm
```
sdk use java 24.0.1-graal
```

* build uberjar
```
./gradlew clean build
```

* build native image
```
./gradlew nativeCompile
```

* run native image
```
export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_REGION=us-east-1

./build/native/nativeCompile/aws-graalvm --help
./build/native/nativeCompile/aws-graalvm ssm /my-app
```
