package th3doc.babysitter.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface UtilsInterface
{
    /**
     * Capitalize First Letter
     *
     * @param str single argument, no regex support
     * @return formatted string
     */
    default String formatString(String str)
    {
        return (str.substring(0, 1).toUpperCase() + str.toLowerCase().substring(1));
    }
    
    /**
     * Capitalize The First Letter of Each Word and Separate All Phrases by Regex.
     *
     * @param onePhrase true for arg || false for args[]
     * @param phrase to be modified
     * @param regex to perform split()
     * @return formatted strin
     */
    default String formatUpperToFirstCapital(boolean onePhrase, String phrase, String regex)
    {
        if(onePhrase)
        {
            String singleFormat;
            if(regex.equals(""))
            {
                singleFormat = formatString(phrase);
            }
            else
            {
                String[] args = phrase.split(regex);
                StringBuilder strBuild = new StringBuilder();
                for(String arg : args)
                {
                    if(strBuild.length() > 0) { strBuild.append(" "); }
                    strBuild.append(formatString(arg));
                }
                singleFormat =  strBuild.delete(strBuild.length()-1, strBuild.length()-1).toString();
            }
            return singleFormat;
        }
        else
        {
            String[] args = phrase.split(" ");
            StringBuilder strBuild = new StringBuilder();
            for(String arg : args)
            {
                StringBuilder formatted = new StringBuilder();
                if(regex.equals("")) { formatted.append(formatString(arg)); }
                else
                {
                    String[] args1 = arg.split(regex);
                    for(String arg1 : args1)
                    {
                        if(formatted.length() > 0) { formatted.append(" "); }
                        formatted.append(formatString(arg1));
                    }
                }
                if(strBuild.length() > 0) { strBuild.append(" "); }
                strBuild.append(formatted.toString());
            }
            return strBuild.delete(strBuild.length()-1, strBuild.length()-1).toString();
        }
    }
    /**
     *
     *
     * Check Regex For Valid Color Code to Apply Bungee#ChatColor.of()
     *
     * @param color code
     * @return true ? false
     */
    default boolean isValidColorCode(String color)
    {
        // Regex to check valid hexadecimal color code.
        String regex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(color);
        return m.matches();
    }
    
    
    /**
     * Get The Date or Time.
     *
     * @param format calender type
     * @return date || time
     */
    default String getCalender(Utils.Calender format)
    {
        Date now = new Date();
        SimpleDateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String[] split = dateTime.format(now).split(" ");
        switch(format)
        {
            case DATE: return split[0];
            case TIME: return split[1];
            default: return "";
        }
    }
}
