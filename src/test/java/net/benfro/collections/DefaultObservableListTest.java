package net.benfro.collections;

import com.google.common.collect.Lists;
import org.jdesktop.observablecollections.ObservableListListener;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultObservableListTest {

   private DefaultObservableList<String> instance;

   @Nested
   @DisplayName("An empty DefaultObservableList")
   class TestWithEmptyList {

      @BeforeEach
      void setUp() {
         instance = DefaultObservableList.of();
      }

      @Test
      @DisplayName("should not support elements property changes")
      void supportsElementPropertyChanged() {
         assertFalse(instance.supportsElementPropertyChanged());
      }

      @Test
      @DisplayName("should have size == 0")
      void size() {
         assertEquals(0, instance.size());
      }

      @Test
      @DisplayName("should be empty")
      void isEmpty() {
         assertTrue(instance.isEmpty());
      }

      @Test
      @DisplayName("should not ackowledge to contain an arbitrary object")
      void contains() {
         assertFalse(instance.contains("B"));
      }
   }

   @Nested
   @DisplayName("An DefaultObservableList with three element")
   class TestWithSomeData {

      @BeforeEach
      void setUp() {
         instance = DefaultObservableList.of("A", "B", "C");
      }

      @Test
      @DisplayName("should have size == 3")
      void size() {
         assertEquals(3, instance.size());
      }

      @Test
      @DisplayName("should not be empty")
      void isEmpty() {
         assertFalse(instance.isEmpty());
      }

      @Test
      @DisplayName("should affirm containment of an object which is put in it")
      void contains() {
         assertTrue(instance.contains("B"));
      }

      @Test
      @DisplayName("should be able to spawn an Iterator")
      void iterator() {
         assertNotNull(instance.iterator());
      }

      @Test
      @DisplayName("should be able to spawn an object array with size == 3")
      void toObjectArray() {
         assertEquals(3, instance.toArray().length);
      }

      @Test
      @DisplayName("should be able to spawn an type array with size == 3")
      void toTypeArray() {
         assertEquals(3, instance.toArray(new String[0]).length);
      }

      @Test
      @DisplayName("should increase in size when added to")
      void add() {
         instance.add("D");
         assertEquals(4, instance.size());
      }

      @Test
      @DisplayName("should decrease in size when removed from")
      void remove() {
         assertTrue(instance.remove("C"));
         assertEquals(2, instance.size());
      }

      @Test
      void containsAll() {
         assertTrue(instance.containsAll(Lists.newArrayList("A", "B")));
      }

      @Test
      @DisplayName("should increase in size when a collection is added to it")
      void addAll() {
         instance.addAll(Lists.newArrayList("D", "E"));
         assertEquals(5, instance.size());
      }

      @Test
      @DisplayName("should increase in size when a collection is added to it at an index")
      public void addAllToIndex() {
         instance.addAll(1, Lists.newArrayList("D", "E"));
         assertEquals(5, instance.size());
      }

      @Test
      @DisplayName("should be empty after all elements are removed")
      public void removeAll() {
         assertTrue(instance.removeAll(Lists.newArrayList("A", "B", "C")));
         assertTrue(instance.isEmpty());
      }

      @Test
      public void retainAll() {
      }

      @Test
      @DisplayName("should be empty after list is cleared")
      public void clear() {
         instance.clear();
         assertTrue(instance.isEmpty());
      }

      @Test
      public void get() {
         assertEquals("B", instance.get(1));
      }

      @Test
      public void set() {
      }

      @Test
      public void indexOf() {
         assertEquals(1, instance.indexOf("B"));
      }

      @Test
      public void lastIndexOf() {
         instance.add("A");
         assertEquals(3, instance.lastIndexOf("A"));
      }

      @Test
      public void listIterator() {
         assertNotNull(instance.listIterator());
      }

      @Test
      public void listIterator1() {
         assertNotNull(instance.listIterator(1));
      }

      @Test
      public void subList() {
         assertEquals(Lists.newArrayList("B", "C"), instance.subList(1, 3));
      }

      @Test
      public void readWithLock() {
         assertEquals("A", instance.readWithLock(l -> l.get(0)));
      }

      @Test
      public void readListWithLock() {
         assertEquals(Lists.newArrayList("A", "B"), instance.readListWithLock(l -> l.subList(0, 2)));
      }
   }

   @Nested
   class TestWithObservableListener {

      private ObservableListListener mockListener;

      @BeforeEach
      public void setUp() {
         instance = DefaultObservableList.of(Lists.newArrayList("A", "B", "C"));
         mockListener = Mockito.mock(ObservableListListener.class);
         instance.addObservableListListener(mockListener);
      }

      @AfterEach
      public void afterEach() {
         verifyNoMoreInteractions(mockListener);
      }

      @Nested
      class TestAddingOperations {
         @Test
         public void testAddOne() {
            instance.add("D");
            verify(mockListener, times(1)).listElementsAdded(instance, 3, 1);
         }

         @Test
         void testAddList() {
            instance.addAll(Lists.newArrayList("E", "F"));
            verify(mockListener, times(1)).listElementsAdded(instance, 3, 2);
         }

         @Test
         void testAddAt() {
            instance.add(1, "D");
            verify(mockListener, times(1)).listElementsAdded(instance, 1, 1);
         }
      }

      @Nested
      class TestDeletingOperations {

         @Test
         void testRemoveOneObject() {
            instance.remove("B");
            verify(mockListener, times(1)).listElementsRemoved(instance, 1, Lists.newArrayList("B"));
         }

         @Test
         void testRemoveIndexedObject() {
            instance.remove(1);
            verify(mockListener, times(1)).listElementsRemoved(instance, 1, Lists.newArrayList("B"));
         }

         @Test
         void testRemoveMoreObjects() {
            instance.removeAll(Lists.newArrayList("A", "B"));
            verify(mockListener, times(1)).listElementsRemoved(instance, 0, Lists.newArrayList("A", "B"));
         }

         @Test
         void testRemoveMoreNonAdjacentObjects() {
            instance.removeAll(Lists.newArrayList("A", "C"));
            verify(mockListener, times(1)).listElementsRemoved(instance, 0, Lists.newArrayList("A", "C"));
         }

         @Test
         void testClear() {
            instance.clear();
            verify(mockListener, times(1)).listElementsRemoved(instance, 0, Lists.newArrayList("A", "B", "C"));
         }

         @Test
         void testUpdate() {
            instance.set(0, "D");
            verify(mockListener, times(1)).listElementReplaced(instance, 0, "A");
         }
      }

      @Nested
      class TestUpdateOperations {

         public class PropertyBean {
            private final PropertyChangeSupport support = new PropertyChangeSupport(this);

            int age;
            String name;
            boolean male;

            public PropertyBean(int age, String name, boolean male) {
               this.age = age;
               this.name = name;
               this.male = male;
            }

            public int getAge() {
               return age;
            }

            public void setAge(int age) {
               final int old = this.age;
               this.age = age;
               support.firePropertyChange("age", old, age);
            }

            public String getName() {
               return name;
            }

            public void setName(String name) {
               final String old = this.name;
               this.name = name;
               support.firePropertyChange("name", old, name);
            }

            public boolean isMale() {
               return male;
            }

            public void setMale(boolean male) {
               final boolean old = this.male;
               this.male = male;
               support.firePropertyChange("male", old, male);
            }

            public void addPropertyChangeListener(PropertyChangeListener listener) {
               support.addPropertyChangeListener(listener);
            }

            public void removePropertyChangeListener(PropertyChangeListener listener) {
               support.removePropertyChangeListener(listener);
            }
         }

         DefaultObservableList<TestUpdateOperations.PropertyBean> instance;

         @BeforeEach
         void setUp() {
            ArrayList<PropertyBean> children = Lists.newArrayList(
                    new TestUpdateOperations.PropertyBean(10, "Benny", true),
                    new TestUpdateOperations.PropertyBean(8, "Laura", false),
                    new TestUpdateOperations.PropertyBean(20, "Samantha", false)
            );
            instance = DefaultObservableList.ofPropertyAware(children, TestUpdateOperations.PropertyBean.class);
            mockListener = Mockito.mock(ObservableListListener.class);
            instance.addObservableListListener(mockListener);
         }

         @Test
         public void supportsElementPropertyChanged() {
            assertTrue(instance.supportsElementPropertyChanged());
         }

         @Test
         void testChangeProperty() {
            instance.get(0).setAge(9);
            verify(mockListener).listElementPropertyChanged(instance, 0);
            verifyNoMoreInteractions(mockListener);
         }

         @Test
         void testChangePropertyInstream() {
            instance.forEach(c -> c.setName(c.getName() + " Monkey"));
            verify(mockListener, times(1)).listElementPropertyChanged(instance, 0);
            verify(mockListener, times(1)).listElementPropertyChanged(instance, 1);
            verify(mockListener, times(1)).listElementPropertyChanged(instance, 2);
            verifyNoMoreInteractions(mockListener);
         }

      }
   }
}
