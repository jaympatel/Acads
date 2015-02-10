/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guifilesplitter;

/**
 *
 * @author Jay
 */
/*
 * CoruptFileException.java
 * Created on 18 November 2002, 16:35
 * Used to denote corupt input files.
 * @author  alapan
 * @version 0.1
 */
/** Used by the splitter and merger routines to show that
 * the file is corupt.
 */
public class CoruptFileException extends java.lang.Exception
{

    /**Creates a new instance of CoruptFileException without detail message.
     */
    public CoruptFileException() {
    }


    /**Constructs an instance of with the specified detail message.
     * @param msg the detail message.
     */
    public CoruptFileException(String msg) {
        super(msg);
    }
}
