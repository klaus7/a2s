
package at.ac.meduniwien.mias.adltoschematron.webservice.client.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="htmlReport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="schematron" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="svrlReport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response", propOrder = {
    "htmlReport",
    "schematron",
    "svrlReport"
})
public class Response {

    protected String htmlReport;
    protected String schematron;
    protected String svrlReport;

    /**
     * Gets the value of the htmlReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHtmlReport() {
        return htmlReport;
    }

    /**
     * Sets the value of the htmlReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHtmlReport(String value) {
        this.htmlReport = value;
    }

    /**
     * Gets the value of the schematron property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchematron() {
        return schematron;
    }

    /**
     * Sets the value of the schematron property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchematron(String value) {
        this.schematron = value;
    }

    /**
     * Gets the value of the svrlReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvrlReport() {
        return svrlReport;
    }

    /**
     * Sets the value of the svrlReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvrlReport(String value) {
        this.svrlReport = value;
    }

}
