package cz.sw.maven.plan;

public enum PlanArtifactType {

    /**
     * In this context, a bundle refers to a standard OSGi bundle as well as a
     * Web application bundles and WAR file. The name of a bundle is the value
     * of the Bundle-SymbolicName header in the META-INF/MANIFEST.MF file of the
     * *.jar or *.war file. The following MANIFEST.MF snippet shows a bundle
     * with name com.springsource.exciting.app:
     * 
     * Bundle-SymbolicName: org.eclispe.virgo.exciting.app If the bundle does
     * not contain a META-INF/MANIFEST.MF file, then the name of the bundle is
     * its filename minus the .jar or .war extension.
     */
    BUNDLE,

    /**
     * The name of a configuration file is its filename minus the .properties
     * extension.
     */
    CONFIGURATION,

    /**
     * The name of a plan is the value of the required name attribute of the
     * <plan> element in the plan’s XML file. In the following XML snippet, the
     * plan name is multi-artifact.plan:
     * 
     * <?xml version="1.0" encoding="UTF-8"?> <plan name="multi-artifact.plan"
     * version="1.0.0" scoped="true" atomic="true"
     * xmlns="http://www.springsource.org/schema/dm-server/plan" ...
     */
    PLAN,

    /**
     * The name of a PAR is the value of the Application-SymbolicName header in
     * the META-INF/MANIFEST.MF file of the *.par file. The following
     * MANIFEST.MF snippet shows a PAR with name com.springsource.my.par:
     * 
     * Application-SymbolicName: org.eclipse.virgo.my.par If the PAR does not
     * contain a META-INF/MANIFEST.MF file, then the name of the PAR is its
     * filename minus the .par extension.
     */
    PAR;

}
