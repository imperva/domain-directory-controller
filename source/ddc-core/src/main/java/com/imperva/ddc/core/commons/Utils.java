package com.imperva.ddc.core.commons;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by GABI.BEYO on 7/21/2016.
 */
public class Utils {
    public static boolean isDistinguishName(String userAccountName) {
        String distinguishedNameRegex = "^(?:[A-Za-z][\\w-]*|\\d+(?:\\.\\d+)*)=(?:#(?:[\\dA-Fa-f]{2})+|(?:[^,=\\+<>#;\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*|\"(?:[^\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*\")(?:\\+(?:[A-Za-z][\\w-]*|\\d+(?:\\.\\d+)*)=(?:#(?:[\\dA-Fa-f]{2})+|(?:[^,=\\+<>#;\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*|\"(?:[^\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*\"))*(?:,(?:[A-Za-z][\\w-]*|\\d+(?:\\.\\d+)*)=(?:#(?:[\\dA-Fa-f]{2})+|(?:[^,=\\+<>#;\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*|\"(?:[^\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*\")(?:\\+(?:[A-Za-z][\\w-]*|\\d+(?:\\.\\d+)*)=(?:#(?:[\\dA-Fa-f]{2})+|(?:[^,=\\+<>#;\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*|\"(?:[^\\\\\"]|\\\\[,=\\+<>#;\\\\\"]|\\\\[\\dA-Fa-f]{2})*\"))*)*$";
        boolean isDistinguishedName = userAccountName.matches(distinguishedNameRegex);
        return isDistinguishedName;
    }

    public static boolean isUserNameFullDomain(String userAccountName) {
        return userAccountName.contains("@");
    }

    public static boolean containsCaseInsensitive(List<String> list, String str) {
        Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(list);

        return set.contains(str);
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
