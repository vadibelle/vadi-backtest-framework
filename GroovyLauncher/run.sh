#!/bin/bash
pwd=`pwd`
export base=`dirname $pwd`
export cmd="$base/EsperBundle/src/vadi/test/sarb/esper/groovy/BuildNRun.groovy -c $base/GroovyLauncher/temp.properties"
groovy -Ddo.compile="true" $cmd
for i in `ls $base/GroovyLauncher/xa?`
do
groovy $cmd "-s -$i"
done
