[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Application Identifiers translator

Every digital or physical object such as documents, locations, products, assets, etc. is identified by the universal identifiers. These universal Identifiers can be represented
using the EPC URI (Pure Identity URI) or Digital Link URI. Applications that are using the EPCIS standard must use one of the syntaxes to identify the objects. Pure Identity URI is
represented using the URN (Uniform Resource Name) format. An example of an SSCC (Serial Shipping Container Code) represented using the URN format is as follows: **urn:epc:id:sscc:
234567.18901234567**. Digital Link URI (Uniform Resource Identifier) is a newly introduced format with EPCIS 2.0 in which identifiers can be represented using the Web URI format.
The above SSCC identifier example can be represented in Digital Link URI format as follows: **https://id.gs1.org/00/123456789012345675**.

In certain scenarios organization likes to make use of both formats for better visibility and accessibility so they need the option to translate the from URN to DL URI and vice
versa. Hence, this OpenEPCIS utility has been developed which can convert the standard application identifiers from URN to Digital Link URI format and vice versa. It can be used
independently or in conjunction with the OpenEPCIS. It supports the following identifier types:

- SGTIN: Serialised Global Trade Item Number
- SSCC: Serial Shipping Container Code
- SGLN: Global Location Number with or without extension
- GRAI: Global Returnable Asset Identifier
- GIAI: Global Individual Asset Identifier
- GSRN: Global Service Relation Number - Recipient
- GSRNP: Global Service Relation Number – Provider
- GDTI: Global Document Type Identifier
- CPI: Component / Part Identifier
- SGCN: Serialised Global Coupon Number
- GINC: Global Identification Number for Consignment
- GSIN: Global Shipment Identification Number
- ITIP: Individual Trade Item Piece
- UPUI: Unit Pack Identifier
- PGLN: Global Location Number of Party
- LGTIN: GTIN + Batch/Lot

#### Conversion from URN to DL URI:

To convert the URN to DL URI call the following method and pass the URN which needs to be converted:

```sh
ConverterUtil.toURI(inputURN);
```

The utility automatically detects the identifier type and converts it to the corresponding DL URI format. This method returns a String. If the **inputURN** does follow the GS1
identifier syntax format then it will throw an corresponding Exception.

Following is an example of converting the SGTIN from URN to DL URI:

```sh
System.out.println(ConverterUtil.toURI("urn:epc:id:sgtin:2345678901.123.9999"));
//https://id.gs1.org/01/12345678901231/21/9999
```

#### Conversion from DL URI to URN:

To convert from DL URI to URN call the following method and pass the existing DL URI and GCP length:

```sh
ConverterUtil.toURN(inputURI,gcpLength);
```

The utility automatically detects the DL URI type and converts it to the corresponding URN format. This method returns the Map<String, String> with various information. If the **
inputURI** does follow the GS1 identifier syntax format then it throws corresponding Exception.

Following is an example of converting the SGTIN from URN to DL URI:

```sh
System.out.println(ConverterUtil.toURN("https://id.gs1.org/01/12345678901231/21/9999", 10));

// {gtin=12345678901231, asURN=urn:epc:id:sgtin:2345678901.123.9999, asCaptured=https://example.com/path/01/12345678901231/21/9999, serial=9999, canonicalDL=https://id.gs1.org/01/12345678901231/21/9999}
```

#### Conversion of class level URN to Web URI:

To convert the class level URN to Web URI invoke the following method and pass the the existing URN. The code will automatically detect the type of identifier and converts it to
appropriate Web URI. If any error found in the identifier then same will be displayed.

```
ConverterUtil.toURIForClassLevelIdentifier(inputURN);
```

Following is an example of converting GTIN from URN to Web URI:

```
System.out.println(ConverterUtil.toURIForClassLevelIdentifier("urn:epc:idpat:sgtin:3489348.939489.*"));
//https://id.gs1.org/01/93489348394895
```

#### Conversion from class level Web URI to URN:

To convert the Class level DL URI to URN invoke the following method and pass the existing DL URI with or without GCP Length. If GCP Length has not been provided then the code will
automatically detect the GCP Length based on the GS1 standards.

```sh
ConverterUtil.toURNForClassLevelIdentifier(inputURI,gcpLength);
ConverterUtil.toURNForClassLevelIdentifier(inputURI);
```

Following is an example of converting GTIN from Web URI to URN:

```
System.out.println(ConverterUtil.toURNForClassLevelIdentifier("https://id.gs1.org/01/88588588585452"));

//{gtin=88588588585452, asURN=urn:epc:idpat:sgtin:8588588.858545.*, asCaptured=https://example.com/path/01/88588588585452, canonicalDL=https://id.gs1.org/01/88588588585452}
```

#### Technologies and Libraries used:

- Java 17 (For all the Validation and Conversion)
- net.sf.barcode4j (For Check digit calculation)
- org.apache.commons (For String manipulation)
