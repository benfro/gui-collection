package net.benfro.collections;

import java.util.List;

public interface MultiSelectableList<E> {
   /**
    * Select one or more elements present int the multi selectable list
    *
    * @param elements
    * @trows IllegalArgumentException if one or more elements are not present int the list
    */
   void selectElements(E... elements);

   /**
    * Deselct one or more elements from the multi selectable list
    *
    * @param elements
    */
   void deselectElements(E... elements);

   /**
    * @param indices
    * @see #selectElements(Object[])
    */
   void selectIndices(int... indices);

   /**
    * @param indices
    * @see #deselectElements(Object[])
    */
   void deselectIndices(int... indices);

   /**
    *
    */
   void clearSelected();

   /**
    * @return A immutable view of the currently selected elements
    */
   List<E> getSelectedElements();

   /**
    * @param element
    * @return <code>true</code> iff the provided element is selected
    */
   boolean isElementSelected(E element);
}
