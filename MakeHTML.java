/*
 * This class belongs to the project MakeHTML
 * Its copyright belongs to Jean-Pierre Hotz, who assures
 * that he wrote this code himself.
 * 
 * You can download the compiled .jar - File of this project on following website:
 * 	    jean-pierre.sytes.net 
 *          > Applikationen 
 *          > MakeHTML 
 * 	        > Download MakeHTML.jar
 * 
 * You can then make a Java Source Code File to a syntax-highlighted HTML-File
 * by calling the program in your command line as follows:
 * java -jar <Directory of the .jar-File>\MakeHTML.jar <Path to Source Code File> 
 * 	    <Path to the HTML-File²> [<print loaded Source Code³>] [<print generated HTML-Code>]
 * 
 * ² - Is being created under all conditions, unless:
 *      a) the input was incorrect (e.g. typo or the input-file was not found)
 * 	    b) the programm is stopped before the file was created
 * 	    c) the directory has restrictions in writing
 * 
 * ³ - additional and actually only for debugging-purposes
 * 	       has to be either:
 * 	          a) "true" -> prints specific code
 * 		 	  b) "false" -> doesn't print specific code
 *     If they are not specified the code will not be printed out
 */

package de.jeanpierrehotz.makehtml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import de.jeanpierrehotz.makehtml.codefragment.CodeFragment;
import de.jeanpierrehotz.makehtml.codefragment.CodeFragmentCollection;

/**
 * With this class you can create an HTML-File with syntax-higlighted Java-Source-Code,
 * whereas the Source-Code comes from an File (doesn't matter whether .java or .txt or .anything)<br>
 * <br>
 * Therefore you have to call the static method {@link MakeHTML#makeFromSourceFile(String, String, boolean, boolean)}<br>
 * <br>
 * As you can probably see by the bad written code, this "Parser" (I put that in quotes since it's that bad)
 * is written by one Person (who is also only learning to code, so don't be a dick), so it should be
 * clear that this Parser definitely has some bugs. Though I don't know any Parser / Code that does this
 * job better than this one (and yes I DID search on google about it), so I'm proud of it.<br>
 * <br>
 * If you see something that could be better coded you can test your idea, or just write a letter,
 * in which you describe how you'd make the code better and rejoice.
 * 
 * @author Jean-Pierre Hotz
 * 
 * @see java.util.ArrayList
 * @see java.io.FileOutputStream
 * @see java.io.IOException
 * 
 * @see de.jeanpierrehotz.makehtml.codefragment.CodeFragment
 * @see de.jeanpierrehotz.makehtml.codefragment.CodeFragmentCollection
 */
public class MakeHTML {

    /**
     * This String contains the very beginning of every HTML-Site, whereas it's declared final<br>
     * This contains only doctype declaration and beginning html- an head- and title-tags
     */
    private static final String BEGIN = "<!doctype html>\n" + "<!-- AUTOMATICALLY GENERATED JAVA-SOURE-CODE PARSING "
            + "\n     WRITTEN BY JEAN-PIERRE HOTZ \n     INSPIRED BY INTELLIJ-->\n" + "<html>\n" + "\t<head>\n" + "\t\t<title>";
    
