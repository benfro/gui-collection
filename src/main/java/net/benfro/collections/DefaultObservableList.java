package net.benfro.collections;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.util.concurrent.Lock;
import com.google.common.collect.Lists;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

import java.util.*;
import java.util.function.Function;

public class DefaultObservableList<E> implements ObservableList<E> {

   public static <T> DefaultObservableList<T> of() {
      return new DefaultObservableList<T>(GlazedLists.eventList(Lists.newArrayList()), false);
   }

   public static <T> DefaultObservableList<T> of(List<T> data) {
      return new DefaultObservableList<T>(GlazedLists.eventList(data), false);
   }

   public static <T> DefaultObservableList<T> of(T... data) {
      return new DefaultObservableList<T>(GlazedLists.eventList(Lists.newArrayList(data)), false);
   }

   public static <T> DefaultObservableList<T> ofPropertyAware(List<T> data, Class<T> clazz) {
      EventList<T> eventList = GlazedLists.eventList(data);
      ObservableElementList<T> observableElementList = new ObservableElementList<>(eventList, GlazedLists.beanConnector(clazz));
      return new DefaultObservableList<T>(observableElementList, true);
   }

   public static <T> DefaultObservableList<T> ofThreadSafe(List<T> data) {
      EventList<T> eventList = GlazedLists.eventList(data);
      return new DefaultObservableList<T>(GlazedLists.threadSafeList(eventList), false);
   }

   private class ListEventToObservableListListener implements ListEventListener<E> {
      private List<E> previousState;

      public ListEventToObservableListListener(EventList<E> input) {
         this.previousState = makeTempList(input);
      }

      @Override
      public void listChanged(final ListEvent<E> listEvent) {
         int startIdx = -1;
         int endIdx = -1;
         boolean firstRun = true;

         getWriteLock().lock();
         try {
            while (listEvent.nextBlock()) {
               if (firstRun) {
                  firstRun = false;
                  startIdx = listEvent.getBlockStartIndex();
               }
               if (!listEvent.hasNext()) {
                  endIdx = listEvent.getBlockEndIndex();
               }
            }

            final int startIdxFinal = startIdx;
            final int endIdxFinal = endIdx;

            switch (listEvent.getType()) {
               case ListEvent.INSERT:
                  observableListListeners.forEach(l -> l.listElementsAdded(DefaultObservableList.this,
                          startIdxFinal,
                          endIdxFinal - startIdxFinal + 1));
                  break;
               case ListEvent.DELETE:
                  List<E> deletedItems = Lists.newArrayList(previousState);
                  deletedItems.removeAll(makeTempList(listEvent.getSourceList()));
                  observableListListeners.forEach(l -> l.listElementsRemoved(DefaultObservableList.this,
                          startIdxFinal, deletedItems));
                  break;
               case ListEvent.UPDATE:
                  E newValue = previousState.get(startIdxFinal);

                  if (newValue != listEvent.getSourceList().get(startIdxFinal)) {
                     observableListListeners.forEach(l -> {
                        l.listElementReplaced(DefaultObservableList.this, startIdxFinal, newValue);
                     });
                  } else {
                     observableListListeners.forEach(l -> {
                        l.listElementPropertyChanged(DefaultObservableList.this, startIdxFinal);
                     });
                  }
                  break;
            }
         } finally {
            getWriteLock().unlock();
         }

         previousState = makeTempList(listEvent.getSourceList());
      }

      private ArrayList<E> makeTempList(EventList<E> input) {
         return Lists.newArrayList(input.subList(0, input.size()));
      }

   }

   protected final EventList<E> backingList;
   private final boolean supportsPropertyChange;
   private final List<ObservableListListener> observableListListeners = Lists.newCopyOnWriteArrayList();

   protected DefaultObservableList(EventList<E> backingList, boolean supportsPropertyChange) {
      this.backingList = backingList;
      this.supportsPropertyChange = supportsPropertyChange;
      ListEventListener<E> listener = new ListEventToObservableListListener(this.backingList);
      this.backingList.addListEventListener(listener);
   }

   @Override
   public boolean supportsElementPropertyChanged() {
      return supportsPropertyChange;
   }

   @Override
   public int size() {
      return backingList.size();
   }

   @Override
   public boolean isEmpty() {
      return backingList.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return backingList.contains(o);
   }

   @Override
   public Iterator<E> iterator() {
      return backingList.iterator();
   }

   @Override
   public Object[] toArray() {
      return backingList.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return backingList.toArray(a);
   }

   @Override
   public boolean add(E e) {
      return backingList.add(e);
   }

   @Override
   public boolean remove(Object o) {
      return backingList.remove(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return backingList.containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      return backingList.addAll(c);
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> c) {
      return backingList.addAll(index, c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return backingList.removeAll(c);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return backingList.retainAll(c);
   }

   @Override
   public void clear() {
      backingList.clear();
   }

   @Override
   public E get(int index) {
      return backingList.get(index);
   }

   @Override
   public E set(int index, E element) {
      return backingList.set(index, element);
   }

   @Override
   public void add(int index, E element) {
      backingList.add(index, element);
   }

   @Override
   public E remove(int index) {
      return backingList.remove(index);
   }

   @Override
   public int indexOf(Object o) {
      return backingList.indexOf(o);
   }

   @Override
   public int lastIndexOf(Object o) {
      return backingList.lastIndexOf(o);
   }

   @Override
   public ListIterator<E> listIterator() {
      return backingList.listIterator();
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      return backingList.listIterator(index);
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      return backingList.subList(fromIndex, toIndex);
   }

   public Lock getWriteLock() {
      return backingList.getReadWriteLock().writeLock();
   }

   public Lock getReadLock() {
      return backingList.getReadWriteLock().readLock();
   }

   public E readWithLock(Function<DefaultObservableList<E>, E> f) {
      getReadLock().lock();
      try {
         return f.apply(this);
      } finally {
         getReadLock().unlock();
      }
   }

   public List<E> readListWithLock(Function<DefaultObservableList<E>, List<E>> f) {
      getReadLock().lock();
      try {
         return f.apply(this);
      } finally {
         getReadLock().unlock();
      }
   }

   @Override
   public void addObservableListListener(ObservableListListener observableListListener) {
      observableListListeners.add(observableListListener);
   }

   @Override
   public void removeObservableListListener(ObservableListListener observableListListener) {
      observableListListeners.remove(observableListListener);
   }
}
