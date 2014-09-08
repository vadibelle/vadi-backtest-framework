package vadi.test.sarb.esper.groovy

import org.apache.tools.ant.types.Path.PathElement;

import groovy.util.AntBuilder

class BuildNRun {
def ant = new AntBuilder()
def home='/Users/vbelle/git/vadi-backtest-framework/'
def base_dir=home+'/EsperBundle/'
def build_dir=base_dir+'build/'
def src_dir=base_dir+'src/'
def lib_dir=base_dir+'../EsperDemo_lib/'
def bundle_dir='vadi/test/sarb/esper/'
def bundle='messages.properties'
//def groovy_dir='/Users/vbelle/.gvm/groovy/current/embeddable'
def ant_dir='/Users/vbelle/Applications/apache-ant-1.9.4/lib/'

def classpath = ant.path {
	fileset(dir: "${lib_dir}"){
	   include(name: "*.jar")
	}
	//fileset(dir:groovy_dir)
	//{
		//include(name:'*.jar')
	//}
	pathelement(path: "${build_dir}")
	pathelement(path: ant_dir+'ant.jar')
	pathelement(path:ant_dir+'ant-launcher.jar')
	
}
def init() {
	
	ant.taskdef name: "groovyc", classname: "org.codehaus.groovy.ant.Groovyc"
	ant.taskdef name: "groovy", classname: "org.codehaus.groovy.ant.Groovy"
	
}
def clean() {
	ant.delete(dir: "${build_dir}")
	ant.delete(file:base_dir+'algo.jar')

 }
def build()
{
	ant.echo("start building ")
	
	ant.mkdir(dir: "${build_dir}")
	//println ant.project.properties.'java.home'
	
	ant.javac(destdir: "${build_dir}", srcdir: "${src_dir}", classpath: "${classpath}",listfiles:"no",includeAntRuntime:'false')
	ant.groovyc(srcdir:"${src_dir}",destdir:"${build_dir}",classpath: "${classpath}",listfiles:"no")
	ant.copy(file:src_dir+bundle_dir+bundle,todir:build_dir+bundle_dir)
	ant.jar(destfile:base_dir+'algo.jar',basedir:build_dir)
}
def start()
{
	//ant.groovy(src:"RunStrategy", classpath:"$classpath}"+"${build_dir}")
	//ant.echo("${classpath}")
	
	//ant.echo("${classpath}")
	//ant.java(classname:"vadi.test.sarb.esper.groovy.RunStrategy",classpath:"${classpath}")
	//ant.groovy(src:"${src_dir}/vadi/test/sarb/esper/groovy/RunStrategy.groovy", classpath:"${classpath}", {
		//arg(line:'-c=/Users/vbelle/git/vadi-backtest-framework/GroovyLauncher/temp.properties -s=/Users/vbelle/git/vadi-backtest-framework/GroovyLauncher/symbol.list')})
	
	ant.java(classname:'org.codehaus.groovy.ant.Groovy',classpath:"${classpath}",{
		arg(line:src_dir+'vadi/test/sarb/esper/groovy/RunStrategy.groovy -c='+home+'/GroovyLauncher/temp.properties -s='+home+'GroovyLauncher/symbol.list')
	})
	
}

static void main(args)
{
	def b = new BuildNRun()
	println "running builld"
	b.init()
	b.clean()
	b.build()
	b.start()
}

}