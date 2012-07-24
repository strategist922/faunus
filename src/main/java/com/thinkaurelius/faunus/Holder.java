package com.thinkaurelius.faunus;

import org.apache.hadoop.io.GenericWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Holder<T extends FaunusElement> extends GenericWritable implements WritableComparable<Holder<T>> {

    protected char tag;

    static {
        WritableComparator.define(Holder.class, new Comparator());
    }

    private static Class[] CLASSES = {
            FaunusVertex.class,
            FaunusEdge.class
    };

    protected Class<T>[] getTypes() {
        return CLASSES;

    }

    public Holder() {
        super();
    }

    public Holder(final DataInput in) throws IOException {
        this();
        this.readFields(in);
    }

    public Holder(final char tag, final T element) {
        this();
        this.set(element);
        this.tag = tag;
    }

    public char getTag() {
        return this.tag;
    }

    public T get() {
        return (T) super.get();
    }

    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeChar(this.tag);
        super.write(out);

    }

    @Override
    public void readFields(final DataInput in) throws IOException {
        this.tag = in.readChar();
        super.readFields(in);
    }

    @Override
    public boolean equals(final Object object) {
        return object.getClass().equals(Holder.class) && ((Holder) object).getTag() == this.tag && ((Holder) object).get().equals(this.get());
    }

    @Override
    public int compareTo(final Holder<T> holder) {
        final FaunusElement e1 = holder.get();
        final FaunusElement e2 = this.get();
        if (e1 instanceof FaunusVertex && e2 instanceof FaunusVertex)
            return ((FaunusVertex) e1).compareTo((FaunusVertex) e2);
        else
            return 0;
    }


    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(Holder.class);
        }

        @Override
        public int compare(final byte[] holder1, final int start1, final int length1, final byte[] holder2, final int start2, final int length2) {
            // 1 byte is the class
            // 2 byte is the character
            // the next 8 bytes are the long id

            final ByteBuffer buffer1 = ByteBuffer.wrap(holder1);
            final ByteBuffer buffer2 = ByteBuffer.wrap(holder2);

            buffer1.get();
            buffer2.get();

            buffer1.getChar();
            buffer2.getChar();

            return (((Long) buffer1.getLong()).compareTo(buffer2.getLong()));
        }

        @Override
        public int compare(final WritableComparable a, final WritableComparable b) {
            if (a instanceof Holder && b instanceof Holder)
                return (((Holder) a).get().getIdAsLong()).compareTo(((Holder) b).get().getIdAsLong());
            else
                return super.compare(a, b);
        }
    }
}