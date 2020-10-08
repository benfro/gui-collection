package net.benfro.collections.sandbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("A DefaultMultiSelectableList")
class DefaultMultiSelectableListTest {

   @Test
   @DisplayName("should have a static factory method creating an empty instance")
   void testCreateEmpty() {
      assertTrue(DefaultMultiSelectableList.of().isEmpty());
   }
}
