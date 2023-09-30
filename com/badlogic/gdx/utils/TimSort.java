// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Comparator;

final class TimSort<T>
{
    private T[] a;
    private Comparator<? super T> c;
    private int minGallop;
    private T[] tmp;
    private int tmpCount;
    private int stackSize;
    private final int[] runBase;
    private final int[] runLen;
    
    TimSort() {
        this.minGallop = 7;
        this.stackSize = 0;
        this.tmp = (T[])new Object[256];
        this.runBase = new int[40];
        this.runLen = new int[40];
    }
    
    public final void doSort(final T[] a, final Comparator<T> c, int lo, final int hi) {
        this.stackSize = 0;
        final int length = a.length;
        final int n2 = lo;
        final int n3 = length;
        if (n2 > hi) {
            throw new IllegalArgumentException("fromIndex(" + n2 + ") > toIndex(" + hi + ")");
        }
        if (n2 < 0) {
            throw new ArrayIndexOutOfBoundsException(n2);
        }
        if (hi > n3) {
            throw new ArrayIndexOutOfBoundsException(hi);
        }
        int nRemaining;
        if ((nRemaining = hi - lo) < 2) {
            return;
        }
        if (nRemaining < 32) {
            final int initRunLen = countRunAndMakeAscending(a, lo, hi, c);
            binarySort(a, lo, hi, lo + initRunLen, c);
            return;
        }
        this.a = a;
        this.c = c;
        this.tmpCount = 0;
        final int minRun = minRunLength(nRemaining);
        int runLen;
        do {
            if ((runLen = countRunAndMakeAscending(a, lo, hi, c)) < minRun) {
                final int force = (nRemaining <= minRun) ? nRemaining : minRun;
                final int lo2 = lo;
                binarySort(a, lo2, lo2 + force, lo + runLen, c);
                runLen = force;
            }
            final int n4 = lo;
            final int n5 = runLen;
            this.runBase[this.stackSize] = n4;
            this.runLen[this.stackSize] = n5;
            ++this.stackSize;
            this.mergeCollapse();
            lo += runLen;
        } while ((nRemaining -= runLen) != 0);
        this.mergeForceCollapse();
        this.a = null;
        this.c = null;
        final T[] tmp = this.tmp;
        for (int i = 0, n = this.tmpCount; i < n; ++i) {
            tmp[i] = null;
        }
    }
    
