package net.benfro.collections.sandbox;

import ca.odell.glazedlists.EventList;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.benfro.collections.DefaultObservableList;
import net.benfro.collections.SelectableList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultMultiSelectableList<E> extends DefaultObservableList<E> implements MultiSelectableList<E> {

   private final PropertyChangeSupport support = new PropertyChangeSupport(this);
   private final Set<Integer> selected = Sets.newConcurrentHashSet();

   protected DefaultMultiSelectableList(EventList<E> backingList) {
      super(backingList, false);
   }

   @Override
   public void selectElements(E... elements) {
      backingList.getReadWriteLock().readLock().lock();
      try {
         ArrayList<E> elementsToSelect = Lists.newArrayList(elements);
         if (backingList.containsAll(elementsToSelect)) {
            elementsToSelect.forEach(e -> {
               int index = backingList.indexOf(e);
               selected.add(index);
               support.fireIndexedPropertyChange(SelectableList.SELECTED_ELEMENT_PROPERTY, index, null, e);
            });
         } else {
            throw new IllegalArgumentException("One or more elements aren't contained in this list and thus not selectable");
         }
      } finally {
         backingList.getReadWriteLock().readLock().unlock();
      }

   }

   @Override
   public void deselectElements(E... elements) {
      backingList.getReadWriteLock().readLock().lock();
      try {
         ArrayList<E> elementsToDeselect = Lists.newArrayList(elements);
         elementsToDeselect.forEach(e -> {
            int index = backingList.indexOf(e);
            if (index >= 0) {
               if (selected.remove(index)) {
                  support.fireIndexedPropertyChange(SelectableList.SELECTED_ELEMENT_PROPERTY, index, e, null);
               }
               ;
            }
         });
      } finally {
         backingList.getReadWriteLock().readLock().unlock();
      }
   }

   @Override
   public void selectIndices(int... indices) {
      backingList.getReadWriteLock().readLock().lock();
      Optional<Integer> max = Arrays.stream(indices).boxed().max(Integer::compare);
      Optional<Integer> min = Arrays.stream(indices).boxed().min(Integer::compare);
      max.ifPresent(maxVal -> Preconditions.checkArgument(maxVal < backingList.size()));
      min.ifPresent(minVal -> Preconditions.checkArgument(minVal >= 0 && minVal < backingList.size()));

      try {
         Arrays.stream(indices).boxed().forEach(idx -> {
            Optional.ofNullable(backingList.get(idx)).ifPresent(elem -> {
               if (selected.add(idx)) {
                  support.fireIndexedPropertyChange(SelectableList.SELECTED_ELEMENT_PROPERTY, idx, null, elem);
               }
            });
         });
      } finally {
         backingList.getReadWriteLock().readLock().unlock();
      }
   }

   @Override
   public void deselectIndices(int... indices) {

   }

   @Override
   public void clearSelected() {
      //selectedRegistry.getReadWriteLock().writeLock().lock();
      //try {
      //   selectedRegistry.clear();
      //} finally {
      //   selectedRegistry.getReadWriteLock().writeLock().unlock();
      //}
   }

   @Override
   public List<E> getSelectedElements() {
      return selected.stream().map(i -> backingList.get(i)).collect(Collectors.toList());
   }

   @Override
   public boolean isElementSelected(E element) {
      backingList.getReadWriteLock().readLock().lock();
      try {
         return selected.contains(backingList.indexOf(element));
      } finally {
         backingList.getReadWriteLock().readLock().unlock();
      }
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      support.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      support.removePropertyChangeListener(listener);
   }
}
