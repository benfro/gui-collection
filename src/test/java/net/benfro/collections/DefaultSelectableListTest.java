package net.benfro.collections;

import ca.odell.glazedlists.GlazedLists;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;

class DefaultSelectableListTest {

   private DefaultSelectableList<String> instance;

   @Nested
   @DisplayName("An empty DefaultSelectableList")
   class EmptyList {

      @BeforeEach
      void setUp() {
         instance = new DefaultSelectableList(GlazedLists.eventList(Lists.newArrayList()));
      }

      @Test
      @DisplayName("should have a NULL as selected element")
      void getSelectedElement() {
         assertNull(instance.getSelectedElement());
      }

      @Test
      @DisplayName("should mark FALSE on isElementSelected")
      void isElementSelected() {
         assertFalse(instance.isElementSelected());
      }

      @Test
      @DisplayName("should return -1 as chosen index")
      void testNullIndex() {
         assertEquals(-1, instance.getSelectedIndex());
      }
   }


   @Nested
   @DisplayName("A populated DefaultSelectableList with a selected element")
   class TestSelectedMethodsWhenSelectionMade {

      @BeforeEach
      void setUp() {
         instance = new DefaultSelectableList(GlazedLists.eventList(Lists.newArrayList("A", "B", "C")));
         instance.setSelectedElement("A");
      }

      @Test
      @DisplayName("should have an element set")
      void setSelectedElement() {
         assertEquals("A", instance.getSelectedElement());
      }

      @Test
      @DisplayName("isElementSelected should be true")
      void isElementSelected() {
         assertTrue(instance.isElementSelected());
      }

      @Test
      @DisplayName("an exception is thrown when the element is not in list")
      void testThrowsWhenSelectedNotInList() {
         assertThrows(IllegalArgumentException.class, () -> instance.setSelectedElement("D"));
      }

      @Test
      void setSelectedIndex() {
         instance.setSelectedIndex(2);
         assertEquals("C", instance.getSelectedElement());
      }

      @Test
      void testThrowsWhenIndexOutOfBounds() {
         assertThrows(IndexOutOfBoundsException.class, () -> instance.setSelectedIndex(3));
      }
   }

   @Nested
   class TestWithListener {

      private PropertyChangeListener listener;

      @BeforeEach
      void setUp() {
         instance = new DefaultSelectableList(GlazedLists.eventList(Lists.newArrayList("A", "B", "C")));
         listener = Mockito.mock(PropertyChangeListener.class);
         instance.addPropertyChangeListener(listener);
      }

      @Test
      void testListenerDetectsSelection() {
         instance.setSelectedElement("C");
         PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(instance, "selectedElement", null, "C");
         verify(listener).propertyChange(refEq(propertyChangeEvent));
      }
   }
}