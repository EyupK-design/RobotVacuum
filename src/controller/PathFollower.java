package controller;

import java.util.ArrayList;
import java.util.List;

// BFS tarafından üretilen koordinat listesini adım adım tüketir.
// Controller istasyona dönüş sırasında her tick'te nextStep() çağırır.
public class PathFollower {

    private List<int[]> path  = new ArrayList<>();
    private int         index = 0;

    // Yeni yolu atar ve sayacı sıfırlar
    public void setPath(List<int[]> newPath) {
        this.path  = (newPath != null) ? new ArrayList<>(newPath) : new ArrayList<>();
        this.index = 0;
    }

    // Gidilecek adım kaldıysa true döner
    public boolean isFollowing() { return index < path.size(); }

    // Sonraki [x, y] koordinatını döndürür ve ilerler. Adım kalmadıysa null döner.
    public int[] nextStep() {
        if (!isFollowing()) return null;
        return path.get(index++);
    }

    public void clear() {
        this.path  = new ArrayList<>();
        this.index = 0;
    }
}
