plan-maven-plugin
=================

Maven plugin which generates Virgo plan XML based on the project direct dependencies (transient dependencies are ignored).
 
Example configuration:

Use "plan" project packaging

	<groupId>group</groupId>
	<artifactId>artifact</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>plan</packaging>
	
	<build>
	   <plugins>
	      <plugin>
	         <groupId>cz.sw.maven.plugins</groupId>
	         <artifactId>plan-maven-plugin</artifactId>
	         <version>0.0.1-SNAPSHOT</version>
	         <extensions>true</extensions>
	         <configuration>
	            <atomic>true</atomic>
	            <scoped>false</scoped>
	         </configuration>
	      </plugin>
	   </plugins>
	</build>
	

Declare your direct dependencies

bundle:

   <dependency>
      <groupId>also-support</groupId>
      <artifactId>bundles</artifactId>
      <version>1.2.3</version>
   </dependency>

plan:

   <dependency>
      <groupId>also-support</groupId>
      <artifactId>plans</artifactId>
      <version>[1.0.0, 1.0.1)</version>
      <type>plan</type>
   </dependency>
   
par:

   <dependency>
      <groupId>also-support</groupId>
      <artifactId>pars</artifactId>
      <version>1.0</version>
      <type>par</type>
   </dependency>
   
configuration:

   Any .properties files located in ${basedir}/conf will be included in plan xml
   You can change default location by:

   <configuration>
      <atomic>true</atomic>
      <scoped>false</scoped>
      <configurationDir>${basedir}/some/other/dir</configurationDir>
   </configuration>
