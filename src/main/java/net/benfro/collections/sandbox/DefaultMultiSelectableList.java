package net.benfro.collections.sandbox;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.benfro.collections.DefaultObservableList;
import net.benfro.collections.SelectableList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultMultiSelectableList<E> extends DefaultObservableList<E>
        implements MultiSelectableList<E> {

   public static <E> DefaultMultiSelectableList<E> of(List<E> list) {
      return new DefaultMultiSelectableList<>(GlazedLists.eventList(list));
   }

   public static <E> DefaultMultiSelectableList<E> ofThreadSafe(List<E> list) {
      return new DefaultMultiSelectableList<>(GlazedLists.threadSafeList(GlazedLists.eventList(list)));
   }

   private final PropertyChangeSupport support = new PropertyChangeSupport(this);
   private final List<E> selected = Lists.newCopyOnWriteArrayList();

   public DefaultMultiSelectableList(EventList<E> backingList) {
      super(backingList, false);
   }

   @Override
   public void selectElements(E... elements) {
      //backingList.getReadWriteLock().readLock().lock();
      try {
         ArrayList<E> elementsToSelect = Lists.newArrayList(elements);
         selectElementsInternal(elementsToSelect);
      } finally {
         //backingList.getReadWriteLock().readLock().unlock();
      }

   }

   private void selectElementsInternal(List<E> elementsToSelect) {
      elementsToSelect.forEach(e -> {
         int idx = backingList.indexOf(e);
         if (idx >= 0) {
            selected.add(e);
            support.fireIndexedPropertyChange(SelectableList.SELECTED_ELEMENT_PROPERTY, idx, null, e);
         }else {
            throw new IllegalArgumentException("One or more elements aren't contained in this list and thus not selectable");
         }
      });
   }

   @Override
   public void deselectElements(E... elements) {
      //backingList.getReadWriteLock().readLock().lock();
      try {
         List<E> elementsToDeselect = Lists.newArrayList(elements);
         deselectElementsInternal(elementsToDeselect);
      } finally {
         //backingList.getReadWriteLock().readLock().unlock();
      }
   }

   private void deselectElementsInternal(Collection<E> elementsToDeselect) {
      elementsToDeselect.forEach(e -> {
         int index = selected.indexOf(e);
         if (index >= 0) {
            selected.remove(index);
            support.fireIndexedPropertyChange(SelectableList.SELECTED_ELEMENT_PROPERTY, index, e, null);
         }
      });
   }

   @Override
   public void selectIndices(int... indices) {
      //backingList.getReadWriteLock().readLock().lock();
      validateRange(indices);

      try {
         List<E> collect = Arrays.stream(indices).boxed().map(backingList::get).collect(Collectors.toList());
         selectElementsInternal(collect);
      } finally {
         //backingList.getReadWriteLock().readLock().unlock();
      }
   }

   private void validateRange(int[] indices) {
      Optional<Integer> max = Arrays.stream(indices).boxed().max(Integer::compare);
      Optional<Integer> min = Arrays.stream(indices).boxed().min(Integer::compare);
      max.ifPresent(maxVal -> Preconditions.checkArgument(maxVal < backingList.size()));
      min.ifPresent(minVal -> Preconditions.checkArgument(minVal >= 0 && minVal < backingList.size()));
   }

   @Override
   public void deselectIndices(int... indices) {
      //backingList.getReadWriteLock().readLock().lock();
      validateRange(indices);

      try {
         List<E> collect = Arrays.stream(indices).boxed().map(backingList::get).collect(Collectors.toList());
         deselectElementsInternal(collect);
      } finally {
         //backingList.getReadWriteLock().readLock().unlock();
      }
   }

   @Override
   public void clearSelected() {
      deselectElementsInternal(selected);
   }

   @Override
   public List<E> getSelectedElements() {
      return selected;
   }

   @Override
   public boolean isElementSelected(E element) {
      //backingList.getReadWriteLock().readLock().lock();
      try {
         return selected.contains(element);
      } finally {
         //backingList.getReadWriteLock().readLock().unlock();
      }
   }

   @Override
   public boolean remove(Object o) {
      deselectElementsInternal(Collections.singletonList((E)o));
      return super.remove(o);
   }

   @Override
   public E remove(int index) {
      deselectElementsInternal(Collections.singletonList((E)backingList.get(index)));
      return super.remove(index);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      deselectElementsInternal((Collection<E>) c);
      return super.removeAll(c);
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      support.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      support.removePropertyChangeListener(listener);
   }
}
