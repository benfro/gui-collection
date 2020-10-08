package net.benfro.collections.sandbox;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HotSwappablePluggableListTest {

   @Test
   void testToUnderstandFunction() {
      HotSwappablePluggableList pluggableList = new HotSwappablePluggableList();
      EventList<String> strings = GlazedLists.eventList(Lists.newArrayList("A", "B", "C"));
      EventList<String> strings2 = GlazedLists.eventList(Lists.newArrayList("D", "E", "F"));

      pluggableList.setSource(strings);
      assertEquals(3, pluggableList.size());

      strings.add("APA");
      assertEquals(4, pluggableList.size());

      pluggableList.setSource(strings2);
      assertEquals(3, pluggableList.size());

   }
}