    /**
     * This String contains all the styling, and all the beginning tags until the Caption.
     * The Caption and the Title are set to the Filename of the Source-Code-File, which is
     * why we have to tear the document beginning apart at those points.
     */
    private static final String BEGIN_AFTERFILENAME = "</title>\n" + "\t\t<style type=\"text/css\">\n" + "\t\t\t.ln{color: rgb(120,120,120); "
            + "font-weigth: normal; font-style: normal;}\n" + "\t\t\t.normCom{color: rgb(128, 128, 128);}\n" + "\t\t\t.norm{color: "
            + "rgb(169, 183, 198);}\n" + "\t\t\t.key{color: rgb(204, 120, 50);}\n" + "\t\t\t.num{color: rgb(104,151,187);}\n" + "\t\t\t"
            + ".apiCom{color: rgb(98,151,85); font-style: normal;}\n" + "\t\t\t.apiComTag{color: rgb(98,151,85); font-weight: bold; "
            + "font-style: italic;}\n" + "\t\t\t.string{color: rgb(106,135,89);}\n" + "\t\t</style>\n" + "\t</head>\n" + "\t<body "
            + "bgcolor=\"#2b2b2b\">\n" + "\t\t<table width=\"100%\" bgcolor=\"#c0c0c0\">\n" + "\t\t\t<tr>\n" + "\t\t\t\t<td>\n" 
            + "\t\t\t\t\t<center>\n" + "\t\t\t\t\t\t<font face=\"Arial, Helvetica\" color=\"#000000\">\n" + "\t\t\t\t\t\t\t";
    
    /**
     * This String contains the beginning of the actual code-section
     */
    private static final String BEGIN_AFTERAFTERFILENAME = "\n" + "\t\t\t\t\t\t</font>\n" + "\t\t\t\t\t</center>\n" + "\t\t\t\t</td>\n" 
            + "\t\t\t</tr>\n" + "\t\t</table>\n" + "\t\t<pre>\n" + "<span class=\"norm\">";
    
    /**
     * This String contains the end of the HTML-File
     */
    private static final String END = "</span>\n" + "\t\t</pre>\n" + "\t</body>\n" + "</html>";

    /**
     * This array contains a listing of all keywords, which should not have 
     * any letter behind or in front of it
     */
    private static String[] keywords = {
        "abstract",     "continue",     "for",          "new",          "switch",
        "assert",       "default",      "goto",         "package",      "synchronized",
        "boolean",      "do",           "if",           "private",      "this",
        "break",        "double",       "implements",   "protected",    "throw",
        "byte",         "else",         "import",       "public",       "throws",
        "case",         "enum",         "instanceof",   "return",       "transient",
        "catch",        "extends",      "int",          "short",        "try",
        "char",         "final",        "interface",    "static",       "void",
        "class",        "finally",      "long",         "strictfp",     "volatile",
        "const",        "float",        "native",       "super",        "while"};

    /**
     * This array contains a listing of all special "keywords", which may have any
     * character in front or behind it
     */
    private static String[] specialKeyWords = {";", ","};

