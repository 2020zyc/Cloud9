/*
 * Cloud9: A MapReduce Library for Hadoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.cloud9.tuple;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.io.WritableComparable;

/**
 * <p>
 * Class that represents an array list in Hadoop's data type system. It extends ArrayList class, 
 * hence supports all services provided by ArrayList.
 * Elements in the list must be homogeneous and must implement Hadoop's Writable interface. 
 * This class, combined with {@link Tuple}, allows the user to
 * define arbitrarily complex data structures.
 * </p>
 * 
 * @see Tuple
 * @param <E>
 *            type of list element
 */

public class ArrayListWritableComparable<E extends WritableComparable> extends ArrayList<E> implements WritableComparable{

    private static final long serialVersionUID = 1L;

	/**
	 * Creates an ArrayListWritable object.
	 */
	public ArrayListWritableComparable() {
		super();
	}

	/**
	 * Deserializes the array.
	 * 
	 * @param in
	 *            source for raw byte representation
	 */
	@SuppressWarnings("unchecked")
	public void readFields(DataInput in) throws IOException {

		this.clear();

		int numFields = in.readInt();
		if(numFields==0) return;
		String className = in.readUTF();
		E obj;
		try {
			Class c = Class.forName(className);
			for (int i = 0; i < numFields; i++) {
				obj = (E) c.newInstance();
				obj.readFields(in);
				this.add(obj);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serializes this Tuple.
	 * 
	 * @param out
	 *            where to write the raw byte representation
	 */
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.size());
		if(size()==0) return;
		E obj=get(0);
		
		out.writeUTF(obj.getClass().getCanonicalName());

		for (int i = 0; i < size(); i++) {
			obj = get(i);
			if (obj == null) {
				throw new IOException("Cannot serialize null fields!");
			}
			obj.write(out);
		}
	}
	
	/**
	 * <p>
	 * Defines a natural sort order for the ListWritable class. Following
	 * standard convention, this method returns a value less than zero, a value
	 * greater than zero, or zero if this ListWritable should be sorted before,
	 * sorted after, or is equal to <code>obj</code>. The sort order is
	 * defined as follows:
	 * </p>
	 * 
	 * <ul>
	 * <li>Each element in the list is compared sequentially from first to
	 * last.</li>
	 * <li>Lists are sorted with respect to the natural order of the current
	 * list element under consideration, by calling its <code>compareTo</code>
	 * method.</li>
	 * <li>If the current list elements are equal, the next set of elements are
	 * considered.</li>
	 * <li>If all compared elements are equal, but lists are different lengths,
	 * the shorter list is sorted first.</li>
	 * <li>If all list elements are equal and the lists are equal in length,
	 * then the lists are considered equal</li>
	 * </ul>
	 * 
	 * @return a value less than zero, a value greater than zero, or zero if
	 *         this Tuple should be sorted before, sorted after, or is equal to
	 *         <code>obj</code>.
	 */
	public int compareTo(Object obj) {
		ArrayListWritableComparable<?> that = (ArrayListWritableComparable<?>) obj;

		// iterate through the fields
		for (int i = 0; i < this.size(); i++) {
			// sort shorter list first
			if (i >= that.size())
				return 1;

			@SuppressWarnings("unchecked")
			Comparable<Object> thisField = this.get(i);
			@SuppressWarnings("unchecked")
			Comparable<Object> thatField = that.get(i);

			if (thisField.equals(thatField)) {
				// if we're down to the last field, sort shorter list first
				if (i == this.size() - 1) {
					if (this.size() > that.size())
						return 1;

					if (this.size() < that.size())
						return -1;
				}
				// otherwise, move to next field
			} else {
				return thisField.compareTo(thatField);
			}
		}

		return 0;
	}


	/**
	 * Generates human-readable String representation of this ArrayList.
	 * 
	 * @return human-readable String representation of this ArrayList
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < this.size(); i++){
			if (i != 0)
				sb.append(", ");
			sb.append(this.get(i));
		}
		sb.append("]");

		return sb.toString();
	}
	
}
