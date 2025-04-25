/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter.core;

import io.openepcis.identifiers.converter.DefaultGCPLengthProvider;
import io.openepcis.identifiers.converter.constants.ConstantDigitalLinkTranslatorInfo;
import io.openepcis.identifiers.converter.util.ConverterUtil;
import io.openepcis.identifiers.validator.core.epcis.compliant.GCNValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.GCN_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GCN_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class GCNConverter implements Converter {

    private static final GCNValidator GCN_VALIDATOR = new GCNValidator();
    private boolean isClassLevel;

    public GCNConverter() {
    }

    public GCNConverter(final boolean isClassLevel) {
        this.isClassLevel = isClassLevel;
    }

    // Check if the provided URN is of GCN type
    public boolean supportsDigitalLinkURI(final String urn) {
        return urn.contains(GCN_AI_URN_PREFIX);
    }

    // Check if the provided Digital Link URI is of GCN Type
    public boolean supportsURN(final String dlURI) {
        return dlURI.contains(GCN_AI_URI_PREFIX);
    }

    // Convert the provided URN to respective Digital Link URI of GCN type
    public String convertToDigitalLink(final String urn) throws ValidationException {
        try {
            // Call the Validator class for the GCN to check the URN syntax
            GCN_VALIDATOR.validate(urn);

            // If the URN passed the validation then convert the URN to URI
            final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf("."));
            String sgcn =
                    gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
            sgcn = sgcn.substring(0, 12) + ConverterUtil.checksum(sgcn.substring(0, 12));

            if (!isClassLevel) {
                sgcn = sgcn + urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
            }
            return GS1_IDENTIFIER_DOMAIN + GCN_AI_URI_PREFIX + sgcn;
        } catch (Exception exception) {
            throw new ValidationException(
                    "Exception occurred during the conversion of GCN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
                            + urn
                            + "\n"
                            + exception.getMessage());
        }
    }

    // Convert the provided Digital Link URI to respective URN of GCN Type
    public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
            throws ValidationException {
        try {
            // Call the Validator class for the GCN to check the DLURI syntax
            GCN_VALIDATOR.validate(dlURI, gcpLength);

            // If the URI passed the validation then convert the URI to URN
            String sgcn = dlURI.substring(dlURI.indexOf(GCN_AI_URI_PREFIX) + GCN_AI_URI_PREFIX.length());
            return getEPCMap(dlURI, gcpLength, sgcn);
        } catch (Exception exception) {
            throw new ValidationException(
                    "Exception occurred during the conversion of GCN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
                            + dlURI
                            + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
                            + gcpLength
                            + "\n"
                            + exception.getMessage());
        }
    }

    private Map<String, String> getEPCMap(String dlURI, int gcpLength, String sgcn) {
        final Map<String, String> buildURN = new HashMap<>();
        String asURN;

        try {
            String tempSgcn = sgcn.substring(0, 13);
            tempSgcn =
                    tempSgcn.substring(0, gcpLength)
                            + "."
                            + tempSgcn.substring(gcpLength, tempSgcn.length() - 1);

            if (isClassLevel) {
                asURN = "urn:epc:idpat:sgcn:" + tempSgcn + ".*";
            } else {
                final String serial = sgcn.substring(13);
                if (StringUtils.isNotBlank(serial)) {
                    asURN = "urn:epc:id:sgcn:" + tempSgcn + "." + serial;
                } else {
                    asURN = "urn:epc:id:sgcn:" + tempSgcn;
                }
                buildURN.put(ConstantDigitalLinkTranslatorInfo.SERIAL, serial);
            }

            // If dlURI contains GS1 domain then captured and canonical are same
            if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
                buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
            } else {
                // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
                final String canonicalDL =
                        dlURI.replace(dlURI.substring(0, dlURI.indexOf(GCN_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
                buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
            }

            buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
            buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
            buildURN.put("sgcn", sgcn);
        } catch (Exception exception) {
            throw new ValidationException(
                    "The conversion of the GCN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
                            + dlURI
                            + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
                            + gcpLength
                            + "\n"
                            + exception.getMessage());
        }

        // After generating the URN validate it again and ensure GCP validates
        GCN_VALIDATOR.validate(asURN);

        return buildURN;
    }

    // Convert the provided Digital Link URI to respective URN of GCN Type
    public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
        int gcpLength = 0;

        try {
            final String sgcn = dlURI.substring(dlURI.indexOf(GCN_AI_URI_PREFIX) + GCN_AI_URI_PREFIX.length());
            gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sgcn, GCN_AI_URI_PREFIX);

            // Call the Validator class for the GCN to check the DLURI syntax
            GCN_VALIDATOR.validate(dlURI, gcpLength);

            // If the URI passed the validation then convert the URI to URN
            return getEPCMap(dlURI, gcpLength, sgcn);
        } catch (Exception exception) {
            throw new ValidationException(
                    "Exception occurred during the conversion of GCN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
                            + dlURI
                            + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
                            + gcpLength
                            + "\n"
                            + exception.getMessage());
        }
    }
}