    /**
     * This method executes the commandline-call.<br>
     * This means it looks out for the boolean parameters from the commandline.<br>
     * If they exist the programm prints the code according to those parameters.<br>
     * If not the code will not be shown.<br>
     * <br>
     * The output-file will be created if there are no errors (see document-comment for
     * further information).<br>
     * <br>
     * Here a table of all the arguments in listed order:<br>
     * <table width="100%" style="text-align: center;">
     *      <caption>Arguments from the command-line</caption>
     *      <tr>
     *          <th>additional</th>
     *          <th>content</th>
     *          <th>description</th>
     *      </tr>
     *      <tr>
     *          <td>false</td>
     *          <td>java -jar</td>
     *          <td>You execute a .jar-File with java</td>
     *      </tr>
     *      <tr>
     *          <td>false</td>
     *          <td>&lt;filepath as string&gt;</td>
     *          <td>The filepath of the Java-Source-Code-File</td>
     *      </tr>
     *      <tr>
     *          <td>false</td>
     *          <td>&lt;filepath as string&gt;</td>
     *          <td>The filepath of the HTML-file you want to create<br>
     *          This file is very likely to be created, so be aware of typos</td>
     *      </tr>
     *      <tr>
     *          <td>true</td>
     *          <td>&lt;boolean ("true" / "false")&gt;</td>
     *          <td>Whether you want the loaded code to be printed to the command line*</td>
     *      </tr>
     *      <tr>
     *          <td>true</td>
     *          <td>&lt;boolean ("true" / "false")&gt;</td>
     *          <td>Whether you want the created HTML to be printed to the command line*</td>
     *      </tr>
     * </table>
     * *These options were originally thought to only be for debugging and testing
     * 
     * @param args  The arguments given by the command-line
     */
    public static void main(String[] args){
        try {
            /**We declare three boolean variables:
             *      - showin: Whether the loaded fileinput should be printed to the commandline
             *      - showout: Whether the created fileoutput should be printed to the commandline
             *      - error: Whether an error occured during loading the booleans*/
            boolean showin = false, showout = false, error = false;
//            Then we try to load the booleans
            try{
                showin = Boolean.parseBoolean(args[2]);
                showout = Boolean.parseBoolean(args[3]);
            }catch(Exception e){
//              If an error occurs (-> the boolean arguments are not given)
//              we make the HTML-file from th given source without printing anything to the screen
                MakeHTML.makeFromSourceFile(args[0], args[1], false, false);
//              And show that there was an error
                error = true;
            }
//          If there was no error during loading the booleans we can make the HTML-file with the given
//          parameters
            if(!error){
                MakeHTML.makeFromSourceFile(args[0], args[1], showin, showout);
            }
        } catch (FileNotFoundException e){
//          If the sourcefile was not found we print a message that the file is not existing to the user
            System.out.println("Sorry, your input-file was not found! Program was aborted.");
        }catch(Exception e){
//          If any other mistake occurs we show the user how to correctly call the program
            System.out.println("An error occured. You probably called the program in a wrong "
                    + "way.\nRemember: \njava -jar MakeHTML.jar filePathSource filePathDestination "
                    + "[showinput] [showoutput]\n\nMakeHTML.jar        - is the .jar file which "
                    + "includes this program (the fact that you're reading this shows that you "
                    + "already found this program)\nfilePathSource      - the path (best to be "
                    + "complete) to the .java file (.txt is also allowed; java syntax has to be "
                    + "taken though) which is supposed to be parsed\nfilePathDestination - the path "
                    + "of the file which is supposed to be the final html file. The directory to this "
                    + "will be created, so be sure not to bring in any typos. The ending of the "
                    + "filename will not be checked!\nshowinput           - [additional] is the "
                    + "content of the input-file supposed to be given out in the command line "
                    + "(only useful with smaller code-files)\nshowoutput          - [additional] "
                    + "is the content of the output-file supposed to be given out in the command "
                    + "line (only useful with smaller code-files)");
//          e.printStackTrace();
        }
    }

    /**
     * The files from or to which we load or write the code
     */
    private static File src, dest;

    /**
     * An ArrayList, which temporarily saves the single lines of the sourcecode
     */
    private static ArrayList<String> sourceCode;
    
    /**
     * A string containing a temporary saving of the resulting HTML
     */
    private static String resultHTMl;
    
    /**
     * The String, which conatains the line which is currently being parsed
     */
    private static int currentLine;

    /**
     * The {@link CodeFragmentCollection}, which contains all the {@link CodeFragment}-objects 
     * of one line and the associated code in form of a String-object
     */
    private static CodeFragmentCollection tokens;

