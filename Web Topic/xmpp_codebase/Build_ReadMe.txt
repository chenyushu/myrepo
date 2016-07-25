Precondition
1. install gradle plugin in Eclipse
2. JDK 1.7


Spark
ant build
1.New Project => Java Project from Existing Ant Buildfile
2. then ant build
3. build success, target-> build-> bin
4. modify startup bat

please see if 32 bit or 64bit
java -Dappdir=.. -cp ../lib/*;../lib/jdom.jar;../lib/log4j.jar;../lib/lti-civil.jar;../lib/fmj.jar;../lib/startup.jar;../lib/windows/jdic.jar;../resources;../lib/windows; -Djava.library.path="../lib/windows64" org.jivesoftware.launcher.Startup


Smack

remove this lines
'Implementation-GitRevision': ext.gitCommit,

mark these lines
def getGitCommit() {
	// def dotGit = new File("$projectDir/.git")
	// if (!dotGit.isDirectory()) return 'non-git build'

	// def cmd = 'git describe --always --tags --dirty=+'
	// def proc = cmd.execute()
	// def gitCommit = proc.text.trim()
	// assert !gitCommit.isEmpty()
	// gitCommit
}

set ENV Android_HOME


Openfire

openfire_src\build\eclips 
copy setting folder, classpth, project to  openfire root folder 
then modify classpath and project file to .classpath and .project

build/build.xml ant build

http://www.micmiu.com/opensource/openfire/openfire-src-config/
http://www.micmiu.com/opensource/openfire/openfire-plugins-build/




