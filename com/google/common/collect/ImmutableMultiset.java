// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Set;
import com.google.common.annotations.GwtIncompatible;
import java.util.Arrays;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Objects;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.function.ToIntFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import javax.annotation.CheckForNull;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableMultiset<E> extends ImmutableMultisetGwtSerializationDependencies<E> implements Multiset<E>
{
    @LazyInit
    @CheckForNull
    private transient ImmutableList<E> asList;
    @LazyInit
    @CheckForNull
    private transient ImmutableSet<Entry<E>> entrySet;
    
    public static <E> Collector<E, ?, ImmutableMultiset<E>> toImmutableMultiset() {
        return CollectCollectors.toImmutableMultiset((Function<? super E, ? extends E>)Function.identity(), e -> 1);
    }
    
    public static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(final Function<? super T, ? extends E> elementFunction, final ToIntFunction<? super T> countFunction) {
        return CollectCollectors.toImmutableMultiset(elementFunction, countFunction);
    }
    
    public static <E> ImmutableMultiset<E> of() {
        return (ImmutableMultiset<E>)RegularImmutableMultiset.EMPTY;
    }
    
    public static <E> ImmutableMultiset<E> of(final E element) {
        return copyFromElements(element);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e1, final E e2) {
        return copyFromElements(e1, e2);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e1, final E e2, final E e3) {
        return copyFromElements(e1, e2, e3);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e1, final E e2, final E e3, final E e4) {
        return copyFromElements(e1, e2, e3, e4);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e1, final E e2, final E e3, final E e4, final E e5) {
        return copyFromElements(e1, e2, e3, e4, e5);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6, final E... others) {
        return new Builder<E>().add(e1).add(e2).add(e3).add(e4).add(e5).add(e6).add(others).build();
    }
    
    public static <E> ImmutableMultiset<E> copyOf(final E[] elements) {
        return (ImmutableMultiset<E>)copyFromElements((Object[])elements);
    }
    
    public static <E> ImmutableMultiset<E> copyOf(final Iterable<? extends E> elements) {
        if (elements instanceof ImmutableMultiset) {
            final ImmutableMultiset<E> result = (ImmutableMultiset<E>)(ImmutableMultiset)elements;
            if (!result.isPartialView()) {
                return result;
            }
        }
        final Multiset<? extends E> multiset = (Multiset<? extends E>)((elements instanceof Multiset) ? Multisets.cast(elements) : LinkedHashMultiset.create((Iterable<?>)elements));
        return copyFromEntries((Collection<? extends Entry<? extends E>>)multiset.entrySet());
    }
    
    public static <E> ImmutableMultiset<E> copyOf(final Iterator<? extends E> elements) {
        final Multiset<E> multiset = (Multiset<E>)LinkedHashMultiset.create();
        Iterators.addAll(multiset, elements);
        return copyFromEntries((Collection<? extends Entry<? extends E>>)multiset.entrySet());
    }
    
    private static <E> ImmutableMultiset<E> copyFromElements(final E... elements) {
        final Multiset<E> multiset = (Multiset<E>)LinkedHashMultiset.create();
        Collections.addAll(multiset, elements);
        return copyFromEntries((Collection<? extends Entry<? extends E>>)multiset.entrySet());
    }
    
    static <E> ImmutableMultiset<E> copyFromEntries(final Collection<? extends Entry<? extends E>> entries) {
        if (entries.isEmpty()) {
            return of();
        }
        return RegularImmutableMultiset.create(entries);
    }
    
    ImmutableMultiset() {
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        final Iterator<Entry<E>> entryIterator = this.entrySet().iterator();
        return new UnmodifiableIterator<E>(this) {
            int remaining;
            @CheckForNull
            E element;
            
            @Override
            public boolean hasNext() {
                return this.remaining > 0 || entryIterator.hasNext();
            }
            
            @Override
            public E next() {
                if (this.remaining <= 0) {
                    final Entry<E> entry = entryIterator.next();
                    this.element = entry.getElement();
                    this.remaining = entry.getCount();
                }
                --this.remaining;
                return Objects.requireNonNull(this.element);
            }
        };
    }
    
    @Override
    public ImmutableList<E> asList() {
        final ImmutableList<E> result = this.asList;
        return (result == null) ? (this.asList = super.asList()) : result;
    }
    
    @Override
    public boolean contains(@CheckForNull final Object object) {
        return this.count(object) > 0;
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public final int add(final E element, final int occurrences) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public final int remove(@CheckForNull final Object element, final int occurrences) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public final int setCount(final E element, final int count) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    @Override
    public final boolean setCount(final E element, final int oldCount, final int newCount) {
        throw new UnsupportedOperationException();
    }
    
    @GwtIncompatible
    @Override
    int copyIntoArray(final Object[] dst, int offset) {
        for (final Entry<E> entry : this.entrySet()) {
            Arrays.fill(dst, offset, offset + entry.getCount(), entry.getElement());
            offset += entry.getCount();
        }
        return offset;
    }
    
    @Override
    public boolean equals(@CheckForNull final Object object) {
        return Multisets.equalsImpl(this, object);
    }
    
    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }
    
    @Override
    public String toString() {
        return this.entrySet().toString();
    }
    
    @Override
    public abstract ImmutableSet<E> elementSet();
    
    @Override
    public ImmutableSet<Entry<E>> entrySet() {
        final ImmutableSet<Entry<E>> es = this.entrySet;
        return (es == null) ? (this.entrySet = this.createEntrySet()) : es;
    }
    
    private ImmutableSet<Entry<E>> createEntrySet() {
        return this.isEmpty() ? ImmutableSet.of() : new EntrySet();
    }
    
    abstract Entry<E> getEntry(final int p0);
    
    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    private final class EntrySet extends IndexedImmutableSet<Entry<E>>
    {
        private static final long serialVersionUID = 0L;
        
        @Override
        boolean isPartialView() {
            return ImmutableMultiset.this.isPartialView();
        }
        
        @Override
        Entry<E> get(final int index) {
            return ImmutableMultiset.this.getEntry(index);
        }
        
        @Override
        public int size() {
            return ImmutableMultiset.this.elementSet().size();
        }
        
        @Override
        public boolean contains(@CheckForNull final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry<?> entry = (Entry<?>)o;
            if (entry.getCount() <= 0) {
                return false;
            }
            final int count = ImmutableMultiset.this.count(entry.getElement());
            return count == entry.getCount();
        }
        
        @Override
        public int hashCode() {
            return ImmutableMultiset.this.hashCode();
        }
        
        @GwtIncompatible
        @Override
        Object writeReplace() {
            return new EntrySetSerializedForm(ImmutableMultiset.this);
        }
    }
    
    @GwtIncompatible
    static class EntrySetSerializedForm<E> implements Serializable
    {
        final ImmutableMultiset<E> multiset;
        
        EntrySetSerializedForm(final ImmutableMultiset<E> multiset) {
            this.multiset = multiset;
        }
        
        Object readResolve() {
            return this.multiset.entrySet();
        }
    }
    
    public static class Builder<E> extends ImmutableCollection.Builder<E>
    {
        final Multiset<E> contents;
        
        public Builder() {
            this(LinkedHashMultiset.create());
        }
        
        Builder(final Multiset<E> contents) {
            this.contents = contents;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> add(final E element) {
            this.contents.add(Preconditions.checkNotNull(element));
            return this;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> add(final E... elements) {
            super.add(elements);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<E> addCopies(final E element, final int occurrences) {
            this.contents.add(Preconditions.checkNotNull(element), occurrences);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<E> setCount(final E element, final int count) {
            this.contents.setCount(Preconditions.checkNotNull(element), count);
            return this;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> addAll(final Iterable<? extends E> elements) {
            if (elements instanceof Multiset) {
                final Multiset<? extends E> multiset = Multisets.cast(elements);
                multiset.forEachEntry((e, n) -> this.contents.add(Preconditions.checkNotNull(e), n));
            }
            else {
                super.addAll(elements);
            }
            return this;
        }
        
        @CanIgnoreReturnValue
        @Override
        public Builder<E> addAll(final Iterator<? extends E> elements) {
            super.addAll(elements);
            return this;
        }
        
        @Override
        public ImmutableMultiset<E> build() {
            return ImmutableMultiset.copyOf((Iterable<? extends E>)this.contents);
        }
        
        @VisibleForTesting
        ImmutableMultiset<E> buildJdkBacked() {
            if (this.contents.isEmpty()) {
                return ImmutableMultiset.of();
            }
            return JdkBackedImmutableMultiset.create((Collection<? extends Entry<? extends E>>)this.contents.entrySet());
        }
    }
    
    static final class ElementSet<E> extends Indexed<E>
    {
        private final List<Entry<E>> entries;
        private final Multiset<E> delegate;
        
        ElementSet(final List<Entry<E>> entries, final Multiset<E> delegate) {
            this.entries = entries;
            this.delegate = delegate;
        }
        
        @Override
        E get(final int index) {
            return this.entries.get(index).getElement();
        }
        
        @Override
        public boolean contains(@CheckForNull final Object object) {
            return this.delegate.contains(object);
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return this.entries.size();
        }
    }
    
    static final class SerializedForm implements Serializable
    {
        final Object[] elements;
        final int[] counts;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final Multiset<?> multiset) {
            final int distinct = multiset.entrySet().size();
            this.elements = new Object[distinct];
            this.counts = new int[distinct];
            int i = 0;
            for (final Entry<?> entry : multiset.entrySet()) {
                this.elements[i] = entry.getElement();
                this.counts[i] = entry.getCount();
                ++i;
            }
        }
        
        Object readResolve() {
            final LinkedHashMultiset<Object> multiset = LinkedHashMultiset.create(this.elements.length);
            for (int i = 0; i < this.elements.length; ++i) {
                multiset.add(this.elements[i], this.counts[i]);
            }
            return ImmutableMultiset.copyOf((Iterable<?>)multiset);
        }
    }
}
