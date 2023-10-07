package net.lintfordlib.core.debug;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;

public class GCMonitor {

	public static void installGCMonitoring() {
		if (!Debug.debugManager().debugModeEnabled())
			return;

		// get all the GarbageCollectorMXBeans - there's one for each heap generation
		// so probably two - the old generation and young generation
		List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
		// Install a notifcation handler for each bean
		for (GarbageCollectorMXBean gcbean : gcbeans) {
			NotificationEmitter emitter = (NotificationEmitter) gcbean;
			// use an anonymously generated listener for this example
			// - proper code should really use a named class
			NotificationListener listener = new NotificationListener() {
				// keep a count of the total time spent in GCs
				long totalGcDuration = 0;

				// implement the notifier callback handler
				@Override
				public void handleNotification(Notification notification, Object handback) {
					// we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
					if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
						// get the information associated with this notification
						GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
						// get all the info and pretty print it
						long duration = info.getGcInfo().getDuration();
						String gctype = info.getGcAction();
						if ("end of minor GC".equals(gctype)) {
							gctype = "Young Gen GC";
						} else if ("end of major GC".equals(gctype)) {
							gctype = "Old Gen GC";
						}

						Debug.debugManager().logger().v(getClass().getSimpleName(), gctype + ": - " + info.getGcInfo().getId() + " " + info.getGcName() + " (from " + info.getGcCause() + ") " + duration + " microseconds;");

						// Get the information about each memory space, and pretty print it
						Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
						Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
						for (Entry<String, MemoryUsage> entry : mem.entrySet()) {
							String name = entry.getKey();
							MemoryUsage memdetail = entry.getValue();
							long memCommitted = memdetail.getCommitted();
							long memMax = memdetail.getMax();
							long memUsed = memdetail.getUsed();
							MemoryUsage before = membefore.get(name);
							long beforepercent = ((before.getUsed() * 1000L) / before.getCommitted());
							long percent = ((memUsed * 1000L) / before.getCommitted()); // >100% when it gets expanded

							Debug.debugManager().logger().v(getClass().getSimpleName(), name + (memCommitted == memMax ? "(fully expanded)" : "(still expandable)") + "used: " + (beforepercent / 10) + "." + (beforepercent % 10) + "%->" + (percent / 10) + "."
									+ (percent % 10) + "%(" + ((memUsed / 1048576) + 1) + "MB) / ");

						}

						totalGcDuration += info.getGcInfo().getDuration();
						long percent = totalGcDuration * 1000L / info.getGcInfo().getEndTime();

						Debug.debugManager().logger().v(getClass().getSimpleName(), "GC cumulated overhead " + (percent / 10) + "." + (percent % 10) + "%");
					}
				}
			};

			emitter.addNotificationListener(listener, null, null);
		}
	}
}
