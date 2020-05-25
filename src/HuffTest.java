import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HuffTest {
	
	/**
	 * Modify the directory here for test files!
	 * fileIn: directory of the file that you are taking in at first
	 * fileCompressed: directory of product of compression
	 * fileUncompressed: directory of product of uncompression
	 */
	String fileIn = "/Users/baiaohou/Downloads/CIT 594 master/hw1/FileCompression-Students/src/junit1.txt";
	String fileCompressed = "/Users/baiaohou/Downloads/CIT 594 master/hw1/FileCompression-Students/junit2.txt";
	String fileUncompressed = "/Users/baiaohou/Downloads/CIT 594 master/hw1/FileCompression-Students/junit3.txt";

	@Test
	public void testMakeHuffTree() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		// to check if the root weight is correct (16)
		assertEquals(16, a.target.weight());
		
		// to check if root is not a leaf
		assertEquals(false, a.target.root().isLeaf());
		
		Huff aa = new Huff();
		InputStream inss = new ByteArrayInputStream("testingtttgg".getBytes("UTF-8"));

		aa.makeHuffTree(inss);
		
	}

	@Test
	public void testMakeTable() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		a.makeTable();
		assertEquals(null, a.getCode(105));
		assertEquals("1111", a.getCode(106));
		assertEquals((int) a.showCounts().get(97), 3);
		assertEquals((int) a.showCounts().get(106), 2);
		assertEquals((int) a.showCounts().get(119), 4);
	}

	@Test
	public void testPreorderHelper() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		a.makeTable();
		Map<Integer, String> ret = new HashMap<Integer, String>();
		a.preorderHelper((IHuffBaseNode) a.target.root(), 0, "", ret);
		assertEquals("10", a.getCode(119));
		assertEquals("1111", a.getCode(106));
		assertEquals((int) a.showCounts().get(97), 3);
		assertEquals((int) a.showCounts().get(106), 2);
		assertEquals((int) a.showCounts().get(119), 4);
	}

	@Test
	public void testGetCode() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		a.makeTable();
		assertEquals("10", a.getCode(119));
		assertEquals("1111", a.getCode(106));
		assertEquals("110", a.getCode(97));
		assertEquals(null, a.getCode(1));
	}

	@Test
	public void testShowCounts() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		a.makeTable();
		Map<Integer, String> ret = new HashMap<Integer, String>();
		a.preorderHelper((IHuffBaseNode) a.target.root(), 0, "", ret);
		assertEquals((int) a.showCounts().get(97), 3);
		assertEquals((int) a.showCounts().get(106), 2);
		assertEquals((int) a.showCounts().get(119), 4);
	}

	@Test
	public void testHeaderSize() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		a.makeTable();
		int b = a.headerSize();
		assertEquals(b, 86);
	}


	@Test
	public void testWriteHeader() throws IOException {
		InputStream ins = new ByteArrayInputStream("jjaaawwwwuuuuuu".getBytes("UTF-8"));
		Huff a = new Huff();
		a.makeHuffTree(ins);
		a.makeTable();
		
		//create the ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		//construct a  BitOutputStream from out
		BitOutputStream b = new BitOutputStream(out);
		//check the size of the header that was written
		//where h if your Huff object
		
		assertTrue((86 == a.writeHeader(new BitOutputStream(out))));
		out.close(); //do not forget to close the stream
	}  
 
	@Test
	public void testReadHeader() throws IOException {
		Huff a = new Huff();
		a.write(fileIn, fileCompressed, true);
		

		BitInputStream bis = new BitInputStream(fileCompressed);
		
		String output = "";
		int i = bis.read(1);
		while (i != -1) {
			if (i == 0) output = output + '0';
			if (i == 1) output = output + '1';
			i = bis.read(1);
		}
		BitInputStream bism = new BitInputStream(fileCompressed);
		
		HuffTree ht = a.readHeader(bism);
		
		// weight of hufftree should be 76
		assertEquals(76, ht.size());
	}

	@Test
	public void testUncompress() throws IOException {
		Huff a = new Huff();
		a.write(fileIn, fileCompressed,	true);

		BitInputStream bis = new BitInputStream(fileCompressed);
		
		String output = "";
		int i = bis.read(1);
		while (i != -1) {
			if (i == 0) output = output + '0';
			if (i == 1) output = output + '1';
			i = bis.read(1);
		}

		BitInputStream bism = new BitInputStream(fileCompressed);
		
		
		int ret = a.uncompress(fileCompressed, fileUncompressed);
		
		// checking if return value is correct, should be 96
		assertEquals(96, ret);
		
	
	}

}
