
COMPONENT=file-feeder
FULL_COMPONENT=hsn2-${COMPONENT}

all:	${FULL_COMPONENT}-package

clean:	${FULL_COMPONENT}-package-clean

${FULL_COMPONENT}-package:
	mvn clean install -U -Pbundle -Dmaven.test.skip
	mkdir -p build/${COMPONENT}
	tar xzf target/hsn2-filefeeder-1.0.0-SNAPSHOT.tar.gz -C build/${COMPONENT}

${FULL_COMPONENT}-package-clean:
	rm -rf build

build-local:
	mvn clean install -U -Pbundle
