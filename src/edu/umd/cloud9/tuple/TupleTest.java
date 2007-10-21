package edu.umd.cloud9.tuple;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import junit.framework.JUnit4TestAdapter;

import org.apache.hadoop.io.Text;
import org.junit.Test;

public class TupleTest {

	public static final Schema SCHEMA1 = new Schema();
	static {
		SCHEMA1.addField("field1", String.class, "default");
		SCHEMA1.addField("field2", Integer.class, new Integer(1));
		SCHEMA1.addField("field3", Long.class, new Long(2));
		SCHEMA1.addField("field4", Float.class, new Float(2.5));
		SCHEMA1.addField("field5", Double.class, new Double(3.14));
		SCHEMA1.addField("field6", String.class, "test");
	}

	// tests unpacking of default values
	@Test
	public void test1() {
		Tuple tuple = SCHEMA1.instantiate();

		byte[] bytes = tuple.pack();

		Tuple unpacked = Tuple.unpack(bytes, SCHEMA1);

		assertEquals(unpacked.get(0), "default");
		assertEquals(unpacked.get(1), new Integer(1));
		assertEquals(unpacked.get(2), new Long(2));
		assertEquals(unpacked.get(3), new Float(2.5));
		assertEquals(unpacked.get(4), new Double(3.14));
		assertEquals(unpacked.get(5), "test");
	}

	// tests unpacking of user-specified values
	@Test
	public void test2() {
		Tuple tuple = SCHEMA1
				.instantiate("Hello world!", new Integer(5), new Long(2),
						new Float(1.2), new Double(3.14), "another string");

		byte[] bytes = tuple.pack();

		Tuple unpacked = Tuple.unpack(bytes, SCHEMA1);

		assertEquals(unpacked.get(0), "Hello world!");
		assertEquals(unpacked.get(1), new Integer(5));
		assertEquals(unpacked.get(2), new Long(2));
		assertEquals(unpacked.get(3), new Float(1.2));
		assertEquals(unpacked.get(4), new Double(3.14));
		assertEquals(unpacked.get(5), "another string");
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TupleTest.class);
	}

}
