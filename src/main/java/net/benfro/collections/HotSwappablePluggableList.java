package net.benfro.collections;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.PluggableList;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * https://stackoverflow.com/questions/26263681/how-to-deal-with-glazedlistss-pluggablelist-requirement-for-shared-publisher-an?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 *
 * @param <T>
 */
public class HotSwappablePluggableList<T> extends PluggableList<T> {

   private EventList<T> syncSourceList = new BasicEventList<>();
   private ListEventListener<T> listEventListener = null;

   public HotSwappablePluggableList() {
      super(new BasicEventList<T>());
   }

   @Override
   public void setSource(final EventList<T> sourceList) {
      getReadWriteLock().writeLock().lock();
      try {
         if (listEventListener != null) {
            syncSourceList.removeListEventListener(listEventListener);
         }

         syncSourceList = sourceList;

         final EventList<T> syncTargetList = createSourceList();
         listEventListener = GlazedLists.syncEventListToList(syncSourceList, syncTargetList);

         super.setSource(syncTargetList);
      } finally {
         getReadWriteLock().writeLock().unlock();
      }
   }
}
