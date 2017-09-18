/**
 * Copyright 2016 Guillermo Bauzá (birelian) - birelianATgmailDOTcom
 * <p>
 * <p>
 * This file is part of Liquibase File Converter Maven Plugin.
 * <p>
 * Liquibase File Converter Maven Plugin is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p>
 * Liquibase File Converter Maven Plugin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with Liquibase File Converter Maven Plugin. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.birelian.liquibase.converter.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import net.birelian.liquibase.converter.exception.ApplicationException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "convert")
public class XmlToYamlConverterMojo extends AbstractMojo {

  private static final String PATH_SEPARATOR = "/";

  @Parameter(property = "sourceFormat", defaultValue = "xml")
  private String sourceFormat;

  @Parameter(property = "targetFormat", defaultValue = "yaml")
  private String targetFormat;

  @Parameter(property = "sourceDir", defaultValue = "src/main/resources/source")
  private String sourceDir;

  @Parameter(property = "targetDir", defaultValue = "target")
  private String targetDir;

  private final ResourceAccessor resourceAccessor = new FileSystemResourceAccessor();
  private final ChangeLogParameters changeLogParameters = new ChangeLogParameters();

  public void execute() throws MojoExecutionException {

    sayHello();

    try {

      // Check that output directory exists. Create if it doesn't.
      checkOutputDirectory();

      // Get files to be converted
      List<String> files = getFilesFromFileSystem();

      // Convert files
      for (String file : files) {
        processFile(file);
      }

      sayGoodBye();

    } catch (ApplicationException e) {
      getLog().error("Application error: " + e.getMessage());
    }
  }

  /**
   * Process a file
   *
   * @param fileName File name
   * @throws ApplicationException If any
   */
  private void processFile(String fileName) throws ApplicationException {

    String changeLogFile = sourceDir + PATH_SEPARATOR + fileName;

    try {

      // Get and parse the change log file
      DatabaseChangeLog changeLog;
      changeLog =
          ChangeLogParserFactory.getInstance().getParser("." + sourceFormat, resourceAccessor)
              .parse(changeLogFile, changeLogParameters, resourceAccessor);

      // Create file and stream
      File file = createFile(fileName);
      FileOutputStream outputStream = new FileOutputStream(file);

      // Write stream
      liquibase.serializer.ChangeLogSerializerFactory.getInstance()
          .getSerializer("." + targetFormat).write(changeLog.getChangeSets(), outputStream);

      // Close the stream
      outputStream.flush();
      outputStream.close();

      getLog().info("File " + file.getName() + " successfully created");

    } catch (Exception e) {
      getLog().error("Unable to parse file " + fileName, e);
      throw new ApplicationException("Unable to parse file " + fileName, e);
    }
  }

  /**
   * Create a file
   *
   * @param originalFileName The original file name
   * @return The file
   * @throws ApplicationException If any
   */
  private File createFile(String originalFileName) throws ApplicationException {

    // Remove the original extension
    String fileName = StringUtils.remove(originalFileName, "." + sourceFormat);

    File file = new File(targetDir + PATH_SEPARATOR + fileName + "." + targetFormat);

    // Create file if it doesn't exist
    if (!file.exists()) {
      try {
        boolean created = file.createNewFile();
        if (!created) {
          throw new ApplicationException("Unable to create file " + file.getName());
        }

      } catch (IOException e) {
        throw new ApplicationException("Unable to create file " + fileName, e);
      }
    }
    return file;
  }

  /**
   * Get change log files from the classpath
   *
   * @return Files from the file system
   * @throws ApplicationException If any
   */
  private List<String> getFileListFromClasspath() throws ApplicationException {

    getLog().info("Trying to get files from the classpath");

    if (XmlToYamlConverterMojo.class.getClassLoader().getResourceAsStream(sourceDir) != null) {

      try {
        return IOUtils.readLines(
            XmlToYamlConverterMojo.class.getClassLoader().getResourceAsStream(sourceDir),
            Charsets.UTF_8);

      } catch (IOException e) {
        throw new ApplicationException("Unable to get files from the classpath", e);
      }
    }

    return null;
  }

  /**
   * Get change log files from the file system
   *
   * @return Files from the file system
   * @throws ApplicationException If any
   */
  private List<String> getFilesFromFileSystem() throws ApplicationException {

    validateSourceDirectory();

    List<String> files = new ArrayList<>();
    getLog().info("Scanning " + sourceDir + " directory");
    try {
      Files.walk(Paths.get(sourceDir)).forEach(filePath -> {
        if (Files.isRegularFile(filePath)) {
          getLog().info("Added " + filePath.getFileName().toString());
          files.add(filePath.getFileName().toString());
        }
      });

      return files;

    } catch (IOException e) {
      getLog().error("Unable to traverse source directory");
      throw new ApplicationException("Unable to traverse source directory");
    }
  }

  /**
   * Validate that the source directory exists.
   *
   * @throws ApplicationException If any
   */
  private void validateSourceDirectory() throws ApplicationException {

    File sourceDirectory = new File(sourceDir);
    if (!sourceDirectory.exists()) {
      getLog().error("Source directory does not exist");
      throw new ApplicationException("Source directory does not exist");
    }
  }

  /**
   * Check that the output directory exists. Create it if necessary.
   *
   * @throws ApplicationException If any
   */
  private void checkOutputDirectory() throws ApplicationException {

    File targetDirectory = new File(targetDir);
    boolean created;

    if (!targetDirectory.exists()) {
      getLog().info("Creating directory: " + targetDir);
      created = targetDirectory.mkdir();
      if (!created) {
        getLog().error("Unable to create output directory");
        throw new ApplicationException("Unable to create output directory");
      }
    } else {
      getLog().info("Target directory already exists");
    }
  }

  private void sayHello() {
    getLog().info("\n\n\n");
    getLog().info("Liquibase File Converter Maven Plugin");
    getLog().info("");
    getLog().info("");
  }

  private void sayGoodBye() {
    getLog().info("");
    getLog().info("All files processed\n\n\n");
  }

}