    /**
     * This method creates an HMTL-File with syntax-highlighted code and (if given) prints
     * the codes to the commandline.
     * 
     * @param sourceCodePath            The filepath to the Java Source-Code-File
     * @param destinationPath           The filepath to the HTML-File which is about to be created
     * @param putoutInput               Whether we're supposed to print the loaded Source-Code
     * @param putoutOutput              Whether we're supposed to print the created HTML-Code
     * @throws FileNotFoundException    If the Java Source-Code-File doesn't exist at given location
     */
    public static void makeFromSourceFile(String sourceCodePath, String destinationPath, 
            boolean putoutInput, boolean putoutOutput)throws FileNotFoundException{
//      First we create the Source-file and check whether it exists
        src = new File(sourceCodePath);

        if(!src.exists())
            throw new FileNotFoundException("File " + sourceCodePath + "was not found!");

//      Then we create the destination-file
//      by also creating its directories
        dest = new File(destinationPath);

        if(!dest.exists())
            new File(getDirectory(dest)).mkdirs();

//      Initialize the ArrayList, which we need for loading the source-code
        sourceCode = new ArrayList<>();
        
//      Begin the HTML-file by inserting the filename inbetween the splits
        resultHTMl = BEGIN + src.getName() + BEGIN_AFTERFILENAME + src.getName() + BEGIN_AFTERAFTERFILENAME + "\n";

//      Initialize the CodeFragmentCollection, which we will clear again after every line
        tokens = new CodeFragmentCollection();

//      Now we load the Java Source-Code from the src-File
        loadFromSrc();

//      If we are supposed to print the input to the commandline, we'll do this
        if(putoutInput){
            System.out.println("Input from " + sourceCodePath + "resulted in following:\n\n" + printSourceCode());
        }

//      Then we parse all the source code to html
        parseSourceCode();

//      Lastly we save the HTML-code to the dest-File
        saveHTML();

//      And (if wanted) print the hmtl-code to the 
        if(putoutOutput){
            System.out.println("Output to " + sourceCodePath + "resulted in following:\n\n" + resultHTMl);
        }
    }
    
    /**
     * We declare a private constructor, so nobody can instantiate this class (except this class, which
     * is not done).<br>
     * It's not necessary, but in my opinion better, since it would be senseless to create a object
     * out of this class, since i've made everything static
     */
    private MakeHTML(){}
    
