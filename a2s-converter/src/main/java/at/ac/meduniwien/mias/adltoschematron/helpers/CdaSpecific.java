package at.ac.meduniwien.mias.adltoschematron.helpers;

/**
 * Annotates methods and classes that would need some manual changes if we wanted to operate with another reference model than CDA.
 * For adoption to other RM search all occurences of this annotation and replace the CDA specific logic.
 * 
 * @author Klaus Pfeiffer
 */
public @interface CdaSpecific {}
