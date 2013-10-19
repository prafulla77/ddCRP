#!/bin/bash

usage() { echo "Usage: $0 [-n <num>]" 1>&2; exit 1; }

numIter=
while getopts ":n:" o; do

	case "${o}" in
		
		n)  
			numIter=$OPTARG
			;;
		*) 
			usage;
			exit 1;
			;;
	esac
done
if [ -z $numIter ];
then
	usage;
	exit 1;
fi

mkdir tables
mkdir bin
javac -sourcepath src/ -d bin/ -cp "lib/la4j-0.4.0/bin/la4j-0.4.0.jar:lib/jgrapht-0.8.3/jgrapht-jdk1.6.jar:lib/commons-math3-3.2/commons-math3-3.2.jar" src/Driver.java

java -cp "lib/la4j-0.4.0/bin/la4j-0.4.0.jar:lib/jgrapht-0.8.3/jgrapht-jdk1.6.jar:lib/commons-math3-3.2/commons-math3-3.2.jar:bin" Driver -numIter  $numIter
