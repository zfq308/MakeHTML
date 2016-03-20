/*
 * This class belongs to the project MakeHTML
 * Its copyright belongs to Jean-Pierre Hotz, who assures
 * that he wrote this code himself.
 * 
 * You can download the compiled .jar - File of this project on following website:
 *      jean-pierre.sytes.net 
 *          > Applikationen 
 *          > MakeHTML 
 *          > Download MakeHTML.jar
 * 
 * You can then make a Java Source Code File to a syntax-highlighted HTML-File
 * by calling the program in your command line as follows:
 * java -jar <Directory of the .jar-File>\MakeHTML.jar <Path to Source Code File> 
 *      <Path to the HTML-File²> [<print loaded Source Code³>] [<print generated HTML-Code>]
 * 
 * ² - Is being created under all conditions, unless:
 *      a) the input was incorrect (e.g. typo or the input-file was not found)
 *      b) the programm is stopped before the file was created
 *      c) the directory has restrictions in writing
 * 
 * ³ - additional and actually only for debugging-purposes
 *         has to be either:
 *            a) "true" -> prints specific code
 *            b) "false" -> doesn't print specific code
 *     If they are not specified the code will not be printed out
 */

package de.jeanpierrehotz.makehtml.codefragment;

import java.util.ArrayList;

/**
 * This class represents a collection of codefragments and their according code<br>
 * You can modify this object with the given methods
 * @author Jean-Pierre Hotz
 */
public class CodeFragmentCollection{
    /**
     * A list of codefragment, indicating what kind of code it is
     * @see de.jeanpierrehotz.makehtml.codefragment.CodeFragment
     * @see java.util.ArrayList
     */
    ArrayList<CodeFragment> fragments;
    
    /**
     * A list of code pieces in form of Strings, which are the code
     * to the CodeFragment-object at the specific index
     */
    ArrayList<String> codes;

    /**
     * The length of the ArrayLists (are always the same).
     * This property is set to public, so you don't need to call a method
     * every iteration you make over this collection
     */
    public int length;

    /**
     * The constructor, which initializes the collection with empty
     * ArrayLists, and a length of 0
     */
    public CodeFragmentCollection(){
        fragments = new ArrayList<>();
        codes = new ArrayList<>();

        length = 0;
    }

    /**
     * This method adds the given CodeFragment and String to the collection
     * @param frag  the Codefragment, which shows what kind of code the code piece is
     * @param code  the String representing the code piece
     */
    public void add(CodeFragment frag, String code){
        fragments.add(frag);
        codes.add(code);

        length++;
    }

    /**
     * This method gives you the code piece at the given index<br>
     * <br>
     * If you want to make HTML-Code from an CodeFragmentCollection-object,
     * you can do this as follows:<br>
     * <pre>
     *  // Your CodeFragmentCollection:
     *  CodeFragmentCollection collection = new CodeFragmentCollection();
     *  
     *  // The String holding the resulting html
     *  String htmlString = "";
     *  
     *  //  [...] adding objects and so
     *  
     *  //We iterate over every object
     *  for(int i = 0; i &lt; collection.length; i++){
     *  
     *  //  we get the fragment at the position i
     *  //  create the html with this object and the code at the position i
     *  //  and append the resulting code to the String
     *  
     *      htmlString += collection.getFragment(i).generateHTML(collection.getCode(i));
     *  }
     * </pre>
     * @param index     the index of the code piece
     * @return          the code piece at the given index
     * @throws IndexOutOfBoundsException    if the Index is &lt; 0 or &gt;= the length
     */
    public String getCode(int index)throws IndexOutOfBoundsException{
        if(index < 0 || index >= fragments.size()){
            throw new IndexOutOfBoundsException("Index " + index + ((index < 0)? " is too small!": " is too high!"));
        }

        return codes.get(index);
    }

    /**
     * This method gives you the CodeFragment-object at the given index.<br>
     * <br>
     * If you want to make HTML-Code from an CodeFragmentCollection-object,
     * you can do this as follows:<br>
     * <pre>
     *  // Your CodeFragmentCollection:
     *  CodeFragmentCollection collection = new CodeFragmentCollection();
     *  
     *  // The String holding the resulting html
     *  String htmlString = "";
     *  
     *  //  [...] adding objects and so
     *  
     *  //We iterate over every object
     *  for(int i = 0; i &lt; collection.length; i++){
     *  
     *  //  we get the fragment at the position i
     *  //  create the html with this object and the code at the position i
     *  //  and append the resulting code to the String
     *  
     *      htmlString += collection.getFragment(i).generateHTML(collection.getCode(i));
     *  }
     * </pre>
     * @param index     the index of the CodeFragment-object
     * @return          the CodeFragment-object at given position
     * @throws IndexOutOfBoundsException    if the Index is &lt; 0 or &gt;= the length
     */
    public CodeFragment getFragment(int index)throws IndexOutOfBoundsException{
        if(index < 0 || index >= fragments.size()){
            throw new IndexOutOfBoundsException("Index " + index + ((index < 0)? " is too small!": " is too high!"));
        }

        return fragments.get(index);
    }

    /**
     * This method deletes all the items in the collection
     */
    public void clear(){
        fragments.clear();
        codes.clear();

        length = 0;
    }
}