package com.tiktok.open.sdk.share.constants

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

internal object Constants {

    const val SUCCESS = 0

    /**
     * share
     */
    const val SHARE_REQUEST = 3

    /**
     * share response
     */
    const val SHARE_RESPONSE = 4

    /**
     * open TT play store failure response
     * */

    const val SHARE_TIKTOK_INSTALL_LANDING_FAIL = -3
}

internal object ShareErrorCodes {
    const val SUCCESS = 0
    const val UNKNOWN = -1

    /**
     * App certificate does not match configurations
     * */

    const val CONFIGURATION_ERR = 10011
    /**
     * Illegal authorization scope.
     * */
    const val SCOPE_ERR = 10004

    /**
     * Params parsing error.
     * */
    const val PARAMETER_PARSE_ERR = 20002

    /**
     * "TikTok has no album permissions."
     * */
    const val GALLERY_PERMISSION_ERR = 20005

    /**
     * TikTok Network error
     * */
    const val NETWORK_ERR = 20006

    /**
     * Photo doesn't meet requirements.
     * */
    const val INVALID_PHOTO_ERR = 20008

    /**
     * Processing photo resources failed.
     * */
    const val PHOTO_RESOURCE_ERR = 20010

    /**
     * Video format is not supported.
     * */
    const val INVALID_VIDEO_TYPE_ERR = 20012

    /**
     * Users store shared content for draft or user accounts are not allowed to post videos
     * */
    const val SAVE_TO_DRAFT_ERR = 20016
}

internal object LocaleMappings {
    val TIKTOK_T_LOCALES = listOf("JP", "KR", "MO", "KH", "ID", "LA", "MY", "MM", "PH", "TW", "TH", "VN", "NP", "LK", "BD", "PK", "SG")
    val TIKTOK_M_LOCALES = listOf(
        "NF", "GM", "NO", "DK", "MG", "AQ", "CH", "OM", "WF", "SX", "QA", "FR", "UG", "CF", "AR", "DJ", "GS",
        "CO", "CG", "NU", "MX", "AL", "DE", "UZ", "EC", "NA", "PF", "BR", "MS", "MQ", "BM", "MF", "IT", "ZA",
        "MC", "BO", "EH", "NP", "GB", "YT", "AS", "AG", "VI", "YE", "MW", "IO", "MA", "IQ", "EE", "KW", "PM",
        "LC", "BZ", "SH", "AW", "ST", "TL", "BT", "GN", "ML", "FO", "RU", "IE", "BH", "BJ", "KN", "SO", "KZ",
        "DZ", "KI", "SJ", "IM", "GA", "RS", "BL", "TK", "BW", "MT", "HT", "JM", "TG", "CX", "XK", "MR", "VG",
        "BG", "GP", "SN", "SR", "BQ", "PT", "US", "TM", "HK", "KM", "NZ", "TF", "LK", "VC", "VA", "BF", "IS",
        "AF", "HR", "FJ", "CC", "GY", "BY", "AE", "PY", "AM", "ME", "LI", "UA", "TT", "GD", "GI", "AT", "MD",
        "GW", "LY", "CY", "LV", "PN", "KE", "BI", "TR", "FM", "CI", "PR", "NG", "CR", "MZ", "GH", "LU", "GR",
        "MP", "RO", "GF", "SE", "HU", "KG", "GT", "PE", "RW", "PS", "BA", "TZ", "VU", "GL", "VE", "CZ", "PA",
        "SI", "AU", "IN", "NI", "BB", "BS", "NE", "TC", "LB", "PK", "SB", "DO", "LT", "UM", "NC", "RE", "BE",
        "AD", "AO", "LS", "AZ", "MV", "TO", "ER", "SA", "EG", "SL", "GE", "MH", "MN", "PW", "GQ", "MK", "IL",
        "CW", "PG", "CK", "CD", "AI", "AX", "GG", "ZM", "FI", "TD", "ES", "ET", "BD", "TJ", "NL", "KY", "BN",
        "CN", "TN", "FK", "SZ", "GU", "ZW", "CV", "TV", "CM", "DM", "LR", "SV", "CL", "PL", "WS", "JO", "SM",
        "NR", "SC", "HN", "UY", "JE", "SK", "MU", "CA"
    )
}

internal object OneLinkConstants {
    const val SCHEMA_HTTPS = "https"
    const val TIKTOK_T_PLAYSTORE_HOST = "snssdk1180.onelink.me"
    const val TIKTOK_M_PLAYSTORE_HOST = "snssdk1233.onelink.me"
    const val TIKTOK_T_PLAYSTORE_ENDPOINT = "/BAuo/4az84vxo"
    const val TIKTOK_M_PLAYSTORE_ENDPOINT = "/bIdt/ikk538qj"
}

internal object ShareErrorCodes {
    const val SUCCESS = 0
    const val UNKNOWN = -1

    /**
     * App certificate does not match configurations
     * */

    const val CONFIGURATION_ERR = 10011
    /**
     * Illegal authorization scope.
     * */
    const val SCOPE_ERR = 10004

    /**
     * Params parsing error.
     * */
    const val PARAMETER_PARSE_ERR = 20002

    /**
     * "TikTok has no album permissions."
     * */
    const val GALLERY_PERMISSION_ERR = 20005

    /**
     * TikTok Network error
     * */
    const val NETWORK_ERR = 20006

    /**
     * Photo doesn't meet requirements.
     * */
    const val INVALID_PHOTO_ERR = 20008

    /**
     * Processing photo resources failed.
     * */
    const val PHOTO_RESOURCE_ERR = 20010

    /**
     * Video format is not supported.
     * */
    const val INVALID_VIDEO_TYPE_ERR = 20012

    /**
     * Users store shared content for draft or user accounts are not allowed to post videos
     * */
    const val SAVE_TO_DRAFT_ERR = 20016
}
