package dk.goombah.backend.util;

public class NumberFunctions {

    public static Integer tryParse(String number)
    {
        try {
            int integer = Integer.parseInt(number);
            return integer;
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }

    public static int tryParse(String number, int defaultValue)
    {
        try {
            if(number == null)
                return defaultValue;
            int integer = Integer.parseInt(number);
            return integer;
        }
        catch(NumberFormatException e)
        {
            return defaultValue;
        }
    }
}
