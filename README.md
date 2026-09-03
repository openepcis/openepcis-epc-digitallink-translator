<p align="center">
  <img src="https://openepcis.io/img/openepcis-logo.svg" alt="OpenEPCIS" width="30%">
</p>

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Version](https://img.shields.io/github/v/tag/openepcis/openepcis-epc-digitallink-translator?label=version)](https://github.com/openepcis/openepcis-epc-digitallink-translator/tags)
[![Stars](https://img.shields.io/github/stars/openepcis/openepcis-epc-digitallink-translator?style=social)](https://github.com/openepcis/openepcis-epc-digitallink-translator)

<h1 align="center">OpenEPCIS GS1 Digital Link Toolkit</h1>

A Java toolkit that validates GS1 identifiers, translates them between EPC URN and
[GS1 Digital Link](https://www.gs1.org/standards/gs1-digital-link) Web URI. Also turns them into a scannable QR code. It can handle all 16 identifier types used
by [EPCIS](https://www.gs1.org/standards/epcis) 2.0 such as GTIN, SSCC,
SGTIN, SGLN, GRAI, etc.

You can use it as a Java library, as a Quarkus extension, or as a REST service. If you want to try it out, there is a
hosted [web application](https://tools.openepcis.io/ui/format-converter/).

## Why

Every product, pallet, location and document in a supply chain carries an identifier. EPCIS 1.2 represented these identifiers in URN format (URN cannot resolve to anything):

```
urn:epc:id:sscc:234567.18901234567
```

EPCIS 2.0 introduced the Digital Link Web URI, which is the same identifier written as a web address (which can be resolved):

```
https://id.gs1.org/00/123456789012345675
```

Most of the companies can own both. Older systems use URNs, newer systems and printed QR codes carry Web URIs, and both have to agree. Doing that translation manually can go wrong easily, because every identifier type splits its
digits differently and the GS1 Company Prefix (GCP) length decides where the split falls. This module does the split for you, verifies the check digit, and returns back the other format.

## Supported identifiers

| Identifier | Name                                              | Digital Link AI     |
|------------|---------------------------------------------------|---------------------|
| SGTIN      | Serialised Global Trade Item Number               | `/01/` + `/21/`     |
| SSCC       | Serial Shipping Container Code                    | `/00/`              |
| SGLN       | Global Location Number, with or without extension | `/414/` + `/254/`   |
| GRAI       | Global Returnable Asset Identifier                | `/8003/`            |
| GIAI       | Global Individual Asset Identifier                | `/8004/`            |
| GSRN       | Global Service Relation Number, Recipient         | `/8018/`            |
| GSRNP      | Global Service Relation Number, Provider          | `/8017/`            |
| GDTI       | Global Document Type Identifier                   | `/253/`             |
| CPI        | Component / Part Identifier                       | `/8010/` + `/8011/` |
| SGCN       | Serialised Global Coupon Number                   | `/255/`             |
| GINC       | Global Identification Number for Consignment      | `/401/`             |
| GSIN       | Global Shipment Identification Number             | `/402/`             |
| ITIP       | Individual Trade Item Piece                       | `/8006/` + `/21/`   |
| UPUI       | Unit Pack Identifier                              | `/01/` + `/235/`    |
| PGLN       | Global Location Number of Party                   | `/417/`             |
| LGTIN      | GTIN with batch or lot                            | `/01/` + `/10/`     |

Four more GTIN combinations are supported in this module that EPCIS itself does not define: GTIN with consumer product variant
(`/22/`), GTIN with lot, serial and expiry date, GTIN with net weight, and GTIN with weight, amount and best before
date. They are rejected by default. Switch them on with `epcisCompliant(false)`.

## Add it to your project

Versions come from the OpenEPCIS BOM, so you can leave `<version>` out.

```xml
<!-- validate GS1 identifiers -->
<dependency>
    <groupId>io.openepcis</groupId>
    <artifactId>openepcis-digital-link-validator-core</artifactId>
</dependency>

<!-- convert between URN and Digital Link -->
<dependency>
    <groupId>io.openepcis</groupId>
    <artifactId>openepcis-digital-link-converter-core</artifactId>
</dependency>

<!-- generate QR codes -->
<dependency>
    <groupId>io.openepcis</groupId>
    <artifactId>openepcis-qrcode-generator-core</artifactId>
</dependency>

<!-- parse, compress and normalize Digital Link URIs -->
<dependency>
    <groupId>io.openepcis</groupId>
    <artifactId>openepcis-digital-link-utils</artifactId>
</dependency>
```

On Quarkus, add the matching extension instead and inject the beans directly. `quarkus-digital-link-validator` gives
you `ValidatorFactory`, `quarkus-digital-link-converter` gives you `Converter`, and `quarkus-qr-code-generator` gives
you `QrCodeGenerator`.

## Validate an identifier

`ValidatorFactory` works out the identifier type from the GS1 Application Identifier in your input, so you do not have
to provide which type it is. It returns `true` when the identifier is valid and throws exception when it is invalid.

```java
final ValidatorFactory validatorFactory =
    new ValidatorFactory(new GS1DigitalLinkNormalizer(), DefaultGCPLengthProvider.getInstance());

final String urn = "urn:epc:id:sgtin:2345678901.123.9999";
final String digitalLink = "https://id.gs1.org/01/12345678901231/21/9999";

// a URN needs no GCP length
validatorFactory.validateIdentifier(urn, ValidationContext.defaultContext()); // true

// a Digital Link URI needs the GCP length
validatorFactory.validateIdentifier(digitalLink, ValidationContext.builder().gcpLength(10).build()); // true
```

`ValidationContext` carries three settings. The defaults are `epcisCompliant=true`, `validateCheckDigit=true` and `gcpLength=null`.

```java
ValidationContext.builder()
    .epcisCompliant(false)     // also accept GS1 keys that EPCIS does not define
    .validateCheckDigit(false) // skip the check digit test
    .gcpLength(10)             // required for Digital Link URIs, leave out for URNs
    .build();
```

A failed check digit tells you the digit it expected:

```
GTIN has invalid check digit: expected 1 but found 2 in https://id.gs1.org/01/12345678901232/21/1
```

There is also an overload that takes a `java.net.URL`. It normalizes the URI, looks up the GCP length for you, and
returns the normalized URL:

```java
final URL url = URI.create("https://id.gs1.org/01/04012345678901/21/9999").toURL();
validatorFactory.validateIdentifier(url);
// https://id.gs1.org/01/04012345678901/21/9999
```

## Convert between URN and Digital Link

Call the static methods on `ConverterUtil`, or create a `Converter` instance if you prefer. Both do the same thing.

```java
ConverterUtil.toURI("urn:epc:id:sgtin:2345678901.123.9999");
// https://id.gs1.org/01/12345678901231/21/9999
```

Going the other way returns a `Map<String, String>` rather than a single string, because a Digital Link URI holds more
than the URN does:

```java
ConverterUtil.toURN("https://id.gs1.org/01/12345678901231/21/9999", 10);
// {gtin=12345678901231,
//  asURN=urn:epc:id:sgtin:2345678901.123.9999,
//  serial=9999,
//  asCaptured=https://id.gs1.org/01/12345678901231/21/9999,
//  canonicalDL=https://id.gs1.org/01/12345678901231/21/9999}
```

`asCaptured` keeps the URI exactly as you passed it in, `canonicalDL` rewrites it onto `https://id.gs1.org`.

Class level identifiers, the ones that name a product rather than one single item, have their own pair of methods:

```java
ConverterUtil.toURIForClassLevelIdentifier("urn:epc:idpat:sgtin:3489348.939489.*");
// https://id.gs1.org/01/93489348394895

ConverterUtil.toURNForClassLevelIdentifier("https://id.gs1.org/01/04012345678901");
// {gtin=04012345678901, asURN=urn:epc:idpat:sgtin:4012345.067890.*, ...}
```

If you leave the GCP length out, it is looked up from the GS1 prefix list bundled in the jar. That list only covers
prefixes GS1 has actually issued, so for a made up test GTIN you have to pass the length yourself:

```java
ConverterUtil.toURNForClassLevelIdentifier("https://id.gs1.org/01/88588588585452", 7);
// {gtin=88588588585452, asURN=urn:epc:idpat:sgtin:8588588.858545.*, ...}
```

## Digital Link utilities

The `utils` module holds the pieces the validator and converter share. They are useful on their own.

```java
// any domain works, it does not have to be id.gs1.org
final URL url = URI.create("https://example.com/01/12345678901231/21/9999").toURL();

// GS1 check digit
Gs1CheckDigit.of("234567890123");        // '4'
Gs1CheckDigit.isValid("12345678901231"); // true

// read the AIs out of a Digital Link URI
GS1DigitalLinkParser.parse(url); // {01=12345678901231, 21=9999}

// look up the GCP length, synchronously or not
DefaultGCPLengthProvider.getInstance().getGcpLength("https://id.gs1.org/01/12345678901231");

// rewrite short names such as /gtin/ to their AI form, and drop a custom domain
new GS1DigitalLinkNormalizer().normalize(url);
```

`GS1DigitalLinkCompression` shortens a Digital Link so it fits into a smaller QR code, and expands it again:

```java
final GS1DigitalLinkCompression compression = new GS1DigitalLinkCompression();
final String dl = "https://id.gs1.org/01/12345678901231/21/9999";

compression.compressGS1DigitalLink(dl, true, true);
// https://id.gs1.org/DBZ055xf3gk4e

compression.gs1digitalLinkToGS1elementStrings(dl, true);
// (01)12345678901231(21)9999
```

## Generate a QR code

QR codes are drawn with [ZXing](https://github.com/zxing/zxing). The smallest version needs the data and nothing else:

```java
final QrCodeGenerator qrCodeGenerator = new QrCodeGenerator();

final byte[] png = qrCodeGenerator.generateQRCode(
    QrCodeConfig.builder()
        .data("https://id.gs1.org/01/12345678901231/21/9999")
        .build());
```

Everything about the look can be changed: size, margin, colours, gradient, module shape, a logo in the middle, a label
underneath, and the human readable text of the identifier.

```java
final byte[] png = qrCodeGenerator.generateQRCode(
    QrCodeConfig.builder()
        .data("https://id.gs1.org/01/12345678901231/21/9999")
        .mimeType("image/png")
        .qrWidth(512)
        .qrHeight(512)
        .margin(3)
        .backgroundColor(new Color(255, 255, 255, 255))
        .gradientStart(new Color(59, 130, 246, 255))
        .gradientEnd(new Color(29, 78, 216, 255))
        .useRadialGradient(true)
        .drawFinderGradient(true)
        .moduleShape(QrCodeConfig.ModuleShape.CIRCLE)
        .drawShadows(true)
        .displayLabel("OpenEPCIS")
        .displayLabelFontColor(new Color(59, 130, 246, 255))
        .logoResourceUrl(logoResourceUrl)
        .logoScale(0.2f)
        .addHri(true)              // print the identifier under the code
        .compressDigitalLink(true) // shorten the Digital Link before encoding
        .build());
```

Rather than setting every field, you can name a design preset and override only what you want. Two ship with the
`openepcis-qrcode-generator-extensions` module, `GS1` and `OpenEPCIS`:

```java
QrCodeConfig.builder()
    .data("https://id.gs1.org/01/12345678901231/21/9999")
    .designPreset("GS1")
    .addHri(true)
    .build();
```

Your own preset is a class implementing `QrCodeConfigProvider`, registered as a Java service. The generator picks it up
by the name its `supports` method answers to.

## Run the QR code service

The Quarkus demo app serves the generator over HTTP on port 9002, with Swagger UI at
[localhost:9002/q/swagger-ui](http://localhost:9002/q/swagger-ui):

```bash
git clone https://github.com/openepcis/openepcis-epc-digitallink-translator.git
cd openepcis-epc-digitallink-translator
mvn -pl qrcode-generator/quarkus/quarkus-app -am quarkus:dev
```

Ask it for an image:

```bash
curl -X POST 'http://localhost:9002/qr/generate' \
  -H 'Content-Type: application/json' \
  -H 'Accept: image/png' \
  -d '{"data":"https://id.gs1.org/01/12345678901231/21/9999","designPreset":"GS1"}' \
  --output qrcode.png
```

| Endpoint                 | What it does                                               |
|--------------------------|------------------------------------------------------------|
| `POST /qr/generate`      | takes a QR config as JSON, returns the image               |
| `GET /qr/{linkPath}`     | takes a Digital Link path or a full URL, returns the image |
| `GET /qr/design-presets` | lists the presets that are registered                      |
| `OPTIONS /qr`            | reports the media types the two endpoints accept           |

The `GET` form is the handy one, because you can point an `<img>` tag straight at it:

```
http://localhost:9002/qr/01/12345678901231/21/9999?_designPreset=GS1&_hri=true
```

`Accept` picks the image format and defaults to `image/png`. `Design-Preset`, `HRI` and `Compressed` can be sent as
headers or as the query parameters `_designPreset`, `_hri` and `_compressed`. Responses carry an ETag, so a repeated
request with `If-None-Match` comes back as `304`.

## Project layout

| Module             | What it does                                                                   |
|--------------------|--------------------------------------------------------------------------------|
| `validator`        | checks identifiers against the GS1 rules                                       |
| `converter`        | translates URN to Digital Link Web URI and vice versa                          |
| `qrcode-generator` | generates the QR code, plus design presets, a REST API and a runnable demo app |
| `utils`            | Digital Link parsing, compression, normalizing, check digit and GCP length     |

`validator`, `converter` and `qrcode-generator` each ship a `core` module with no framework dependency and a Quarkus extension pair under `quarkus/`.

## Building

Java 25 and Maven. The parent POM is the external OpenEPCIS BOM, so the first build downloads it.

```bash
mvn clean install                                             # everything
mvn -pl validator/core test                                   # one module
mvn -pl qrcode-generator/quarkus/quarkus-app -am quarkus:dev  # run the QR service
```

## Contributing

Bug reports and pull requests are welcome. A wrong identifier conversion, or a wrong GCP length lookup, is worth an issue on its own.

## Related

- [OpenEPCIS Tools](https://tools.openepcis.io/) - open source EPCIS 2.0 tools and services
- [Identifier Converter](https://tools.openepcis.io/ui/format-converter/) - this toolkit as a web application
- [OpenEPCIS](https://openepcis.io/) - Read more about OpenEPCIS
- [Documentation](https://openepcis.io/docs/identifier-converter/) - identifier converter documentation
- [benelog GmbH & Co. KG](https://www.benelog.com/) - Company behind OpenEPCIS
- [GS1 EPCIS Standard](https://www.gs1.org/standards/epcis) - Learn more about EPCIS
- [GS1 Digital Link Standard](https://www.gs1.org/standards/gs1-digital-link) - Learn more about Digital Link
- [getGCPLengthDemo](https://github.com/RalphTro/getGCPLengthDemo) - reference for GCP length lookup

## License

Licensed under the [Apache License 2.0](LICENSE).
