package net.benfro.collections;

public interface SelectableList<E> {

   String SELECTED_ELEMENT_PROPERTY = "selectedElement";

   E getSelectedElement();

   void setSelectedElement(E element);

   void setSelectedIndex(int index);

   int getSelectedIndex();

   boolean isElementSelected();
}
