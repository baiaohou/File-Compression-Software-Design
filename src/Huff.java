import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Huff implements ITreeMaker, IHuffEncoder, IHuffModel, IHuffHeader {
	
	/**
	 * global variables
	 */
	static HuffTree target;  // target HuffTree
	Map<Integer, String> encodingTable = new HashMap<Integer, String>(); 
	Map<Integer, Integer> frequencyTable = new HashMap<Integer, Integer>();
	String headerStr; // header string
	static boolean flag = false; // boolean flag determine going uncompress or not
	
	public Huff() {
		headerStr = "";
	}
	
	
	/**
	 * Return the  Huffman/coding tree.
	 * @return the Huffman tree
	 */
	@Override
	public HuffTree makeHuffTree(InputStream stream) throws IOException {
		// use getTable method
		ICharCounter cc = new CharCounter();
		cc.countAll(stream);
		frequencyTable = cc.getTable();

		// priority queue
		PriorityQueue<HuffTree> pq = new PriorityQueue<HuffTree>();
		
		// add elem into pq
		for (Integer i: frequencyTable.keySet()) {
			HuffTree n = new HuffTree(i, frequencyTable.get(i));
			pq.add(n);
		}
		
		// also add eof
		HuffTree eof = new HuffTree(PSEUDO_EOF, 1);
		pq.add(eof);
		frequencyTable.put(PSEUDO_EOF, 1);
		
		// build tree
		HuffTree tmp1, tmp2, tmp3 = null;

		while (pq.size() > 1) { // while there are 2 things left
			tmp1 = pq.poll();
			tmp2 = pq.poll();
			tmp3 = new HuffTree(tmp1.root(), tmp2.root(), tmp1.weight() + tmp2.weight());
			pq.add(tmp3);
		}
		target = tmp3; // target is global var
		return target;
	}

	
    /**
     * Initialize state from a tree, the tree is obtained
     * from the treeMaker.
     * @return the map of chars/encoding
     */
	@Override
	public Map<Integer, String> makeTable() {
		// TODO Auto-generated method stub
		// Key:Value = Chars/Encoding
		Map<Integer, String> ret = new HashMap<Integer, String>();
		
		// traverse whole tree
		preorderHelper((IHuffBaseNode) (target.root()), 0, "", ret);
		encodingTable = ret;
		return ret;
	}
	
	
	/**
	 * Helper function to build a Huffman tree
	 * @param n - Node (root) to take in
	 * @param i - flag indicating which branch
	 * @param s - encoding string
	 * @param m - map for reference
	 * @return encoding string
	 */
	String preorderHelper(IHuffBaseNode n, int i, String s, Map<Integer, String> m) {
		// Note: i is a flag determine add 1 or 0 to string
		// i = 1 -- left
		// i = 2 -- right
		if (i == 1) {
			s = s + "0";
		} else if (i == 2) {
			s = s + "1";
		}
		
		if (n.isLeaf() == true) { //if (n == null)
			m.put(((HuffLeafNode) n).element(), s);
			return null;
		}
		// recur on left subtree
		HuffInternalNode ll = (HuffInternalNode) n;
		IHuffBaseNode l = ll.left();
		
		//preorderHelper((IHuffBaseNode) (((HuffInternalNode) n).left()), 1, s, m);
		preorderHelper(l, 1, s, m);
		 
		
		// recur on right subtree
		HuffInternalNode rr = (HuffInternalNode) n;
		IHuffBaseNode r = rr.right();
		preorderHelper(r, 2, s, m);
		
		return s;
	}


    /**
     * Returns coding, e.g., "010111" for specified chunk/character. It
     * is an error to call this method before makeTable has been
     * called.
     * @param i is the chunk for which the coding is returned
     * @return the huff encoding for the specified chunk
     */
	@Override
	public String getCode(int i) {
		// TODO Auto-generated method stub
		if (encodingTable.containsKey(i) == false) return null;
		
		return encodingTable.get(i);
	}


    /**
     * @return a map of all characters and their frequency
     */
	@Override
	public Map<Integer, Integer> showCounts() {
		return this.frequencyTable;
	}

	
	/**
	 * Write a compressed version of the data read by the InputStream parameter,
	 * -- if the stream is not the same as the stream last passed to initialize,
	 * then compression won't be optimal, but will still work. If force is
	 * false, compression only occurs if it saves space. If force is true
	 * compression results even if no bits are saved.
	 * 
	 * @param inFile is the input stream to be compressed
	 * @param outFile   specifies the OutputStream/file to be written with compressed data
	 * @param force  indicates if compression forced
	 * @return the size of the compressed file
	 */
	@Override
	public int write(String inFile, String outFile, boolean force) {
		// TODO Auto-generated method stub
		target = null;
		encodingTable = null;
		encodingTable = new HashMap<Integer, String>();
		//frequencyTable = null;
		//frequencyTable = new HashMap<Integer, Integer>();
		headerStr = null;
		headerStr = "";
		flag = false;
		
		CharCounter cc = new CharCounter();
		cc.clear();
		CharCounter.order = null;
		CharCounter.order = new ArrayList<Integer>();
		
		BitInputStream bis = new BitInputStream(inFile);
		try {
			int temp = bis.read();
			String s = "";
			while (temp != -1) {
				//System.out.println(" now is " + (char) temp);
				s = s + (char) temp;
				temp = bis.read();
			}
			InputStream ins = new ByteArrayInputStream(s.getBytes("UTF-8"));
			
			Huff h = new Huff();
			h.makeHuffTree(ins);
			h.makeTable();
			// now we get freq table and encoding table!
			BitOutputStream bos = new BitOutputStream(outFile);
			h.writeHeader(bos);
			
			// now we have finished adding header
			
			// what we now do is to add content!
			
			double afterBytes = h.headerStr.length(); 
			for (int i = 0; i < CharCounter.order.size(); i++) {
				//System.out.print((char) (int) CharCounter.order.get(i));
				// write each char encoding
				Integer key = (int) CharCounter.order.get(i);
				String code = h.encodingTable.get(key);
				
				// avoid null ptr exception
				if (code == null) continue;
				
				for (int j = 0; j < code.length(); j++) {
					afterBytes++;
					if (code.charAt(j) == '0') {
						bos.write(1, 0);
					} else if (code.charAt(j) == '1') {
						bos.write(1, 1);
					}
				}
			}
			// lastly write EOF
			String codeEOF = h.encodingTable.get(256);
			for (int j = 0; j < codeEOF.length(); j++) {
				afterBytes++;
				if (codeEOF.charAt(j) == '0') {
					bos.write(1, 0);
				} else if (codeEOF.charAt(j) == '1') {
					bos.write(1, 1);
				}
			}	
			
			bos.close();
			
			// count test
			int beforeBytes = (CharCounter.order.size());
			//System.out.println("Original size: " + beforeBytes * 8 + " / Compressed Size: " + (int)afterBytes + "\n");
			
			beforeBytes = beforeBytes * 8;
			//System.out.println("BEFORE:" + beforeBytes);
			//System.out.println("AFTER :" + afterBytes);
			if (beforeBytes < afterBytes && force == false) {
				//System.out.println("Compressed file is bigger than original. Stop!");
				File f = new File(outFile);
				if (f.delete());
				flag = false;
				//System.out.println("nah, returning " + (int) afterBytes);
				return (int) (afterBytes);
			}
			flag = true;
			//System.out.println("yay, returning " + (int) afterBytes);

			return (int) (afterBytes);//return (int) (beforeBytes * 8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}


    /**
     * The number of bits in the header using the implementation, including
     * the magic number presumably stored.
     * @return the number of bits in the header
     */
	@Override
	public int headerSize() {
		// magic number: integer 32-bits
		String ref = "";
		
		String s = preorderHelper2((IHuffBaseNode) (target.root()), "");
		// add magic number
		Integer i = MAGIC_NUMBER;
		String m = i.toBinaryString(i);
		int length = m.length();
		for (int a = 0; a < 32 - length; a++) {
			m = "0" + m;
		}
		ref = m + s;
		headerStr = "" + ref;
		return ref.length();
	}

	
	/**
	 * Recursion helper function to get encodings
	 * @param n - IHuffBaseNode for input
	 * @param s - The encoding string (e.g. "010")
	 * @return encoding string
	 */
	String preorderHelper2(IHuffBaseNode n, String s) {
		
		if (n.isLeaf()) {
			// n is leaf
			// add 1
			s = s + "1";
			// add code
			//System.out.println("Now add " + ((HuffLeafNode) n).element() + " after \t" + s);
			Integer i = ((HuffLeafNode) n).element();
			String is = i.toBinaryString(i);
			String iss = "";
			for (int a = 0; a < 9 - is.length(); a++) {
				iss = iss + "0";
			}
			iss = iss + is;
			s = s + iss;
			return s;
		} else {
			s = s + "0";
		}
		
		// recur on left subtree
		s = preorderHelper2((IHuffBaseNode) (((HuffInternalNode) n).left()), s);
		
		// recur on right subtree
		s = preorderHelper2((IHuffBaseNode) (((HuffInternalNode) n).right()), s);
		
		return s;
	}

	
	/**
	 * Write the header, including magic number and all bits needed to
	 * reconstruct a tree, e.g., using readHeader
	 * @param out is where the header is written
	 * @return the size of the header
	 */
	@Override
	public int writeHeader(BitOutputStream out) {
		// TODO Auto-generated method stub
		int ret = headerSize();
		if (out == null) {
			return -1;
		}
		int count = 0;
		for (int i = 0; i < headerStr.length(); i++) {
			count++;
			if (headerStr.charAt(i) == '0') {
				out.write(1, 0);
				//System.out.println("we are now writing " + headerStr.charAt(i));
			} else if (headerStr.charAt(i) == '1') {
				out.write(1, 1);
				//System.out.println("we are now writing " + headerStr.charAt(i));
			}
		}
		
		//System.out.println(count);
		return ret;
	}


    /**
     * Read the header and return an ITreeMaker object corresponding to
     * the information/header read.
     * @param in is source of bits for header
     * @return an ITreeMaker object representing the tree stored in the header
     * @throws IOException if the header is bad, e.g., wrong MAGIC_NUMBER, wrong
     * number of bits, I/O error occurs reading
     */
	@Override
	public HuffTree readHeader(BitInputStream in) throws IOException {
		
		if (in == null) throw new IOException("Error in InputStream!");
		int a = in.read(32);
		if (a != MAGIC_NUMBER) {
			in.close();
			throw new IOException("Magic number incorrect!");	
		}
		HuffTree ret = new HuffTree(0, 0);
		ret.setRoot(preorderHelper3(in));
		
		return ret;
	}
	
	/**
	 * helper function building a HuffMan tree when decoding
	 * @param in : InputStream
	 * @return IHuffBaseNode r
	 * @throws IOException
	 */
	IHuffBaseNode preorderHelper3(BitInputStream in) throws IOException {
		HuffInternalNode r = new HuffInternalNode(null, null, 0);
		int i = in.read(1); // read one bit
		
		if (i == 1) { //leaf
			int coding = in.read(9);
			return new HuffLeafNode(coding, 0);
		} else if (i == 0) {
			// indicating internal node
			
			// recur left
			r.setLeft(preorderHelper3(in));
			// recur right
			r.setRight(preorderHelper3(in));
		} else {
			System.out.println("Fails to build huff tree!");
			return null;
		}
		
		return r;
	}

    /**
     * Uncompress a previously compressed file.
     * 
     * @param inFile  is the compressed file to be uncompressed
     * @param outFile is where the uncompressed bits will be written
     * @return the size of the uncompressed file
     */
	@Override
	public int uncompress(String inFile, String outFile) {
		BitInputStream bis = new BitInputStream(inFile);
		try {
			target = null;
			frequencyTable = null;
			headerStr = null;
			target = readHeader(bis);
			encodingTable = null;
			
			
			Map<Integer, String> map = makeTable();
			//System.out.println(">>>>>MAP IS "+ map);
			
			String s = "";
			String eof = encodingTable.get(256);
			//System.out.println(eof);
			
			BitOutputStream bos = new BitOutputStream(outFile);
			
			Map<String, Integer> reverseET = new HashMap<String, Integer>();
			// Using for-each loop 
	        for (Map.Entry mapElement : encodingTable.entrySet()) { 
	            Integer key = (Integer)mapElement.getKey(); 
	  
	            // Add some bonus marks 
	            // to all the students and print it 
	            String value = ((String )mapElement.getValue()); 
	            reverseET.put(value, key);
	        } 
			//System.out.println(">>REV MAP IS " + reverseET);
			
			int size = 0;
			while (s.equals(eof) == false) {
				s = s + bis.read(1);
				
				if (reverseET.containsKey(s)) {
					size++;
					// write that char
					bos.write((int)reverseET.get(s));
					if (s.equals(eof)) {
						size--;
						bos.close();
						break;
					}
					// clear string
					s = "";
					continue;
				}
			}
			return size * 8;//
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
