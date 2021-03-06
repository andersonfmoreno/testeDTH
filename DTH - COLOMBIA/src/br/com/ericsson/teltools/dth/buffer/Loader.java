package br.com.ericsson.teltools.dth.buffer;

import java.util.ArrayList;
import java.util.Arrays;

import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.definition.JavaFormatterBase;
import cmg.stdapp.javaformatter.definition.JavaFormatterInterface;

public class Loader extends JavaFormatterBase implements JavaFormatterInterface
{
    static int   lastSubscriber = 0;
//    static int   maxSubscribers = 90000000;                   // Maximum of 90 million subscribers // TODO: VOLTAR PARA 90000000.
    static int   maxSubscribers = 100;                   
    static long  listMAINID[]   = new long[maxSubscribers];     // The long data type supports numbers larger than 15 digits and uses only 8 bytes
    static ArrayList<byte[]> listBuffer = new ArrayList<byte[]>(maxSubscribers);
    
    public void initialize() 
    {
    	System.out.println();
        for (int i = 0; i < maxSubscribers; i++) listBuffer.add(null);
    }
        
    /* Functions Compress4Bit and Uncompress4Bit
     * (c) Ricardo Funes, 2012
     * 
     * Packages 8 bit data into 4 bits
     * 
     * Data from 2 bytes and up will receive a fixed compression rate of 50%. 
     * Data with an odd number of bytes will take |length/2| + 1 bytes.
     * 
     * Works only for the following UTF-8 encoded numbers and symbols: 01234567890-+.;#
     * 
     * 1111 is reserved for filler
     */
    private static byte[] compress4bit(byte value[]) throws Exception
    {        
        int position = 0;
        
        for (position = 0; position < value.length; position++)
        {
            switch (value[position]) {
                case '0': value[position] = (byte) 0x0; break; // 0000
                case '1': value[position] = (byte) 0x1; break; // 0001
                case '2': value[position] = (byte) 0x2; break; // 0010
                case '3': value[position] = (byte) 0x3; break; // 0011
                case '4': value[position] = (byte) 0x4; break; // 0100
                case '5': value[position] = (byte) 0x5; break; // 0101
                case '6': value[position] = (byte) 0x6; break; // 0110
                case '7': value[position] = (byte) 0x7; break; // 0111
                case '8': value[position] = (byte) 0x8; break; // 1000
                case '9': value[position] = (byte) 0x9; break; // 1001
                case ';': value[position] = (byte) 0xA; break; // 1010
                case '#': value[position] = (byte) 0xB; break; // 1011
                case '-': value[position] = (byte) 0xC; break; // 1100
                case '+': value[position] = (byte) 0xD; break; // 1101
                case '.': value[position] = (byte) 0xE; break; // 1110
                default:  value[position] = (byte) 0xF; break; // Invalid characters will be mapped to Filler
            }
        }
        
        byte inputArray[] = new byte[value.length + 1];
        inputArray[value.length] = (byte) 0xF; // Filler
        System.arraycopy(value, 0, inputArray, 0, value.length);
        
        byte outputArray[] = new byte[(inputArray.length / 2) + 1];
        
        for (position = 0; position < outputArray.length - 1; position++)
                outputArray[position] = (byte) ((inputArray[2*position] << 4) | inputArray[(2*position)+1]);
                
        outputArray[outputArray.length - 1] = (byte) 0xFF; // Extra filler byte to mark the end
        
        return outputArray;
    }
    
    private static byte[] uncompress4bit(byte value[]) throws Exception
    {
        String buffer = "";
        
        for (int position = 0; position < value.length; position++)
        {
            byte firstNibble  = (byte) ((value[position] & 0xF0) >> 4);
            byte secondNibble = (byte) (value[position] & 0x0F);
            
            switch (firstNibble) {
                case 0xA: buffer += ';'; break; // ; (field separator)
                case 0xB: buffer += '#'; break; // # (record separator)
                case 0xC: buffer += '-'; break; // -
                case 0xD: buffer += '+'; break; // +
                case 0xE: buffer += '.'; break; // .
                case 0xF: return buffer.getBytes("UTF-8"); // Filler marks the end
                default: buffer += firstNibble;
            }
            
            switch (secondNibble) {
                case 0xA: buffer += ';'; break; // ; (field separator)
                case 0xB: buffer += '#'; break; // # (record separator)
                case 0xC: buffer += '-'; break; // -
                case 0xD: buffer += '+'; break; // +
                case 0xE: buffer += '.'; break; // .
                case 0xF: return buffer.getBytes("UTF-8"); // Filler marks the end
                default: buffer += secondNibble;
            }
        }
        
        return buffer.getBytes("UTF-8");
    }
    
