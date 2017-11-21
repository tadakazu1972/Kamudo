package tadakazu1972.kamudo;

import java.util.Random;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Cake {
    public float x, y;
    public float vx, vy;
    public float l, r, t, b;
    public int visible;
    public int ichigo; //設置フラグ 0:設置されていない 1:設置済み
    public int ranS, ranX, ranY;
    public int s;
    public int index;

    public Cake(Map map, int i) {
        do {
            Random rndS = new Random();
            Random rndX = new Random();
            Random rndY = new Random();
            ranS = rndS.nextInt(12);
            ranX = rndX.nextInt(10);
            ranY = rndY.nextInt(15);
        } while (map.MAP[ranY][ranX]!=0);
        s = ranS;
        x = ranX*32.0f;
        y = ranY*32.0f;
        vx = 0.0f;
        vy = 0.0f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        visible = 1;
        ichigo = 0;
        index=i;
    }

    public void ResetCake(Map map) {
        do {
            Random rndS = new Random();
            Random rndX = new Random();
            Random rndY = new Random();
            ranS = rndS.nextInt(12);
            ranX = rndX.nextInt(10);
            ranY = rndY.nextInt(15);
        } while (map.MAP[ranY][ranX]!=0);
        s = ranS;
        x = ranX*32.0f;
        y = ranY*32.0f;
        vx = 0.0f;
        vy = 0.0f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        visible = 1;
        ichigo = 0;
    }
}
