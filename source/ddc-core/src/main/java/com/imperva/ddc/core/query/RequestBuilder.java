package com.imperva.ddc.core.query;

import com.imperva.ddc.core.commons.Utils;
import com.imperva.ddc.core.exceptions.ParsingException;

public abstract class RequestBuilder {

  //  private static final Logger LOGGER = LoggerFactory.getLogger(SearchCriteriaBuilder.class.getName());

    protected String translateField(FieldInfo field) {
        if (field.getType() == null) {
            return field.getName();
        }
        String result;
        switch (field.getType()) {
            case PHONE_NUMBER:
                result = "telephoneNumber";
                break;
            case HOME_PHONE:
                result = "homePhone";
                break;
            case MOBILE_PHONE:
                result = "mobile";
                break;
            case DEPARTMENT:
                result = "department";
                break;
            case MANAGER_CN:
                result = "manager";
                break;
            case PHOTO:
                result = "thumbnailPhoto";
                break;
            case EMAIL:
                result = "mail";
                break;
            case CITY:
                result = "l";
                break;
            case COUNTRY:
                result = "co";
                break;
            case COUNTRY_INITIALS:
                result = "c";
                break;
            case STATE:
                result = "st";
                break;
            case LAST_MODIFIED:
                result = "whenChanged";
                break;
            case LAST_LOGON:
                result = "lastLogon";
                break;
            case TITLE:
                result = "title";
                break;
            case COMMON_NAME:
                result = "cn";
                break;
            case FIRST_NAME:
                result = "givenName";
                break;
            case LAST_NAME:
                result = "sn";
                break;
            case GUID:
                result = "objectGUID";
                break;
            case USER_PRINCIPAL_NAME:
                result = "userPrincipalName";
                break;
            case STREET:
                result = "street";
                break;
            case STREET_ADDRESS:
                result = "streetAddress";
                break;
            case PHYSICAL_DELIVERY_OFFICE_ADDRESS:
                result = "physicalDeliveryOfficeName";
                break;
            case OBJECT_CLASS:
                result = "objectClass";
                break;
            case OBJECT_CATEGORY:
                result = "objectCategory";
                break;
            case LOGON_NAME:
                result = "sAMAccountName";
                break;
            case DISTINGUISHED_NAME:
                result = "distinguishedName";
                break;
            case ZIP:
                result = "postalCode";
                break;
            case DIVISION:
                result = "division";
                break;
            case CREATION_TIME:
                result = "whenCreated";
                break;
            case GROUP:
                result = "memberOf";
                break;
            case MEMBER:
                result = "member";
                break;
            case USER_ACCOUNT_CONTROL:
                result = "userAccountControl";
                break;
            default:
                result = "*";
                break;
        }
        field.setName(result);
        return result;
    }

    /**
     * The LDAP filter specification assigns special meaning to the several characters:
     * http://social.technet.microsoft.com/wiki/contents/articles/5312.active-directory-characters-to-escape.aspx
     */
    //todo fix signature. Only FieldType is necessary here
    protected static String escapeSpecialChars(String str, Field field) {
        if(str == null)
            throw new ParsingException("Phrases value can't be empty");

        if (field != null && FieldType.GUID == field.getType()){
            return str;
        }

        String escapedStr = new String(str);//* Create new string, prevent altering original value
        if (!Utils.isEmpty(escapedStr)) {
            if (escapedStr.contains("\\")) {
//                LOGGER.trace("DN contains '\\', replacing with \\5C");
                escapedStr = escapedStr.replace("\\", "\\5C");
            }
            if (escapedStr.contains("*")) {
//                LOGGER.trace("DN contains '*', replacing with \\2A");
                escapedStr = escapedStr.replace("*", "\\2A");
            }
            if (escapedStr.contains("(")) {
//                LOGGER.trace("DN contains '(', replacing with \\28");
                escapedStr = escapedStr.replace("(", "\\28");
            }
            if (escapedStr.contains(")")) {
//                LOGGER.trace("DN contains ')', replacing with \\29");
                escapedStr = escapedStr.replace(")", "\\29");
            }
//            LOGGER.trace("Final escaped DN: " + escapedStr);
        }
        return escapedStr;
    }

    //todo consider implement a default implementation here as not every class implementor needs this method
    public abstract void translateFilter();
}
