package net.benfro.collections;

import ca.odell.glazedlists.EventList;
import com.google.common.base.Preconditions;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class DefaultSelectableList<E> extends DefaultObservableList<E> implements SelectableList<E> {

   private final PropertyChangeSupport support = new PropertyChangeSupport(this);
   private E selectedElement;

   protected DefaultSelectableList(EventList<E> backingList) {
      super(backingList, false);
   }

   @Override
   public E getSelectedElement() {
      getReadLock().lock();
      try {
         return selectedElement;
      } finally {
         getReadLock().unlock();
      }
   }

   @Override
   public void setSelectedElement(E element) {
      getWriteLock().lock();
      Preconditions.checkArgument(contains(element), "The given argument is not in the selectable range");
      try {
         final E old = this.selectedElement;
         this.selectedElement = element;
         support.firePropertyChange(SELECTED_ELEMENT_PROPERTY, old, element);
      } finally {
         getWriteLock().unlock();
      }
   }

   @Override
   public void setSelectedIndex(int index) {
      setSelectedElement(get(index));
   }

   @Override
   public int getSelectedIndex() {
      getReadLock().lock();
      try {
         if (isElementSelected()) {
            return backingList.indexOf(selectedElement);
         } else {
            return -1;
         }
      } finally {
         getReadLock().unlock();
      }

   }

   @Override
   public boolean isElementSelected() {
      return Objects.nonNull(selectedElement);
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      support.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      support.removePropertyChangeListener(listener);
   }
}
