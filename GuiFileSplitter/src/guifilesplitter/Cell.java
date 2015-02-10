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
 * cell.java
 * Created on 17 November 2002, 15:00
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.io.FileWriter;
import java.io.FileReader;
/** Files are split up into chunks. Since I was reading on ATM networks, the name
 * became cells. Cells can be of two types - a .sxx is a cell which does not contain
 * the EOF of the original file, whi a .spt is a cell which contains the last chunk of
 * the file (ie includes the EOF pointer of the original file).
 *<br><br>
 * A cell has the following format :<br>
 * [Header][Data]<br>
 * The header is of variable length but always contains the following data:<br>
 * -filename : Original Filename(path is not saved)<br>
 * -counter  : The sequence number of the cell<br>
 * -size     : The length in bytes of data<br>
 * If the cell is a .spt, then it also has another field:<br>
 * -max_counter : The total number of cells. This is purely for checking purposes.<br>
 * <br>
 * The header size is 640 bytes which should be enough <br>
 * There is no limit to the data size
 * @author  alapan
 * @version 0.2
 *
 * Changes from version 0.1
 * - Uses bytes instead of characters.
 */
public class Cell {

    /** The size of the header used
     */
    public static final int HEADER_SIZE = 640;
    private String filename = "";
    private int counter = 0;
    private int size = 1024;
    private byte[] data;
    private boolean last = false;

    /** Creates a new instance of cell
     * @param filename Name of the file contained in the cell
     * @param size The number of bytes of data contained in the cell
     * @param counter The sequence number of the Cell
     */
    public Cell(String filename, int size, int counter){
        this.filename = filename;
        this.size = size;
        this.counter = counter;
    }

    /** Adds data items to the cell. Used during file splitting
     * @param data The data stream for the cell
     * @param last Detotes whether it is the last cell.
     * @return Returns true if the data size is correct, false otherwise
     */
    public boolean addData(byte[] data, boolean last){
        if (data.length <= this.size)
        {
            this.data = new byte[data.length];
            System.arraycopy(data,0,this.data,0, data.length);
            if ((data.length < this.size) || (last == true))
                this.last = true;
            return true;
        }
        else
            return false;
    }

    /** Returns the data contained in the cell
     * @return Returns the data contained in the cell (data)
     */
    public byte[] getData(){
        return this.data;
    }

    /** Returns the size of the data part of the cell
     * @return Returns the size of the data (size)
     */
    public int getSize(){
        return this.size;
    }

    /** Returns the sequence counter of the cell
     * @return The Sequence counter (counter)
     */
    public int getCounter(){
        return this.counter;
    }

    /** Returns the filename of the data contained in the cell
     * @return Returns the filename (filename)
     */
    public String getFileName(){
        return this.filename;
    }
    /** Returns whether the cell is the last cell in sequence or not.
     * @return True if the cell is the last cell of the sequence
     * False otherwise
     */
    public boolean isLast(){
        return last;
    }

    /** Writes a cell to the disk. Filename does <B>NOT</B> correspond to the 8.3 filename
     * format. Generated filename is of the following format:
     * If the filename has an extention (the extention is removed)
     * New filename is the basename, added with the sequence counter separated by a _.
     * If the cell is the last of the sequence, the cell is of type ".spt" and thus the
     * extention. Otherwise it is of type ".sxx".
     * @throws IOException Thrown if writing cell to disk is not possible
     */
    public void writeData() throws IOException{
        int lastIndex = this.filename.lastIndexOf(".");
        if (lastIndex == 0)
            lastIndex = -1;
        String filename;
        String suffix = ".sxx";
        if (this.last)
            suffix = ".spt";

        if (lastIndex != -1)
            filename = this.filename.substring(0,lastIndex) +"_" + this.counter + suffix;
        else
            filename = this.filename + this.counter +suffix;

        FileOutputStream fout = new FileOutputStream(filename);
        //FileWriter k = new FileWriter(filename);
        java.io.File f = new java.io.File(this.filename);
        /*
        k.write(f.getName());
        k.write("\n");
        k.write(Integer.toString(this.data.length));
        k.write("\n");
        k.write(Integer.toString(this.counter));
        k.write("\n");
         */
        String header = f.getName() + "\n" + this.data.length + "\n" + this.counter + "\n";
        if (this.last){
//            k.write(Integer.toString(this.counter));
//            k.write("\n");
            header += this.counter + "\n";
        }
//        k.write(this.data);
//        k.close();
        byte[] head = new byte[this.HEADER_SIZE];
        head = header.getBytes();
        if (head.length > this.HEADER_SIZE)
            throw new IOException("Header size too big :(");
        fout.write(head);
        if (head.length < this.HEADER_SIZE){
            head = new byte[this.HEADER_SIZE - head.length];
            for (int i = 0; i < head.length; i++)
                head[i] = '\n';
            fout.write(head);
        }
        fout.write(this.data);
    }

    /** Used during merging, it reads the data from the file generated by the cell
     * @throws IOException Thrown if disk reading fails.
     * @throws CoruptFileException Thrown if the file header does not conform to expected form
     * @return returns number of total cells in the sequence if a .spt
     * else returns -1
     */
    public int readData() throws IOException, CoruptFileException{
        return readData(this.filename);
    }
    /** Reads the data of any cell file into the current cell.
     * @param filename The cell filename
     * @throws IOException Thrown if Disk read error occurs
     * @throws CoruptFileException Thrown if file format is not of the expected form
     * @return Total number of cells in the sequence if a .spt
     * -1 otherwise
     */
    public int readData(String filename) throws IOException, CoruptFileException{
        FileInputStream fin = new FileInputStream(filename);
        byte[] header = new byte[640];
        int read = fin.read(header);
        String head = new String(header);
        StringTokenizer st = new StringTokenizer(head,"\n");

        //BufferedReader in = new BufferedReader(new FileReader(filename));
        //in.readLine();
        int t = -1;
        try
        {
            if (!st.hasMoreTokens())
                throw new CoruptFileException("Internal Error : File Header corrupt");
            this.filename = st.nextToken();
            if (!st.hasMoreTokens())
                throw new CoruptFileException("Internal Error : File Header corrupt");
            this.size = Integer.parseInt(st.nextToken());
            if (!st.hasMoreTokens())
                throw new CoruptFileException("Internal Error : File Header corrupt");
            this.counter = Integer.parseInt(st.nextToken());
            if ((filename.substring(filename.length()-4) == ".spt") && st.hasMoreTokens()){
                if (!st.hasMoreTokens())
                    throw new CoruptFileException("Internal Error : File Header corrupt");
                t = Integer.parseInt(st.nextToken());
            }
            else
                t = -1;
        }
        catch (NumberFormatException e)
        {
            throw new CoruptFileException("Internal Error : File Header corrupt");
        }
        this.data = new byte[this.size];
        fin.read(this.data);
        fin.close();
        return t;
    }
}