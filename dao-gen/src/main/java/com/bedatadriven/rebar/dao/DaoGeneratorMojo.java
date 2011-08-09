package com.bedatadriven.rebar.dao;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates DAO java sources
 *
 * @goal generate
 * @phase generate-sources
 */
public class DaoGeneratorMojo
  extends AbstractMojo {

  /**
   * The maven project descriptor
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * Directory containing *.dao.xml files
   *
   * @parameter default-value="${project.basedir}/src/main/xml"
   * @required
   */
  private File sourceDirectory;


  /**
   * Folder where generated-source will be created (automatically added to compile classpath).
   *
   * @parameter default-value="${project.build.directory}/generated-sources/rebar-dao"
   * @required
   */
  private File generateDirectory;


  public File getGenerateDirectory() {
    if (!generateDirectory.exists()) {
      getLog().debug("Creating target directory " + generateDirectory.getAbsolutePath());
      generateDirectory.mkdirs();
    }
    return generateDirectory;
  }

  public void execute()
    throws MojoExecutionException {

    DaoGenerator generator = new DaoGenerator();
    generator.setValidationHandler(new MavenValidationHandler(getLog()));
    for (File daoSource : findDaoSources()) {
      generator.generate(daoSource, getGenerateDirectory());
    }
  }

  private List<File> findDaoSources() {

    getLog().debug("Scanning '" + sourceDirectory + "' for *.dao.xml files");

    List<File> sourceFiles = new ArrayList<File>();
    if (sourceDirectory.isDirectory()) {
      for (File file : sourceDirectory.listFiles()) {
        if (file.getName().endsWith(".dao.xml")) {
          sourceFiles.add(file);
          getLog().debug("Adding '" + file.getAbsolutePath() + "'");
        }
      }
    }

    if (sourceFiles.isEmpty()) {
      getLog().warn("No DAO source files found in '" + sourceDirectory + "'");
    }

    return sourceFiles;
  }


}
