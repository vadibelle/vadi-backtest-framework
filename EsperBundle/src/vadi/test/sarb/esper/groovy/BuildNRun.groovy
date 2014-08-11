package vadi.test.sarb.esper.groovy

import groovy.util.AntBuilder

class BuildNRun {
def ant = new AntBuilder()
def base_dir='/Users/vbelle/git/vadi-backtest-framework/EsperBundle/'
def build_dir=base_dir+'build/'
def src_dir=base_dir+'src/'
def lib_dir=base_dir+'../EsperDemo_lib/'
def bundle_dir='vadi/test/sarb/esper/'
def bundle='messages.properties'

def classpath = ant.path {
	fileset(dir: "${lib_dir}"){
	   include(name: "*.jar")
	}
	pathelement(path: "${build_dir}")
}
def init() {
	ant.taskdef name: "groovyc", classname: "org.codehaus.groovy.ant.Groovyc"
	ant.taskdef name: "groovy", classname: "org.codehaus.groovy.ant.Groovy"
	
}
def clean() {
	ant.delete(dir: "${build_dir}")

 }
def build()
{
	ant.echo("start building ")
	
	ant.mkdir(dir: "${build_dir}")
	//println ant.project.properties.'java.home'
	
	ant.javac(destdir: "${build_dir}", srcdir: "${src_dir}", classpath: "${classpath}",listfiles:"no")
	ant.groovyc(srcdir:"${src_dir}",destdir:"${build_dir}",classpath: "${classpath}",listfiles:"no")
	ant.copy(file:src_dir+bundle_dir+bundle,todir:build_dir+bundle_dir)
	ant.jar(destfile:base_dir+'algo.jar',basedir:build_dir)
}
def start()
{
	//ant.groovy(src:"RunStrategy", classpath:"$classpath}"+"${build_dir}")
	//ant.echo("${classpath}")
	 classpath = ant.path {
		fileset(dir: "${lib_dir}"){
		   include(name: "*.jar")
		}
		fileset(dir:base_dir) {
			include(name: "*.jar")
		}
		pathelement(path: "${build_dir}")
	}
	 ant.echo("${classpath}")
	//ant.java(classname:"vadi.test.sarb.esper.groovy.RunStrategy",classpath:"${classpath}")
	ant.groovy(src:"${src_dir}/vadi/test/sarb/esper/groovy/RunStrategy.groovy", classpath:"${classpath}", {
		arg(line:'-c=/Users/vbelle/git/vadi-backtest-framework/GroovyLauncher/temp.properties -s=/Users/vbelle/git/vadi-backtest-framework/GroovyLauncher/symbol.list')})
	
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