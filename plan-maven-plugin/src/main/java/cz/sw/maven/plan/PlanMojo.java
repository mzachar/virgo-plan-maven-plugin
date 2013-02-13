package cz.sw.maven.plan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * A Maven {@link Mojo} for creating a plan XML based on the first class dependency
 * members. No transitive dependencies are included in the plan XML.
 * 
* @author matej zachar
* 
 */
@Mojo(name="plan", defaultPhase=LifecyclePhase.PACKAGE)
public class PlanMojo extends AbstractMojo {

    private static final String NAMESPACE_URI = "http://www.springsource.org/schema/dm-server/plan";

    @Parameter(defaultValue="${project}", readonly=true, required=true)
    private MavenProject project;

    /**
     * Scoped attribute of generated plan
     */
    @Parameter
    private boolean scoped = false;

    /**
     * Atomic attribute of generated plan
     */
    @Parameter
    private boolean atomic = false;
    
    /**
     * Directory which is searched for *.properties files which will be included in the plan.xml.
     * Default it is <code>${basedir}/conf</code>
     */
    @Parameter(defaultValue="${basedir}/conf")
    private File configurationDir;
    

    @Override
    @SuppressWarnings({ "unchecked" })
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<Artifact> dependencies = project.getDependencyArtifacts();
        
        Document plan = generatePlanXml(dependencies);
        
        OutputStream out = null;
        try {
            File planFile = new File(project.getBuild().getDirectory(), getArtifactName(project.getArtifact(), PlanArtifactType.PLAN));
            if (planFile.exists() == false) {
                planFile.getParentFile().mkdir();
                planFile.createNewFile();
            }
            
            out = new FileOutputStream(planFile);
            
            Serializer serializer = new Serializer(out, "UTF-8");
            serializer.setIndent(4);
            serializer.write(plan);
        } catch (IOException e) {
        	getLog().error("Unable to generate plan", e);
        	throw new MojoFailureException("Unalbe to generate plan", e);
        	
        } finally {
            IOUtil.close(out);
        }
    }

    /**
     * Generates plan xml based on project artifacts
     */
    private Document generatePlanXml(Set<Artifact> dependencies) {
        Element plan = new Element("plan", NAMESPACE_URI);
        plan.addAttribute(new Attribute("xsi:schemaLocation", "http://www.w3.org/2001/XMLSchema-instance",
                "http://www.springsource.org/schema/dm-server/plan http://www.springsource.org/schema/dm-server/plan/springsource-dm-server-plan.xsd"));

        plan.addAttribute(new Attribute("name", getArtifactName(project.getArtifact(), PlanArtifactType.PLAN)));
        plan.addAttribute(new Attribute("version", project.getVersion()));
        plan.addAttribute(new Attribute("scoped", scoped ? "true" : "false"));
        plan.addAttribute(new Attribute("atomic", atomic ? "true" : "false"));

        for (Artifact dep : dependencies) {
            if (Artifact.SCOPE_COMPILE.equals(dep.getScope()) == false)
                continue;

            Element artifact = createPlanXmlArtifact(dep);
            if (artifact != null) {
                plan.appendChild(artifact);
            }
        }
        
        if (configurationDir != null && configurationDir.exists()) {
            try {
                List<String> configurations = FileUtils.getFileNames(configurationDir, "*.properties", "", false);
                if (configurations != null) {
                    for (String configFileName : configurations) {
                        Element artifact = createPlanXmlArtifact(PlanArtifactType.CONFIGURATION, FileUtils.basename(configFileName, "."+FileUtils.extension(configFileName)), null);
                        plan.appendChild(artifact);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to generate configuration plan entries", e);
            }
        }
        
        return new Document(plan);
    }

    private Element createPlanXmlArtifact(Artifact dependenci) {
        PlanArtifactType type = null;
        if ("jar".equalsIgnoreCase(dependenci.getType())) {
            type = PlanArtifactType.BUNDLE;
        }

        if ("par".equalsIgnoreCase(dependenci.getType())) {
            type = PlanArtifactType.PAR;
        }

        if ("plan".equalsIgnoreCase(dependenci.getType())) {
            type = PlanArtifactType.PLAN;
        }
        
        // we can't use this type in plan dependency
        if (type == null)
            return null;

        String version = dependenci.getVersion();
        
        if (dependenci.getVersionRange() != null) {
            version = dependenci.getVersionRange().toString();
        }
        
        return createPlanXmlArtifact(type, getArtifactName(dependenci, type), version);
    }
    
    private Element createPlanXmlArtifact(PlanArtifactType type, String name, String version) {
        if (version == null) {
            version = "0";
        }
        
        Element artifact = new Element("artifact", NAMESPACE_URI);
        
        artifact.addAttribute(new Attribute("type", type.name().toLowerCase()));
        artifact.addAttribute(new Attribute("name", name));
        artifact.addAttribute(new Attribute("version", version));

        return artifact;
    }
    
    private String getArtifactName(Artifact artifact, PlanArtifactType type) {
        return artifact.getGroupId() + "." + artifact.getArtifactId() + (type == PlanArtifactType.PLAN ? ".plan" : "");
    }

}