    private static int toValue(char ch)
    {
        int chaVal = 0;
        
        switch (ch)
        {
            case ' ': chaVal = 0;  break; case 'a': chaVal = 1;  break; case 'b': chaVal = 2;  break; case 'c': chaVal = 3;  break;
            case 'd': chaVal = 4;  break; case 'e': chaVal = 5;  break; case 'f': chaVal = 6;  break; case 'g': chaVal = 7;  break;
            case 'h': chaVal = 8;  break; case 'i': chaVal = 9;  break; case 'j': chaVal = 10; break; case 'k': chaVal = 11; break;
            case 'l': chaVal = 12; break; case 'm': chaVal = 13; break; case 'n': chaVal = 14; break; case 'o': chaVal = 15; break;
            case 'p': chaVal = 16; break; case 'q': chaVal = 17; break; case 'r': chaVal = 18; break; case 's': chaVal = 19; break;
            case 't': chaVal = 20; break; case 'u': chaVal = 21; break; case 'v': chaVal = 22; break; case 'w': chaVal = 23; break;
            case 'x': chaVal = 24; break; case 'y': chaVal = 25; break; case 'z': chaVal = 26; break; case '\'':chaVal = 27; break;
            case '"': chaVal = 28; break; case '!': chaVal = 29; break; case '@': chaVal = 30; break; case '2': chaVal = 31; break;
            case '#': chaVal = 32; break; case '$': chaVal = 33; break; case '%': chaVal = 34; break; case '&': chaVal = 35; break;
            case '*': chaVal = 36; break; case '<': chaVal = 37; break; case '>': chaVal = 38; break; case '-': chaVal = 39; break;
            case '_': chaVal = 40; break; case '+': chaVal = 41; break; case '=': chaVal = 42; break; case '\\':chaVal = 43; break;
            case '(': chaVal = 44; break; case ')': chaVal = 45; break; case '[': chaVal = 46; break; case ']': chaVal = 47; break;
            case ',': chaVal = 48; break; case '?': chaVal = 49; break; case '/': chaVal = 50; break; case ':': chaVal = 51; break;
            case ';': chaVal = 52; break; case '.': chaVal = 53; break; case '|': chaVal = 54; break; case '0': chaVal = 55; break;
            case '1': chaVal = 56; break; case '3': chaVal = 57; break; case '4': chaVal = 58; break; case '5': chaVal = 59; break;
            case '6': chaVal = 60; break; case '7': chaVal = 61; break; case '8': chaVal = 62; break; case '9': chaVal = 63; break;
            default:  chaVal = 0;
        }
        
        return chaVal;
    }
    