    /**
     * This method parses the source code from the ArrayList sourceCode 
     * to HTMl-code into the String resultHTML
     */
    private static void parseSourceCode(){
//      and we need to know whether we are currently in a normal Linecomment ("//"),
//      or in a non-API-Comment (/* ... */) or in a API-Comment (/** */) or in a String ("...")
//      a API-tag (@...; inside an API-comment)
        boolean lineCommented = false, nonAPICommented = false, apiCommented = false, string = false, apitag = false;
//      And also how long the keyword, which comes next is
        int keywordLength = 0;
        
//      We begin in line 1 and go through every line
        for(currentLine = 1; currentLine - 1 < sourceCode.size(); currentLine++){
//          Then we save the current line in following variable
            String currLine = sourceCode.get(currentLine - 1);

//          the variable, which indicates at which index the current CodeFragment
//          has begun. We're always beginning at index 0
            int begin = 0;

//          So we (probably) go through the whole line
            for(int i = 0; i < currLine.length(); i++){
//              If we're inside a apitag and the tag has ended
                if ( Character.isWhitespace( currLine.charAt(i) ) && apitag ){
//                  We add the API-Tag-CodeFragment to the tokens
                    tokens.add(new CodeFragment(CodeFragment.Kind.apicomtag), currLine.substring(begin, i));
//                  refresh the beginning, since the CodeFragment has ended
                    begin = i;
//                  And then indicate that we're no longer inside a API-Tag
                    apitag = false;
                }
//              Otherwise if we're inside a API-Comment and the character
//              at the current position is a '@' -> a API-Tag has begun
                else if ( apiCommented && currLine.charAt(i) == '@' ){
//                  We add the previous code as API-comment
                    tokens.add(new CodeFragment(CodeFragment.Kind.apicom), currLine.substring(begin, i));
//                  refresh the beginning, since the API-Tag has begun
                    begin = i;
//                  and indicate that we now are in a API-Tag
                    apitag = true;
                }
//              Then if we're not inside a comment and the current character is '"', and the previous is not '\'
                else if ( currLine.charAt(i) == '\"' && currLine.charAt(i - 1) != '\\' && !lineCommented && 
                        !nonAPICommented && !apiCommented ){
//                  and if we're already inside a String
                    if(string){
//                      We end the String
                        string = false;
                        tokens.add(new CodeFragment(CodeFragment.Kind.string), currLine.substring(begin, i + 1));
                        begin = i + 1;
                    }else{
//                      otherwise we begin the String and save the previous code as normal
                        string = true;
                        tokens.add(new CodeFragment(CodeFragment.Kind.norm), currLine.substring(begin, i));
                        begin = i;
                    }
                }else if(string||lineCommented){
//                  If we're inside a String and the previous if clauses weren't true we know to not make anything
                    continue;
                }else if(currLine.charAt(i) == '/'){
//                  Now if there was a '/' -> probably a comment
                    if(currLine.length() >= i + 2){
                        if(currLine.charAt(i + 1) == '/'){
//                          If there is a "normal" one-line comment ("//"), which is not inside a multi-line comment
//                          we add the previous code as normal,
//                          and the rest of the line as normal comment
                            if(!apiCommented && !nonAPICommented){
                                tokens.add(new CodeFragment(CodeFragment.Kind.norm), currLine.substring(begin, i));
                                tokens.add(new CodeFragment(CodeFragment.Kind.normcom), currLine.substring(i, currLine.length()));
                                lineCommented = true;
                            }
                        }else if(currLine.charAt(i + 1) == '*'){
//                          Otherwise if it's a other kind of multiline-comment, we still add the previous code
//                          as normal and then indicate what kind of comment we're in right now (with the booleans)
                            tokens.add(new CodeFragment(CodeFragment.Kind.norm), currLine.substring(begin, i));
                            begin = i;
                            if(currLine.length() >= i + 3){
                                if(currLine.charAt(i + 2) == '*') {
                                    apiCommented = true;
                                }else{
                                    nonAPICommented = true;
                                }
                            }else{
                                nonAPICommented = true;
                            }
                        }
                    }
                }else if(currLine.charAt(i) == '*'){
//                  If there was a *, which COULD be the end of an comment
                    if(currLine.length() >= i + 2){
//                      If it IS the end of an comment
                        if(currLine.charAt(i + 1) == '/'){
//                          we just have to decide what kind of comment we're in right now
                            if(apiCommented){
                                tokens.add(new CodeFragment(CodeFragment.Kind.apicom), currLine.substring(begin, i+2));
                            }else if(nonAPICommented){
                                tokens.add(new CodeFragment(CodeFragment.Kind.normcom), currLine.substring(begin, i+2));
                            }
//                          And end every multiline-comment
                            apiCommented = false;
                            nonAPICommented = false;
                        }
                    }
                }else if(!apiCommented&&!nonAPICommented&&!lineCommented&&(keywordLength = withKeyWord(currLine, i))!= -1){
//                  If we're not inside any comment and there is a keyword
//                  we add a normal code piece
                    tokens.add(new CodeFragment(CodeFragment.Kind.norm), currLine.substring(begin, i));
//                  And a keyword code-piece
                    tokens.add(new CodeFragment(CodeFragment.Kind.key), currLine.substring(i, i + keywordLength));
                    
//                  We jump over the keyword
                    i += keywordLength;
//                  And begin at the end of the keyword
                    begin = i;
                }else if(currLine.length() == i+1 && !lineCommented && !nonAPICommented && !apiCommented){
//                  If we're at the end of the line and not inside a comment
//                  we add the rest of the line as normal code
                    tokens.add(new CodeFragment(CodeFragment.Kind.norm), currLine.substring(begin, i + 1));
                }
            }

//          At the end of a line every line-comment, String and API-Tag ends
            lineCommented = false;
            string = false;
            //apitag = false;

//          If we're still inside a multiline-comment, we add the rest of the line to this comment
            if(apiCommented){
                if(apitag){
                    tokens.add(new CodeFragment(CodeFragment.Kind.apicomtag), currLine.substring(begin, currLine.length()));
                    apitag = false;
                }else{
                    tokens.add(new CodeFragment(CodeFragment.Kind.apicom), currLine.substring(begin, currLine.length()));
                }
            }else if(nonAPICommented){
                tokens.add(new CodeFragment(CodeFragment.Kind.normcom), currLine.substring(begin, currLine.length()));
            }

//          Then we create the HTML from this line only
            String thisLine = "";
            
//          by first adding the linenumber
            thisLine += addCurrentLineNumber();

//          and then generating HTML from each token, which is appended to the currently generating line
            for(int i = 0; i < tokens.length; i++){
                thisLine += tokens.getFragment(i).generateHTML(tokens.getCode(i));
            }

//          then we delete all the tokens and add a linebreak after we add the generated line to the resulting HTML
            tokens.clear();
            resultHTMl += thisLine + "\n";
        }

//      As soon as we're through every line, we can tell the user that we're hoping that his
//      code looks halfway pretty and is parsed right
        System.out.println("Hopefully parsed the right way!");
        
//      And add the very end of the HTML-document
        resultHTMl += END;
    }

