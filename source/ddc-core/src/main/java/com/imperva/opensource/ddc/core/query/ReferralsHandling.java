package com.imperva.opensource.ddc.core.query;

/**
 * Created by gabi.beyo on 28/06/2015.
 * Indicates the Directory server how to handle referrals suggestions
 */
public enum ReferralsHandling {
    /**
     * Automatically search in suggested referrals
     */
    FOLLOW,

    /**
     * Don't search in suggested referrals
     */
    IGNORE,

    /**
     * Throws an exception when a referral is encountered
     */
    THROW
}