    private static <T> void binarySort(final T[] a, final int lo, final int hi, int start, final Comparator<? super T> c) {
        if (start == lo) {
            ++start;
        }
        while (start < hi) {
            final T pivot = a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                final int mid = left + right >>> 1;
                if (c.compare((Object)pivot, (Object)a[mid]) < 0) {
                    right = mid;
                }
                else {
                    left = mid + 1;
                }
            }
            final int n;
            switch (n = start - left) {
                case 2: {
                    a[left + 2] = a[left + 1];
                }
                case 1: {
                    a[left + 1] = a[left];
                    break;
                }
                default: {
                    System.arraycopy(a, left, a, left + 1, n);
                    break;
                }
            }
            a[left] = pivot;
            ++start;
        }
    }
    
    private static <T> int countRunAndMakeAscending(T[] a, final int lo, int hi, Comparator<? super T> c) {
        int runHi;
        if ((runHi = lo + 1) == hi) {
            return 1;
        }
        if (c.compare((Object)a[runHi++], (Object)a[lo]) < 0) {
            while (runHi < hi && c.compare((Object)a[runHi], (Object)a[runHi - 1]) < 0) {
                ++runHi;
            }
            final T[] array = a;
            c = (Comparator<? super T>)runHi;
            hi = lo;
            a = array;
            --c;
            while (hi < c) {
                final T t = a[hi];
                a[hi++] = a[c];
                a[c--] = t;
            }
        }
        else {
            while (runHi < hi && c.compare((Object)a[runHi], (Object)a[runHi - 1]) >= 0) {
                ++runHi;
            }
        }
        return runHi - lo;
    }
    
    private static int minRunLength(int n) {
        int r = 0;
        while (n >= 32) {
            r |= (n & 0x1);
            n >>= 1;
        }
        return n + r;
    }
    
    private void mergeCollapse() {
        while (this.stackSize > 1) {
            int n;
            if (((n = this.stackSize - 2) > 0 && this.runLen[n - 1] <= this.runLen[n] + this.runLen[n + 1]) || (n >= 2 && this.runLen[n - 2] <= this.runLen[n] + this.runLen[n - 1])) {
                if (this.runLen[n - 1] < this.runLen[n + 1]) {
                    --n;
                }
            }
            else if (this.runLen[n] > this.runLen[n + 1]) {
                break;
            }
            this.mergeAt(n);
        }
    }
    
    private void mergeForceCollapse() {
        while (this.stackSize > 1) {
            int n;
            if ((n = this.stackSize - 2) > 0 && this.runLen[n - 1] < this.runLen[n + 1]) {
                --n;
            }
            this.mergeAt(n);
        }
    }
    
    private void mergeAt(int i) {
        int base1 = this.runBase[i];
        int len1 = this.runLen[i];
        final int base2 = this.runBase[i + 1];
        int len2 = this.runLen[i + 1];
        this.runLen[i] = len1 + len2;
        if (i == this.stackSize - 3) {
            this.runBase[i + 1] = this.runBase[i + 2];
            this.runLen[i + 1] = this.runLen[i + 2];
        }
        --this.stackSize;
        i = gallopRight(this.a[base2], this.a, base1, len1, 0, this.c);
        base1 += i;
        if ((len1 -= i) == 0) {
            return;
        }
        final T key = this.a[base1 + len1 - 1];
        final T[] a = this.a;
        final int base3 = base2;
        final int len3 = len2;
        if ((len2 = gallopLeft(key, a, base3, len3, len3 - 1, this.c)) == 0) {
            return;
        }
        if (len1 <= len2) {
            this.mergeLo(base1, len1, base2, len2);
            return;
        }
        this.mergeHi(base1, len1, base2, len2);
    }
    
    private static <T> int gallopLeft(final T key, final T[] a, final int base, int len, final int hint, final Comparator<? super T> c) {
        int lastOfs = 0;
        int ofs = 1;
        if (c.compare((Object)key, (Object)a[base + hint]) > 0) {
            for (len -= hint; ofs < len && c.compare((Object)key, (Object)a[base + hint + ofs]) > 0; ofs = len) {
                lastOfs = ofs;
                if ((ofs = (ofs << 1) + 1) <= 0) {}
            }
            if (ofs > len) {
                ofs = len;
            }
            lastOfs += hint;
            ofs += hint;
        }
        else {
            for (len = hint + 1; ofs < len && c.compare((Object)key, (Object)a[base + hint - ofs]) <= 0; ofs = len) {
                lastOfs = ofs;
                if ((ofs = (ofs << 1) + 1) <= 0) {}
            }
            if (ofs > len) {
                ofs = len;
            }
            len = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - len;
        }
        ++lastOfs;
        while (lastOfs < ofs) {
            len = lastOfs + (ofs - lastOfs >>> 1);
            if (c.compare((Object)key, (Object)a[base + len]) > 0) {
                lastOfs = len + 1;
            }
            else {
                ofs = len;
            }
        }
        return ofs;
    }
    
    private static <T> int gallopRight(final T key, final T[] a, final int base, int len, final int hint, final Comparator<? super T> c) {
        int ofs = 1;
        int lastOfs = 0;
        if (c.compare((Object)key, (Object)a[base + hint]) < 0) {
            for (len = hint + 1; ofs < len && c.compare((Object)key, (Object)a[base + hint - ofs]) < 0; ofs = len) {
                lastOfs = ofs;
                if ((ofs = (ofs << 1) + 1) <= 0) {}
            }
            if (ofs > len) {
                ofs = len;
            }
            len = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - len;
        }
        else {
            for (len -= hint; ofs < len && c.compare((Object)key, (Object)a[base + hint + ofs]) >= 0; ofs = len) {
                lastOfs = ofs;
                if ((ofs = (ofs << 1) + 1) <= 0) {}
            }
            if (ofs > len) {
                ofs = len;
            }
            lastOfs += hint;
            ofs += hint;
        }
        ++lastOfs;
        while (lastOfs < ofs) {
            len = lastOfs + (ofs - lastOfs >>> 1);
            if (c.compare((Object)key, (Object)a[base + len]) < 0) {
                ofs = len;
            }
            else {
                lastOfs = len + 1;
            }
        }
        return ofs;
    }
    
    private void mergeLo(int base1, int len1, int base2, int len2) {
        final T[] a = this.a;
        final T[] tmp = this.ensureCapacity(len1);
        System.arraycopy(a, base1, tmp, 0, len1);
        int cursor1 = 0;
        base2 = base2;
        base1 = base1;
        a[base1++] = a[base2++];
        if (--len2 == 0) {
            System.arraycopy(tmp, 0, a, base1, len1);
            return;
        }
        if (len1 == 1) {
            System.arraycopy(a, base2, a, base1, len2);
            a[base1 + len2] = tmp[0];
            return;
        }
        final Comparator<? super T> c = this.c;
        int minGallop = this.minGallop;
    Label_0397:
        while (true) {
            int count1 = 0;
            int count2 = 0;
            do {
                if (c.compare((Object)a[base2], (Object)tmp[cursor1]) < 0) {
                    a[base1++] = a[base2++];
                    ++count2;
                    count1 = 0;
                    if (--len2 == 0) {
                        break Label_0397;
                    }
                    continue;
                }
                else {
                    a[base1++] = tmp[cursor1++];
                    ++count1;
                    count2 = 0;
                    if (--len1 != 1) {
                        continue;
                    }
                    break Label_0397;
                }
            } while ((count1 | count2) < minGallop);
            do {
                if ((count1 = gallopRight(a[base2], tmp, cursor1, len1, 0, c)) != 0) {
                    System.arraycopy(tmp, cursor1, a, base1, count1);
                    base1 += count1;
                    cursor1 += count1;
                    if ((len1 -= count1) <= 1) {
                        break Label_0397;
                    }
                }
                a[base1++] = a[base2++];
                if (--len2 == 0) {
                    break Label_0397;
                }
                if ((count2 = gallopLeft(tmp[cursor1], a, base2, len2, 0, c)) != 0) {
                    System.arraycopy(a, base2, a, base1, count2);
                    base1 += count2;
                    base2 += count2;
                    if ((len2 -= count2) == 0) {
                        break Label_0397;
                    }
                }
                a[base1++] = tmp[cursor1++];
                if (--len1 == 1) {
                    break Label_0397;
                }
                --minGallop;
            } while (count1 >= 7 | count2 >= 7);
            if (minGallop < 0) {
                minGallop = 0;
            }
            minGallop += 2;
        }
        this.minGallop = ((minGallop <= 0) ? 1 : minGallop);
        if (len1 == 1) {
            System.arraycopy(a, base2, a, base1, len2);
            a[base1 + len2] = tmp[cursor1];
            return;
        }
        if (len1 == 0) {
            throw new IllegalArgumentException("Comparison method violates its general contract!");
        }
        System.arraycopy(tmp, cursor1, a, base1, len1);
    }
    
    private void mergeHi(final int base1, int len1, int base2, int len2) {
        final T[] a = this.a;
        final T[] tmp = this.ensureCapacity(len2);
        System.arraycopy(a, base2, tmp, 0, len2);
        int cursor1 = base1 + len1 - 1;
        int cursor2 = len2 - 1;
        base2 = base2 + len2 - 1;
        a[base2--] = a[cursor1--];
        if (--len1 == 0) {
            System.arraycopy(tmp, 0, a, base2 - (len2 - 1), len2);
            return;
        }
        if (len2 == 1) {
            base2 -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, base2 + 1, len1);
            a[base2] = tmp[cursor2];
            return;
        }
        final Comparator<? super T> c = this.c;
        int minGallop = this.minGallop;
    Label_0455:
        while (true) {
            int count1 = 0;
            int count2 = 0;
            do {
                if (c.compare((Object)tmp[cursor2], (Object)a[cursor1]) < 0) {
                    a[base2--] = a[cursor1--];
                    ++count1;
                    count2 = 0;
                    if (--len1 == 0) {
                        break Label_0455;
                    }
                    continue;
                }
                else {
                    a[base2--] = tmp[cursor2--];
                    ++count2;
                    count1 = 0;
                    if (--len2 != 1) {
                        continue;
                    }
                    break Label_0455;
                }
            } while ((count1 | count2) < minGallop);
            do {
                final int n = len1;
                final T key = tmp[cursor2];
                final T[] a2 = a;
                final int len3 = len1;
                if ((count1 = n - gallopRight(key, a2, base1, len3, len3 - 1, c)) != 0) {
                    base2 -= count1;
                    cursor1 -= count1;
                    len1 -= count1;
                    System.arraycopy(a, cursor1 + 1, a, base2 + 1, count1);
                    if (len1 == 0) {
                        break Label_0455;
                    }
                }
                a[base2--] = tmp[cursor2--];
                if (--len2 == 1) {
                    break Label_0455;
                }
                final int n2 = len2;
                final T key2 = a[cursor1];
                final T[] a3 = tmp;
                final int base3 = 0;
                final int len4 = len2;
                if ((count2 = n2 - gallopLeft(key2, a3, base3, len4, len4 - 1, c)) != 0) {
                    base2 -= count2;
                    cursor2 -= count2;
                    len2 -= count2;
                    System.arraycopy(tmp, cursor2 + 1, a, base2 + 1, count2);
                    if (len2 <= 1) {
                        break Label_0455;
                    }
                }
                a[base2--] = a[cursor1--];
                if (--len1 == 0) {
                    break Label_0455;
                }
                --minGallop;
            } while (count1 >= 7 | count2 >= 7);
            if (minGallop < 0) {
                minGallop = 0;
            }
            minGallop += 2;
        }
        this.minGallop = ((minGallop <= 0) ? 1 : minGallop);
        if (len2 == 1) {
            base2 -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, base2 + 1, len1);
            a[base2] = tmp[cursor2];
            return;
        }
        if (len2 == 0) {
            throw new IllegalArgumentException("Comparison method violates its general contract!");
        }
        System.arraycopy(tmp, 0, a, base2 - (len2 - 1), len2);
    }
    
    private T[] ensureCapacity(final int minCapacity) {
        this.tmpCount = Math.max(this.tmpCount, minCapacity);
        if (this.tmp.length < minCapacity) {
            int newSize = minCapacity;
            newSize = ((newSize = ((newSize = ((newSize = ((newSize = (minCapacity | newSize >> 1)) | newSize >> 2)) | newSize >> 4)) | newSize >> 8)) | newSize >> 16);
            if (++newSize < 0) {
                newSize = minCapacity;
            }
            else {
                newSize = Math.min(newSize, this.a.length >>> 1);
            }
            final T[] newArray = (T[])new Object[newSize];
            this.tmp = newArray;
        }
        return this.tmp;
    }
}
