package org.jenkinsci.plugins.ftpRename;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalEnvironment {
    // search for variables of the form ${VARNAME} or $NoWhiteSpace
    private static final Pattern pattern = Pattern.compile("\\$\\{(.+)\\}|\\$(.+)\\s?");
    
    public String replaceGlobalVars(String str, Map<String, String> envVariables) {
        Matcher m = pattern.matcher(str);
        while (m.find()) {
            // If ${VARNAME} match found, return that group, else return $NoWhiteSpace group
            String globalVariable = (m.group(1) != null) ? m.group(1) : m.group(2);                
            String globalValue = envVariables.get(globalVariable);
            if (globalValue != null) {
                //Replace the full match (group 0) to remove any $ and {}
                str = str.replace(m.group(0), globalValue);
            }
        }
        return str;
    }
}
