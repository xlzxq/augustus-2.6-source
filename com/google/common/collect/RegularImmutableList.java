// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.ListIterator;
import java.util.Spliterators;
import java.util.Spliterator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
class RegularImmutableList<E> extends ImmutableList<E>
{
    static final ImmutableList<Object> EMPTY;
    @VisibleForTesting
    final transient Object[] array;
    
    RegularImmutableList(final Object[] array) {
        this.array = array;
    }
    
    @Override
    public int size() {
        return this.array.length;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    Object[] internalArray() {
        return this.array;
    }
    
    @Override
    int internalArrayStart() {
        return 0;
    }
    
    @Override
    int internalArrayEnd() {
        return this.array.length;
    }
    
    @Override
    int copyIntoArray(final Object[] dst, final int dstOff) {
        System.arraycopy(this.array, 0, dst, dstOff, this.array.length);
        return dstOff + this.array.length;
    }
    
    @Override
    public E get(final int index) {
        return (E)this.array[index];
    }
    
    @Override
    public UnmodifiableListIterator<E> listIterator(final int index) {
        return Iterators.forArray(this.array, 0, this.array.length, index);
    }
    
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this.array, 1296);
    }
    
    static {
        EMPTY = new RegularImmutableList<Object>(new Object[0]);
    }
}