    /**
     * This method tests whether there is a keyword at the index ind of the String str,
     * whereas special keywords (e.g ';') may have any character in front or behind it,
     * and the "normal" keywords amy only have non-letters in front or behind it.
     * 
     * @param str       The String, which probably represents code
     * @param ind       The index at which the keyword's supposed to begin
     * @return          -1 if there's no keyword; otherwise its length
     */
    private static int withKeyWord(String str, int ind){
//      first we check for the special keywords
        for(int i = 0; i < specialKeyWords.length; i++){
//          if any of them (saved in an array) is at the position of ind
            if(str.substring(ind, ind + specialKeyWords[i].length()).equals(specialKeyWords[i])){
//              We return the length of the matching keyword
                return specialKeyWords[i].length();
            }
        }
//      then we check for all the other keywords
        for(int i = 0; i < keywords.length; i++){
//          Try-Catch, since there will be some StringIndexOutOfBoundsExceptions thrown
            try {
//              if the keyword at position i in the array equals the substring of str at position ind
                if(str.substring(ind, ind + keywords[i].length()).equals(keywords[i])){
//                  and the previous and next character is not a letter
                    if(ind == 0||ind != 0 && isNoChar(str.charAt(ind - 1))){
                        if(ind + keywords[i].length() == str.length()||ind + keywords[i].length() != str.length() && 
                                isNoChar(str.charAt(ind + keywords[i].length()))){
//                          we can return its length
                            return keywords[i].length();
                        }
                    }
                }
//          But we just let it continue if a StringIndexOutOfBoundsException is thrown
            }catch(Exception exc){}
        }
//      if there waas no length of a keyword returned we return -1 since there is
//      no matching keyword
        return -1;
    }

    /**
     * This method tests a character on its attribute of being a letter.<br>
     * Therefore you have to have an understanding of ASCII-Code, which you can
     * read about here: http://www.asciitable.com/
     * @param toTest    the character, which is tested
     * @return          whether given character is NOT a letter
     */
    private static boolean isNoChar(char toTest){
        return toTest < 65 || toTest > 90 && toTest < 97 || toTest > 122;
    }

    /**
     * This method gives you the loaded source-code, so you can print it in one statement
     * @return  the loaded code in one single string
     */
    private static String printSourceCode(){
        String bla = "";
        for(int i = 0; i < sourceCode.size(); i++){
            bla += sourceCode.get(i) + "\n";
        }
        return bla;
    }

    /**
     * This method gives you the directory of an file.<br>
     * We need this method to create only the directory of the dest-File
     * @param file  The file whose directory we need
     * @return      The directory-path of given file
     */
    private static String getDirectory(File file){
//      We need the path of given file
        String toRet = file.getPath();
//      And need to look for the first index from right to be a '\'
        int index = toRet.length() - 1;
        while(toRet.charAt(index) != '\\'){
            index--;
        }
//      Then we get the path until this calculated index
        toRet = toRet.substring(0, index);
//      And then return the path
        return toRet;
    }