    private static char toChar(int val)
    {
        char ch = ' ';
        
        switch (val)
        {
            case 0:  ch = ' '; break; case 1:  ch = 'a'; break; case 2:  ch = 'b'; break; case 3:  ch = 'c';  break;
            case 4:  ch = 'd'; break; case 5:  ch = 'e'; break; case 6:  ch = 'f'; break; case 7:  ch = 'g';  break;
            case 8:  ch = 'h'; break; case 9:  ch = 'i'; break; case 10: ch = 'j'; break; case 11: ch = 'k';  break;
            case 12: ch = 'l'; break; case 13: ch = 'm'; break; case 14: ch = 'n'; break; case 15: ch = 'o';  break;
            case 16: ch = 'p'; break; case 17: ch = 'q'; break; case 18: ch = 'r'; break; case 19: ch = 's';  break;
            case 20: ch = 't'; break; case 21: ch = 'u'; break; case 22: ch = 'v'; break; case 23: ch = 'w';  break;
            case 24: ch = 'x'; break; case 25: ch = 'y'; break; case 26: ch = 'z'; break; case 27: ch = '\''; break;
            case 28: ch = '"'; break; case 29: ch = '!'; break; case 30: ch = '@'; break; case 31: ch = '2';  break;
            case 32: ch = '#'; break; case 33: ch = '$'; break; case 34: ch = '%'; break; case 35: ch = '&';  break;
            case 36: ch = '*'; break; case 37: ch = '<'; break; case 38: ch = '>'; break; case 39: ch = '-';  break;
            case 40: ch = '_'; break; case 41: ch = '+'; break; case 42: ch = '='; break; case 43: ch = '\\'; break;
            case 44: ch = '('; break; case 45: ch = ')'; break; case 46: ch = '['; break; case 47: ch = ']';  break;
            case 48: ch = ','; break; case 49: ch = '?'; break; case 50: ch = '/'; break; case 51: ch = ':';  break;
            case 52: ch = ';'; break; case 53: ch = '.'; break; case 54: ch = '|'; break; case 55: ch = '0';  break;
            case 56: ch = '1'; break; case 57: ch = '3'; break; case 58: ch = '4'; break; case 59: ch = '5';  break;
            case 60: ch = '6'; break; case 61: ch = '7'; break; case 62: ch = '8'; break; case 63: ch = '9';  break;
            default: ch = ' ';
        }
        
        return ch;
    }
    
    /* Functions Compress6Bit and Uncompress6Bit
     * (c) Ricardo Funes, 2012
     * 
     * Packages 8 bit data into 6 bits
     * 
     * Data from 4 bytes and up will receive a compression rate of 20% to 30%. 
     * 
     * Works only for the following UTF-8 encoded numbers and symbols present in the toChar and toValue functions.
     * 
     * 0xFF marks the beginning position
     */
    private static byte[] compress6bit(String txt) throws Exception
    {        
        int length = txt.length();
        
        byte encoded[] = new byte[(int) (3.0f*Math.ceil(length/4.0f))];    
        char str[] = new char[length];
        
        txt.getChars(0,length,str,0);
        
        String temp;
        String strBinary = new String("");
        
        for (int i = 0; i < length; i++)
        {
            temp = Integer.toBinaryString(toValue(str[i]));
            
            while (temp.length() % 6 != 0) temp = "0" + temp;
            
            strBinary = strBinary + temp;
        }
        
        while (strBinary.length() % 8 != 0) strBinary = strBinary + "0";
        
        Integer tempInt = new Integer(0);
        
        for(int i = 0 ; i < strBinary.length(); i += 8)
        {
            tempInt = Integer.valueOf(strBinary.substring(i,i+8),2);
            encoded[i/8] = tempInt.byteValue();
        }
        return encoded;
    }
    
    private static String uncompress6bit(byte encoded[]) throws Exception
    {
        String strTemp   = new String("");
        String strBinary = new String("");
        String strText   = new String("");
        Integer tempInt  = new Integer(0);
        
        int intTemp = 0;
        boolean readyToRead = false;
        
        for (int i = 0; i < encoded.length; i++)
        {         
            // Find the beginning position
            if (((encoded[i] & 0xFF) == 0xFF) && (readyToRead == false)) 
            {
                readyToRead = true;
                i++;
            }
            
            if (readyToRead)
            {    
                if (encoded[i] < 0)
                    intTemp = (int) encoded[i] + 256;
                else
                    intTemp = (int) encoded[i];
            
                strTemp = Integer.toBinaryString(intTemp);
            
                while (strTemp.length() % 8 != 0) strTemp = "0" + strTemp;
            
                strBinary = strBinary + strTemp;
            }
        }
        
        for (int i = 0 ; i < strBinary.length(); i += 6)
        {
            tempInt = Integer.valueOf(strBinary.substring(i,i+6),2);
            strText = strText + toChar(tempInt.intValue()); 
        }
        
        return strText;
    }
    
