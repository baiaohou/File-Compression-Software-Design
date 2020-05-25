import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharCounter implements ICharCounter, IHuffConstants {

	// global variable: HashMap table
	Map<Integer, Integer> table = new HashMap<Integer, Integer>();
	public static List<Integer> order = new ArrayList<Integer>();
	
    /**
     * Returns the count associated with specified character.
     * @param ch is the chunk/character for which count is requested
     * @return count of specified chunk
     * @throws the appropriate exception if ch isn't a valid chunk/character
     */
	@Override
	public int getCount(int ch) {
		// TODO Auto-generated method stub
		if (ch < 0 || ch > ALPH_SIZE) { // range of printable chars
			throw new IllegalArgumentException("Invalid charater!");
		}
		 
		if (table.containsKey(ch) == false) return 0;
		
		return table.get(ch);
	}

	/**
     * Initialize state by counting bits/chunks in a stream
     * @param stream is source of data
     * @return count of all chunks/read
     * @throws IOException if reading fails 
     */
	@Override
	public int countAll(InputStream stream) throws IOException {
		// we already have InputStream stream
		// now create data input stream
		DataInputStream dis = null;
		int count = 0;
		
		try {
			dis = new DataInputStream(stream);
			while (dis.available() > 0) {
				count++;
				byte b = dis.readByte();
				//System.out.print((char) b + " ");
				//System.out.print(b + " ");
				if (table.containsKey((int) b)) {
					add(b); // if b is in table, update
				} else { // does not exist, set new key
					set(b, 1);
				}
				// add to ArrayList
				order.add((int)b);
			}
			
		} catch (Exception e) {
			// if any IO error occurs
			e.printStackTrace();
		}
		return count;
	}

    /**
     * Update state to record one occurrence of specified chunk/character.
     * @param i is the chunk being recorded
     */
	@Override
	public void add(int i) {
//		table.put(i, table.get(i) + 1); // increment key (+1)
		table.put(i, table.getOrDefault(i, 0) + 1);
	}

	
    /**
     * Set the value/count associated with a specific character/chunk.
     * @param i is the chunk/character whose count is specified
     * @param value is # occurrences of specified chunk
     */
	@Override
	public void set(int i, int value) {
		table.put(i, value);
	}

    /**
     * All counts cleared to zero.
     */
	@Override
	public void clear() {
		for (HashMap.Entry<Integer, Integer> entry : table.entrySet()) {
			table.put(entry.getKey(), 0); // clear to 0
		}
	}
	
	/**
	 * @return a map of all characters and their frequency
	 */
	@Override
	public Map<Integer, Integer> getTable() {
		return table;
	}
	

}
