package net.benfro.collections.sandbox;

import ca.odell.glazedlists.GlazedLists;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("A DefaultMultiSelectableList")
class DefaultMultiSelectableListTest {

   @Captor
   ArgumentCaptor<PropertyChangeEvent> eventCaptor;
   @Mock
   PropertyChangeListener mock;
   DefaultMultiSelectableList<String> instance;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
      instance = DefaultMultiSelectableList.ofThreadSafe(GlazedLists.eventListOf("A", "B", "C", "D", "E", "F"));
      instance.addPropertyChangeListener(mock);
   }

   @Test
   @DisplayName("should have a static factory method creating an empty instance")
   void testCreateEmpty() {
      assertTrue(DefaultMultiSelectableList.of().isEmpty());
   }

   @Test
   void testState() {
      assertEquals(6, instance.size());
      assertFalse(instance.isEmpty());
      assertTrue(instance.getSelectedElements().isEmpty());
   }

   @Test
   void testSelectOneElement() {
      instance.selectElements("E");
      assertEquals(Collections.singletonList("E"), instance.getSelectedElements());

      verify(mock, times(1)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testSelectOneIndex() {
      instance.selectIndices(4);
      assertEquals(Collections.singletonList("E"), instance.getSelectedElements());

      verify(mock, times(1)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testSelectMoreElement() {
      instance.selectElements("E", "B", "F");
      assertEquals(Lists.newArrayList("E", "B", "F"), instance.getSelectedElements());

      verify(mock, times(3)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testDeselectOneElement() {
      instance.selectElements("E");
      instance.deselectElements("E");
      assertTrue(instance.getSelectedElements().isEmpty());
      verify(mock, times(2)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testClearSelected() {
      instance.selectElements("E", "B", "F");
      instance.clearSelected();

      verify(mock, times(6)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testRemove() {
      instance.selectElements("F");
      instance.remove("F");
      assertTrue(instance.getSelectedElements().isEmpty());

      verify(mock, times(2)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testRemoveAtIndex() {
      instance.selectElements("E");
      instance.remove(4);
      assertTrue(instance.getSelectedElements().isEmpty());

      verify(mock, times(2)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testRemoveAll() {
      instance.selectElements("A", "B", "E");
      instance.removeAll(instance);
      assertTrue(instance.getSelectedElements().isEmpty());

      verify(mock, times(6)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testDeselectNonSelectedShouldNotFireEvent() {
      instance.deselectElements("E");

      verify(mock, times(0)).propertyChange(eventCaptor.capture());
   }

   @Test
   void testIsElementSelected() {
      instance.selectElements("E");
      assertTrue(instance.isElementSelected("E"));
      assertFalse(instance.isElementSelected("A"));
   }
}
