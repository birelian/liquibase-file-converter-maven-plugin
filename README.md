# Liquibase File Converter Maven Plugin

This maven plugin allows to convert Liquibase change set files between XML, YAML and JSON formats.

### Usage

1. Include the artifact in the plugins section of your POM.xml
2. Execute the goal <code>mvn liquibase-file-converter:convert -DsourceFormat=foo -DtargetFormat=bar</code>
3. Allowed format values are json, xml and yaml.
4. Default source format is XML and default target format is YAML.

### Notes

1. Source files are expected to be in src/main/resources/source directory
2. Generated files can be found in the target/ directory

These settings may be overridden by using the <code>-DsourceDir</code> and <code>-DtargetDir</code> parameters.
For example, we can read from a directory called inputDir and write to a directory called outputDir

<code>mvn liquibase-file-converter:convert -DsourceDir=inputDir -DtargetDir=outputDir</code>