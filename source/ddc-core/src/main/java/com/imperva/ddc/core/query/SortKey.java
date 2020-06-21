package com.imperva.ddc.core.query;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;

public class SortKey extends FieldInfo {

    /**
     * The matching rule to use to order the result
     */
    private String matchingRuleId = SchemaConstants.NUMERIC_STRING_ORDERING_MATCH_MR_OID;

    /**
     * A flag to set to true to get the result in reverse order. Default to false
     */
    private boolean reverseOrder = false;

	public SortKey() {
		super();
	}

	public SortKey(FieldType fieldType) {
		super(fieldType);
	}

	public SortKey(String fieldName) {
		super(fieldName);
	}

	public String getMatchingRuleId() {
		return matchingRuleId;
	}

	public void setMatchingRuleId(String matchingRuleId) {
		this.matchingRuleId = matchingRuleId;
	}

	public boolean isReverseOrder() {
		return reverseOrder;
	}

	public void setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
	}
}
