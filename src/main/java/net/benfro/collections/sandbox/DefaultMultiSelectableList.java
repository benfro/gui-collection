package net.benfro.collections.sandbox;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.google.common.collect.Lists;
import net.benfro.collections.DefaultObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultMultiSelectableList<E> extends DefaultObservableList<DefaultMultiSelectableList.SelectableWrapper<E>> implements MultiSelectableList<E> {

   static class SelectableWrapper<E> {
      public final E wrapped;
      public boolean selected;

      public SelectableWrapper(E wrapped) {
         this.wrapped = wrapped;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         SelectableWrapper<?> that = (SelectableWrapper<?>) o;
         return Objects.equals(wrapped, that.wrapped);
      }

      @Override
      public int hashCode() {
         return Objects.hash(wrapped);
      }
   }

   //public  static DefaultMultiSelectableList of() {
   //   EventList<SelectableWrapper> eventList =  GlazedLists.even
   //}

   protected DefaultMultiSelectableList(EventList<SelectableWrapper<E>> backingList) {
      super(backingList, false);
      backingList = new BasicEventList<>(backingList.getPublisher(), backingList.getReadWriteLock());
   }

   @Override
   public void selectElements(E... elements) {
      backingList.getReadWriteLock().writeLock().lock();
      try {
         ArrayList<E> elementsToSelect = Lists.newArrayList(elements);
         if (backingList.containsAll(elementsToSelect)) {
            //elementsToSelect.forEach(e -> selectedRegistry.add(backingList.indexOf(e)));
         } else {
            throw new IllegalArgumentException("One or more elements aren't contained in this list and thus not selectable");
         }
      } finally {
         backingList.getReadWriteLock().writeLock().unlock();
      }

   }

   @Override
   public void deselectElements(E... elements) {

   }

   @Override
   public void selectIndices(int... indices) {

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
      return null;
   }

   @Override
   public boolean isElementSelected(E element) {
      return false;
   }
}
