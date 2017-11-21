package tadakazu1972.kamudo;

import java.util.Random;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Cloud {
    public float x, y;
    public float vx, vy;
    public float l, r, t, b;
    public int visible;

    public Cloud(int q){
        x = 0.0f+q*100.0f;
        y = 30.0f+q*20.0f;
        Random r = new Random();
        vy = r.nextFloat();
        vx = r.nextFloat();
        visible = 1;
    }

    public void Move1(MainActivity ac){
        x = x + vx;
        if ( x > ac.VIEW_WIDTH) x = -120.0f;
    }

    public void Move2(MainActivity ac){
        y = y + vy;
        if ( y > ac.VIEW_HEIGHT) y=-60.0f;
    }

    public void SetVY(float y){
        vy = y;
    }
}
