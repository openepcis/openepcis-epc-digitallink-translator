[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<div style="text-align: justify">

# Application Identifiers translator

Java program to efficiently and effectively validates and converts EPCIS application identifiers from URN (Universal Resource Identifier) to digital link Web-URI (Uniform Resource
Identifier) and vice versa.

## Introduction

Every digital or physical object such as documents, locations, products, assets, etc. is identified by the universal identifiers. These universal Identifiers can be represented
using the EPC URI (Pure Identity URI) or Digital Link URI. Applications that are using the EPCIS standard must use one of the syntax's to identify the objects. Pure Identity URI is
represented using the URN (Uniform Resource Name) format. An example of an SSCC (Serial Shipping Container Code) represented using the URN format is as follows:

<div align="center">
    <b> urn:epc:id:sscc:234567.18901234567 </b>
</div>
&nbsp;

Digital Link URI (Uniform Resource Identifier) is a newly introduced format with EPCIS 2.0 in which identifiers can also be represented using the Web URI format. The above SSCC
identifier example can
be represented in Digital Link URI format as follows:

<div align="center">
<b> https://id.gs1.org/00/123456789012345675 </b>
</div>
&nbsp;

In certain scenarios' organization likes to make use of both formats for better visibility and accessibility, so they need the option to translate the from URN to DL URI and vice
versa. Hence,
this OpenEPCIS utility has been developed which can convert the standard EPCIS application identifiers from URN to Digital Link WebURI format and vice versa. It can be used
independently, or as
dependencies for other projects or can also be directly accessed via [web application](https://tools.openepcis.io/openepcis-ui/Identifiersconverter).
Currently, it supports the following identifier types:

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

## Project Structure

The project has been segregated to 3 different modules:

* **Validator Module:** This module is used to validate the identifiers based on the GS1 standards.
* **Converter Module:** This module is used to convert the identifiers from URN to DL WebURI and vice versa.
* **QR Code Generator Module:** This module is used to generate the QR code for the identifiers.

## Validation of URN and DL WebURI

The `ValidatorFactory` offers GS1-compliant validation for both URN and Digital Link (DL) WebURI identifiers. It automatically detects the identifier type from the GS1 Application
Identifier (AI) present in the input and throws an exception if validation fails.

To validate the URN call the following method and pass the URN which needs to be validated:

```java
validatorFactory.validateIdentifier(inputURN);
```

To validate the digital link WebURI call the following method and pass the digital link which needs to be validated along with appropriate GCP length:

```java
validatorFactory.validateIdentifier(dlURI,gcpLength);
```

Based on GS1 AI present in provided URN (ex: gtin, sscc, upui, etc.) or DL WebURI (/00/, /01/, /21/, etc.), the utility will automatically detect the identifier type and validate
it against the respective AI. The utility will throw an
exception if the identifier is not valid or unknown identifier is provided.

## Conversion from URN to DL WebURI

To convert the URN to DL WebURI call the following method and pass the URN which needs to be converted:

```java
ConverterUtil.toURI(inputURN);
```

All the conversion mentioned here can be achieved using the static call as mentioned above or by creating a instance of `Converter` class.

```java
final Converter converter=new Converter();
        converter.toURI(inputURN);
```

The utility automatically detects the identifier type and converts it to the corresponding DL URI format. This method returns a String. If the **inputURN** does follow the GS1
identifier syntax
format then it will throw an corresponding Exception.

Following is an example of converting the SGTIN from URN to DL WebURI:

```java
System.out.println(ConverterUtil.toURI("urn:epc:id:sgtin:2345678901.123.9999"));
//https://id.gs1.org/01/12345678901231/21/9999
```

## Conversion from DL WebURI to URN

To convert from DL WebURI to URN call the following method and pass the existing DL WebURI and GCP length:

```java
ConverterUtil.toURN(inputURI,gcpLength);

        or

final Converter converter=new Converter();
        converter.toURN(inputURI,gcpLength);
```

The utility automatically detects the DL WebURI type and converts it to the corresponding URN format. This method returns the Map<String, String> with various information. If the *
*inputURI** does follow the GS1 identifier syntax format then it throws corresponding Exception.

Following is an example of converting the SGTIN from URN to DL URI:

```java
System.out.println(ConverterUtil.toURN("https://id.gs1.org/01/12345678901231/21/9999",10));

// {gtin=12345678901231, asURN=urn:epc:id:sgtin:2345678901.123.9999, asCaptured=https://example.com/path/01/12345678901231/21/9999, serial=9999, canonicalDL=https://id.gs1.org/01/12345678901231/21/9999}
```

## Conversion of class level URN to DL WebURI

To convert the class level URN to WebURI invoke the following method and pass the existing URN. The code will automatically detect the type of identifier and converts it to
appropriate Web URI. If any error found in the identifier then same will be displayed.

```java
ConverterUtil.toURIForClassLevelIdentifier(inputURN);
```

Following is an example of converting GTIN from URN to DL WebURI:

```java
System.out.println(ConverterUtil.toURIForClassLevelIdentifier("urn:epc:idpat:sgtin:3489348.939489.*"));
//https://id.gs1.org/01/93489348394895
```

## Conversion from class level DL WebURI to URN

To convert the Class level DL WebURI to URN invoke the following method and pass the existing DL WebURI with or without GCP Length. If GCP Length has not been provided then the
code will automatically detect the GCP Length based on the GS1 standards.

```java
ConverterUtil.toURNForClassLevelIdentifier(inputURI,gcpLength);
        ConverterUtil.toURNForClassLevelIdentifier(inputURI);
```

Following is an example of converting GTIN from WebURI to URN:

```java
System.out.println(ConverterUtil.toURNForClassLevelIdentifier("https://id.gs1.org/01/88588588585452"));

//{gtin=88588588585452, asURN=urn:epc:idpat:sgtin:8588588.858545.*, asCaptured=https://example.com/path/01/88588588585452, canonicalDL=https://id.gs1.org/01/88588588585452}
```

## QR Code Generation

The utility can be used to generate the QR code for the identifiers. The QR code is generated using the ZXing library. If required various configuration can be provided based on
which respective QR code will be generated such as backgroun color, width, height, color, module shape etc.

Following is an example to generate simple QR code with default config:

```java
final QrCodeGenerator qrCodeGenerator=new QrCodeGenerator();
final QrCodeConfig minimalConfig=QrCodeConfig.builder()
        .data("https://www.openepcis.io")
        .build();
        qrCodeGenerator.generateQRCode(minimalConfig)
```

Following is an example to generate QR code with custom config:

```java
final QrCodeGenerator qrCodeGenerator=new QrCodeGenerator();
final QrCodeConfig config=
        QrCodeConfig.builder()
        .data("https://www.openepcis.io")
        .mimeType("image/gif")
        .qrWidth(500)
        .qrHeight(500)
        .margin(3)
        .backgroundColor(new Color(255,255,255,255))
        .gradientStart(new Color(59,130,246,255))
        .gradientEnd(new Color(59,130,246,255))
        .useRadialGradient(true)
        .drawFinderGradient(true)
        .moduleShape(QrCodeConfig.ModuleShape.CIRCLE)
        .displayLabel("OpenEPCIS")
        .displayLabelFontColor(new Color(59,130,246,255))
        .logoResourceUrl(logoResourceUrl)
        .logoScale(0.2f)
        .build();
        qrCodeGenerator.generateQRCode(config);
```

## Technologies and Libraries used

- Java 17 (For all the Validation and Conversion)
- net.sf.barcode4j (For Check digit calculation)
- org.apache.commons (For String manipulation)

## Quick Links and References

* [Web application](https://tools.openepcis.io/openepcis-ui/Identifiersconverter)
* [Documentation](https://openepcis.io/docs/identifier-converter/)
* [More about OpenEPCIS](https://openepcis.io/)
* [getGCPLengthDemo](https://github.com/RalphTro/getGCPLengthDemo)

</div>