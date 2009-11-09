#!/bin/sh

version=0.0.1-SNAPSHOT

rm -rf dist
mkdir -p dist/sms-$version
mkdir dist/sms-$version/bin dist/sms-$version/etc dist/sms-$version/lib
cp bin/* dist/sms-$version/bin
cp etc/* dist/sms-$version/etc
cp lib/* dist/sms-$version/lib
cp target/smsd-*.jar dist/sms-$version/bin
