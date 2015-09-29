package eu.cityopt.opt.io;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Merge the contents of TimeSeriesData.
 */
public class MergedTimeSeries
extends AbstractCollection<MergedTimeSeries.Entry> {
    public class Entry {
        private double time;
        private int series, point;
        
        public double getTime() {
            return time;
        }

        public int getSeries() {
            return series;
        }
        
        public double getValue() {
            return values.get(series)[point]; 
        }
        
        private Entry(int series, int point) {
            time = times.get(series)[point];
            this.series = series;
            this.point = point;
        }
    }
    
    private class MergeIterator implements Iterator<Entry> {
        private PriorityQueue<Entry> heads = new PriorityQueue<>(
                Math.max(1, times.size()),
                Comparator.comparingDouble(Entry::getTime));
        
        private MergeIterator() {
            heads.addAll(
                    IntStream.range(0, times.size())
                    .filter(i -> times.get(i).length > 0)
                    .mapToObj(i -> new Entry(i, 0))
                    .collect(Collectors.toList()));
        }

        @Override
        public boolean hasNext() {
            return !heads.isEmpty();
        }

        @Override
        public Entry next() {
            Entry e = heads.poll();
            if (e == null) {
                throw new NoSuchElementException();
            }
            int pn = e.point + 1;
            if (pn < times.get(e.series).length) {
                heads.add(new Entry(e.series, pn));
            }
            return e;
        }
    }
    
    private final List<String> names;
    private final List<double []> times, values;
    
    public MergedTimeSeries(List<String> names, TimeSeriesData data) {
        this.names = names;
        times = names.stream()
                .map(n -> data.getSeries(n).getTimes())
                .collect(Collectors.toCollection(ArrayList::new));
        values = names.stream()
                .map(n -> data.getSeries(n).getValues())
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public MergedTimeSeries(TimeSeriesData data) {
        this(sorted(data.seriesData.keySet()), data);
    }

    public MergedTimeSeries() {
        names = Collections.emptyList();
        times = values = Collections.emptyList();
    }
    
    public List<String> getNames() {return names;}

    /**
     * Return an iterator that merges the input time series.
     * Iterating over all points is O(m log n) in time if there are
     * n series and m points in total. 
     * @return
     */
    @Override
    public Iterator<Entry> iterator() {
        return new MergeIterator();
    }

    private static List<String> sorted(Collection<String> keys) {
        List<String> s = new ArrayList<>(keys);
        Collections.sort(s);
        return s;
    }

    /** Return the total number of series points.
     */
    @Override
    public int size() {
        return times.stream().mapToInt(a -> a.length).sum();
    }
}
