import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class CharCounterTest {

	@Test
	public void testGetCount() throws IOException {
		ICharCounter cc = new CharCounter();
		InputStream ins = new ByteArrayInputStream("wangzimao".getBytes("UTF-8"));
		int ret = cc.countAll(ins);
		assertEquals(9, ret);
	}
 
	@Test
	public void testCountAll() throws IOException {
		ICharCounter cc = new CharCounter();
		InputStream ins = new ByteArrayInputStream("wangzimao".getBytes("UTF-8"));
		cc.countAll(ins);
		// count for 103 should be 1
		// count for 111 should be 1
		assertEquals(1, cc.getCount(103));
		assertEquals(1, cc.getCount(111));
	}

	@Test
	public void testAdd() {
		ICharCounter cc = new CharCounter();
		cc.add(55);
		// after adding 55, now count for 55 should be 1
		assertTrue(cc.getTable().containsKey(55));
		assertEquals(1, (int) cc.getTable().get(55));
	}

	@Test
	public void testSet() {
		ICharCounter cc = new CharCounter();
		cc.add(55);
		// after adding 55, now count for 55 should be 1
		assertTrue(cc.getTable().containsKey(55));
		assertEquals(1, (int) cc.getTable().get(55));
		
		// set value for 55 is 5
		cc.set(55, 5);
		assertEquals(5, (int) cc.getTable().get(55));
		
		// set value for 66 is 6
		cc.set(66, 6);
		assertEquals(6, (int) cc.getTable().get(66));
	}

	@Test
	public void testClear() {
		ICharCounter cc = new CharCounter();
		cc.add(55);
		// after adding 55, now count for 55 should be 1
		assertTrue(cc.getTable().containsKey(55));
		assertEquals(1, (int) cc.getTable().get(55));
		
		// set value for 55 is 5
		cc.set(55, 5);
		assertEquals(5, (int) cc.getTable().get(55));
		
		// set value for 66 is 6
		cc.set(66, 6);
		assertEquals(6, (int) cc.getTable().get(66));
		
		// clear test
		cc.clear();
		assertEquals(0, (int) cc.getTable().get(55));
		assertEquals(0, (int) cc.getTable().get(66));
	}

	@Test
	public void testGetTable() throws IOException {
		ICharCounter cc = new CharCounter();
		InputStream ins = new ByteArrayInputStream("wangzimao".getBytes("UTF-8"));
		cc.countAll(ins);
		// count for 103 should be 1
		// count for 111 should be 1
		assertEquals(8, cc.getTable().size());
	}

}