    private static void update(long mainID, String numberValue, String textValue) throws Exception
    {      
        // Note: The listMAINID array must be previously sorted, otherwise the binary search will fail
        int subscriberPosition = Arrays.binarySearch(listMAINID, 0, lastSubscriber, mainID);
        
        if (subscriberPosition < 0) return;
        
        byte compressedNumber[] = compress4bit(numberValue.getBytes("UTF-8"));
        byte compressedText[]   = compress6bit(textValue);
        
        int sizeNumber = compressedNumber.length;
        int sizeText   = compressedText.length;
        
        byte tempBuffer[] = new byte[sizeNumber + sizeText];
        
        System.arraycopy(compressedNumber, 0, tempBuffer, 0, sizeNumber);
        System.arraycopy(compressedText, 0, tempBuffer, sizeNumber, sizeText);
        
        listBuffer.set(subscriberPosition, tempBuffer);
    }
    
    private static void insert(int subscriberPosition, long mainID, String numberValue, String textValue) throws Exception
    {      
        if (subscriberPosition < maxSubscribers)
        {
            byte compressedNumber[] = compress4bit(numberValue.getBytes("UTF-8"));
            byte compressedText[]   = compress6bit(textValue);
        
            int sizeNumber = compressedNumber.length;
            int sizeText   = compressedText.length;
            
            byte tempBuffer[] = new byte[sizeNumber + sizeText];
        
            System.arraycopy(compressedNumber, 0, tempBuffer, 0, sizeNumber);
            System.arraycopy(compressedText, 0, tempBuffer, sizeNumber, sizeText);
            
            listBuffer.set(subscriberPosition, tempBuffer);
        
            listMAINID[subscriberPosition] = mainID;
            
            if (subscriberPosition > lastSubscriber) 
                lastSubscriber = subscriberPosition; // An approximation of the last subscriber position inserted
        }
    }
    
    private static String query(long mainID) throws Exception
    {     
        // Note: The listMAINID array must be previously sorted, otherwise the binary search will fail
        int subscriberPosition = Arrays.binarySearch(listMAINID, 0, lastSubscriber, mainID);
        
        if (subscriberPosition < 0) return "";
        
        byte tempBuffer[] = listBuffer.get(subscriberPosition);
        
        String numberValue = new String(uncompress4bit(tempBuffer));        
        String textValue   = new String(uncompress6bit(tempBuffer));
        
        return numberValue + ";" + textValue;
    }
    
    private static void clearArrays() throws Exception
    { 
        for (int subscriberPosition = 0; subscriberPosition < maxSubscribers; subscriberPosition++)
            listMAINID[subscriberPosition] = 0;
            
        listBuffer.clear();
        listBuffer.trimToSize();
        listBuffer.ensureCapacity(maxSubscribers);
        
        for (int i = 0; i < maxSubscribers; i++) listBuffer.add(null);
    }
        
    public void format(DataObject dataSource) throws Exception
    { 
        long begin, end;
        
        if (dataSource.parameterExists("queryMAINID")) 
        { 
            begin = System.nanoTime();         
            String resposta = query(dataSource.getValueAsLong("queryMAINID")); 
            end = System.nanoTime();
            dataSource.removeParameter("queryMAINID"); 
            
            dataSource.setParameter("HttpResponse", resposta + "\nResposta obtida em " + (end - begin)/1000/1000.00 + " milisegundos.");  
        } 
        else
        {
	        if (dataSource.parameterExists("POSITION") && dataSource.parameterExists("MAINID") && dataSource.parameterExists("NUMBER_VALUE") && dataSource.parameterExists("TEXT_VALUE")) 
	        {
	            insert(dataSource.getValueAsInt("POSITION"), dataSource.getValueAsLong("MAINID"), dataSource.getValueAsString("NUMBER_VALUE"), dataSource.getValueAsString("TEXT_VALUE"));       
	            dataSource.removeParameter("POSITION");
	            dataSource.removeParameter("MAINID");
	            dataSource.removeParameter("NUMBER_VALUE");
	            dataSource.removeParameter("TEXT_VALUE");
	        }
	        else 
	        {
		        if (dataSource.parameterExists("clearArrays"))
		        {  
		            begin = System.nanoTime();         
		            clearArrays();
		            end = System.nanoTime();
		            dataSource.removeParameter("clearArrays"); 
		            
		            dataSource.setParameter("HttpResponse", "Limpeza efetuada em " + (end - begin)/1000/1000/1000.00 + " segundos."); 
		        }
	        }
        }
    }
}
