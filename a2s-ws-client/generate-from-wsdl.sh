#!/bin/bash
#
# Use this script to generate the java classes from the wsdl.
# - Start the webserver
# - Run script
# + wsimport has to be on the path
#
wsimport -d src/main/java -keep -p at.ac.meduniwien.mias.adltoschematron.webservice.client.generated http://localhost:8080/a2s-ws/a2s-ws?wsdl
