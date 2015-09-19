package games.strategy.sound;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.Clip;

class ClipCache {
  private final HashMap<URL, Clip> clipMap = new HashMap<URL, Clip>();
  private final List<URL> cacheOrder = new ArrayList<URL>();
  private final int maxSize;

  ClipCache(final int max) {
    if (max < 1) {
      throw new IllegalArgumentException("ClipCache max must be at least 1");
    }
    maxSize = max;
  }

  public synchronized Clip get(final URL file) {
    Clip clip = clipMap.get(file);
    if (clip != null) {
      cacheOrder.remove(file);
      cacheOrder.add(file);
      return clip;
    }
    if (clipMap.size() >= maxSize) {
      final URL leastPlayed = cacheOrder.get(0);
      // System.out.println("Removing " + leastPlayed + " and adding " + file);
      final Clip leastClip = clipMap.remove(leastPlayed);
      leastClip.stop();
      leastClip.flush();
      leastClip.close();
      cacheOrder.remove(leastPlayed);
    }
    clip = ClipPlayer.createClip(file, false);
    clipMap.put(file, clip);
    cacheOrder.add(file);
    return clip;
  }

  public synchronized void removeAll() {
    for (final Clip clip : clipMap.values()) {
      clip.stop();
      clip.flush();
      clip.close();
    }
    clipMap.clear();
    cacheOrder.clear();
  }
}
