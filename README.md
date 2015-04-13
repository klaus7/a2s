# A2S - ADL to Schematron

This tool creates a Schematron schema from openEHR archetypes expressed in the archetype definition language. (ADL)

Currently it only works with HL7 v3 CDA R2 compliant archetypes and documents, though support for the ISO 13606 reference model is planned.


## Try it out online

Go to http://a2s-kpe.rhcloud.com/ to try it out online.


## Prerequisites

* Maven
* You will need to checkout and build the [openEHR Java Libs](https://github.com/openEHR/java-libs)


## Start

You can start the generator with `Start.java` class found under "src/test/java/at/ac/meduniwien/mias/adltoschematron/Start.java".

The generated assertions are in german, but it is possible to customize the messages of the assertions in the `language.properties` file.

Multi-language support with multiple files like `language.en.properties` is planned.


## Build

```
cd a2s-converter
mvn install
cd ../a2s-ws-client
mvn package
```

You should now have a deployable `client.war` under `a2s-ws-client/target`.

This is enough for creating Schematron files. If you want to validate files you also have to deploy the `iso_schematron_skeleton_for_saxon.xsl` to your server.
