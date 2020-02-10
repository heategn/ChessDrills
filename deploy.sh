#!/bin/bash

OUTPUT_LINUX=runtime_linux_x64
OUTPUT_WINDOWS=runtime_windows_x64

OUTPUT_LINUX_JVM=$OUTPUT_LINUX/lib/server/libjvm.so

function buildLinux {

	if [ -d $OUTPUT_LINUX ]; then
		sudo rm -r $OUTPUT_LINUX
	fi	

	jlink --launcher chessdrills=chessdrills/org.chessdrills.ChessDrills --compress=2 --no-header-files --no-man-pages --strip-debug --module-path dist:javafx-jmods-11 --add-modules chessdrills --output "$OUTPUT_LINUX"
	echo "Stripping unneeded symbols from libjvm.so"
	if [ -f $OUTPUT_LINUX_JVM ]; then
		strip -p --strip-unneeded "$OUTPUT_LINUX_JVM"
	else
		echo "ERROR: $OUTPUT_LINUX_JVM not found."
	fi

	return

}

function buildWindows {

	if [ -d $OUTPUT_WINDOWS ]; then
		sudo rm -r $OUTPUT_WINDOWS
	fi	

	jlink --launcher chessdrills=chessdrills/org.chessdrills.ChessDrills --compress=2 --no-header-files --no-man-pages --strip-debug --module-path dist:javafx-jmods-11-windows:windows_jdk_11/jdk-11.0.4+11/jmods --add-modules chessdrills --output runtime_windows_x64
	return

}

case $1 in 
	"windows")
		buildWindows
		;;
	"linux")
		buildLinux
		;;
		*)
		echo "$1 platform not recognized."
		;;
esac 
