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

/**
 * This class represents a CodeFragment and indicates what kind of code it is
 * @author Jean-Pierre Hotz
 */
public class CodeFragment {

    /**
     * This enum was created for differentiating between the different kinds of code.<br>
     * Therefore in one {@link CodeFragment}-object can only be ONE type of code<br>
     * <br>
     * The different kinds are:
     * <table>
     *      <caption>Enumerate Kind:</caption>
     *      <tr>
     *          <th>CodeFragment.Kind</th>
     *          <th>Description</th>
     *      </tr>
     *      <tr>
     *          <td>ln</td>
     *          <td>The linenumber (automatically inserted)</td>
     *      </tr>
     *      <tr>
     *          <td>normcom</td>
     *          <td>A normal comment created by "//" or "/* ... &#42;/"</td>
     *      </tr>
     *      <tr>
     *          <td>norm</td>
     *          <td>"Normal" not-highlighted code</td>
     *      </tr>
     *      <tr>
     *          <td>key</td>
     *          <td>A syntax-highlighted keyword</td>
     *      </tr>
     *      <tr>
     *          <td>num</td>
     *          <td>A number</td>
     *      </tr>
     *      <tr>
     *          <td>apicom</td>
     *          <td>A API-Comment created by "/** ... &#42;/"</td>
     *      </tr>
     *      <tr>
     *          <td>apicomtag</td>
     *          <td>A API-tag. Created by "@..." in a API-comment</td>
     *      </tr>
     *      <tr>
     *          <td>string</td>
     *          <td>A String created by String-Literals: "..."</td>
     *      </tr>
     * </table>
     * @author Jean-Pierre Hotz
     */
    public enum Kind{
        ln, normcom, norm, key, num, apicom, apicomtag, string;
    }

    /**
     * What kind of Codefragment is represented by this object
     */
    Kind kindOfFragment;

    /**
     * This constructor creates a CodeFragmet-object of the given Kind of code
     * @param kindOfFragment    the kind of code this object should represent
     */
    public CodeFragment(CodeFragment.Kind kindOfFragment){
        this.kindOfFragment = kindOfFragment;
    }

    /**
     * This method creates a HTML-tagged Code, from the kind of code given by this object
     * @param codefragment  the code, which should stand in this tag
     * @return              the HTML-tagged Code as a String
     */
    @SuppressWarnings("incomplete-switch")
    public String generateHTML(String codefragment){
//      First we replace all greater-than and less-than signs by their HTML-entity,
//      so we won't come in trouble with lost code
    	codefragment = codefragment.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
    	
//    	If we have normal code we can just return the given code with the replaced greater-than
//    	and less-than signs, since the whole code-block is surrounded by a span-tag
//    	making all the not-assigned code look like normal code
        if(kindOfFragment == Kind.norm){
            return codefragment;
        }

//      Otherwise we begin a new span tag
        String toRet = "<span class=\"";

//      Give this span-tag its class
        switch(kindOfFragment){
            case ln: toRet += "ln"; break;
            case normcom: toRet += "normCom"; break;
            case key: toRet += "key"; break;
            case num: toRet += "num"; break;
            case apicom: toRet += "apiCom"; break;
            case apicomtag: toRet += "apiComTag"; break;
            case string: toRet += "string"; break;
        }
        toRet = toRet + "\">";

//      Insert the given code into it
        toRet += codefragment;

//      And close the tag again
        toRet += "</span>";

        return toRet;
    }
}