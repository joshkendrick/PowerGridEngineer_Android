all: release

clean:
	./gradlew clean

release:
	./gradlew clean assembleRelease

beta:
	./gradlew clean assembleBeta

debug:
	./gradlew clean assembleDebug

.PHONY: all clean release beta debug