    /**
     * This method loads the Source code from the src-File and stores each line
     * in an own String-object inside the sourceCode - ArrayList
     */
    private static void loadFromSrc(){
        try {
//          We create the BufferedReader for the src-File
            BufferedReader in = new BufferedReader(new FileReader(src));
//          We need an buffer-variable for the code (as String) in the current line
            String curr = "";
//          Then we read from the BufferedReader as long as there is not a null-String
//          indicating that the BufferedReader has no more lines to read
            while((curr = in.readLine()) != null){
//              and add those lines to the ArrayList
                sourceCode.add(curr);
            }
//          Then we close the file
            in.close();
            
///////////////////////////////////
//// IMPLEMENTATION OF THE TAB-REPLACEMENT
///////////////////////////////////
            
//          we then go through each line of the code
            for(int i = 0; i < sourceCode.size(); i++){
//              copy the line
                String toModify = sourceCode.get(i);
//              go through each character in this line
                for(int j = 0; j < toModify.length(); j++){
//                  and if at any position there is a tab
                    if(toModify.charAt(j) == '\t'){
//                      we'll copy the string before the tab
                        String temp = toModify.substring(0, j);
//                      add (depending on the position of the tab) upto 4 spaces
                        switch(j % 4){
                            case 0: temp += " ";
                            case 3: temp += " ";
                            case 2: temp += " ";
                            case 1: temp += " ";
                        }
//                      then again add the string after the tab
                        temp += toModify.substring(j + 1, toModify.length());
//                      and overwrite the currently scanned String with the modified
                        toModify = temp;
                    }
                }
//              and as soon as we're done with it we replace the line with the
//              fully modified String
                sourceCode.set(i, toModify);
            }
            
            /* 
             * We could have done the tab-replacement in the while-Loop (line: 558 - 561), in which we
             * load all the code; BUT if we did we would occupy the file, from which we load
             * for longer than we need, which we don't want!
             */
            
//          And we print to the user that we successfully loaded the code
            System.out.println("Successfully loaded from external file!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method saves the generated HTML-code to the dest-File
     */
    private static void saveHTML(){
        try {
//          Therefore we create a BufferedWriter from the dest-File
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest),"UTF-8"));
//          Then we write all the HTML-Code to the File
            out.write(resultHTMl);
//          And last close the File
            out.close();

//          Finally we print to the user that we saved the HTML-code
            System.out.println("Successfully saved to external file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * This variable holds the length (in digits) of the highest linenumber in this code
     */
    private static int max_linenumber_width;
    
    /**
     * This method calculates the number of digits in the highest linenumber
     */
    private static void define_max_linenumber_width(){
//      we begin at 10^0 (=1)
        int i = 0;
//      And while 10^i is less than the highest linenumber (-> first time 10^i is greater
//      than the highest linenumber), we increment i.
//      I could make a mathematical proof for this being correct, but nahh...
        while(Math.pow(10, i) < sourceCode.size()){
            i++;
        }
//      Last but not least we save the length in the private static field max_linenumber_width
        max_linenumber_width = i;
    }

    /**
     * This method generates the current line as prequel to the code
     * @return  the HTML-tag including the current linenumber
     */
    private static String addCurrentLineNumber(){
//      We begin the anchor- and span-tag
        String toRet = "<a name=\"l" + currentLine + "\"><span class=\"ln\">";
        
//      If this method is called the first time
        if(currentLine == 1)
//          we define the length of the highest linenumber
            define_max_linenumber_width();

//      We create a String with the current linenumber
        String zahl = "" + currentLine;
        
//      and add blank spaces until this String is at least 4 digits wide,
//      or until we reach the width of the highest linenumber
        while(zahl.length() < max_linenumber_width || zahl.length() < 4)
            zahl += " ";

//      Then we add the generated linenumber to the HTML
        toRet += zahl + "|";
        
//      Lastly we add the closing-tags
        toRet += "</span></a>";
//      and return the generated HTML
        return toRet;
    }
}