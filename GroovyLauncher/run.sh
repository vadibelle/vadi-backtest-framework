#!/bin/bash -x
kill -9 $(ps -aef| grep java | awk '{print $2}')
#/Users/vbelle/git/vadi-backtest-framework/GroovyLauncher
/Users/vbelle/Applications/h2/bin/h2.sh&
pwd=$(pwd)
#export base=`dirname $pwd`
export base=/Users/vbelle/git/vadi-backtest-framework/
echo $base
export cmd="$base/EsperBundle/src/vadi/test/sarb/esper/groovy/BuildNRun.groovy -c $base/GroovyLauncher/temp.properties"
/Users/vbelle/.gvm/groovy/current/bin/groovy -Ddo.compile="true" $cmd
for i in `ls /Users/vbelle/git/vadi-backtest-framework/GroovyLauncher/xa?`
do
/Users/vbelle/.gvm/groovy/current/bin/groovy $cmd "-s -$i" > $i.log 2>&1 &
done
