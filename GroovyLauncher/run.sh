#!/bin/bash -x
#echo "make sure ant_dir in BuildNRun.groovy is set"
pwd=`pwd`
export base=`dirname $pwd`
export cmd="$base/EsperBundle/src/vadi/test/sarb/esper/groovy/BuildNRun.groovy -c $base/GroovyLauncher/temp.properties"
groovy -Ddo.compile="true" $cmd
for i in `ls xa?`
do
groovy $cmd "-s -$i"  > $i.log 2>&1 &
done
