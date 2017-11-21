package tadakazu1972.kamudo;

import java.util.Random;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Tama {
    protected float x, y;   // display x, y
    protected float wx, wy; // world x, y
    protected float vx, vy; // move x, y
    protected float ay;     // gravity
    protected float l, r, t, b; // collision detection
    protected int d; // moveing direction
    protected int index;    // animation index
    protected int base_index; //animation base index
    protected int status;   // character status
    protected int hp;
    protected int damage;
    protected int ranX, ranY;
    protected Random rndX = new Random();
    protected Random rndY = new Random();
    protected Random rnd = new Random();
    public int MAXX=9;  //マップ配列Xの最大値
    public int MAXY=14; //マップ配列Yの最大値

    public Tama(Map map, MainActivity ac){
        do {
            ranX = rndX.nextInt(MAXX+1);
            ranY = rndY.nextInt(MAXY+1);
        } while (map.MAP[ranY][ranX]>0);
        x = ranX * 32.0f;
        y = ranY * 32.0f;
        wx = ranX * 32.0f;
        wy = ranY * 32.0f;
        vx = 1.0f;
        vy = 0.0f;
        ay = 1.0f;
        l = wx;
        r = wx + 31.0f;
        t = wy;
        b = wy + 31.0f;
        d = 2;
        index = 0;
        base_index = 0;
        hp = rnd.nextInt(1000);
        damage = 0;
        status = 0;
    }

    public void reset(Map map){
        do {
            ranX = rndX.nextInt(MAXX+1);
            ranY = rndY.nextInt(MAXY+1);
        } while (map.MAP[ranY][ranX]>0);
        x = ranX * 32.0f;
        y = ranY * 32.0f;
        wx = ranX * 32.0f;
        wy = ranY * 32.0f;
        vx = 1.0f;
        vy = 0.0f;
        ay = 1.0f;
        l = wx;
        r = wx + 31.0f;
        t = wy;
        b = wy + 31.0f;
        d = 2;
        index = 0;
        base_index = 0;
        hp = rnd.nextInt(1000);
        damage = 0;
        status = 0;
    }

    public void move(Map map, MainActivity ac) {
        int x1, x2, y1, y2, dx1, dx2, dy1;
        //足元に地面があるか確認のためマップ座標算出
        dx1=(int)(wx+4.0f+vx)/32; if (dx1<0) dx1=0; if (dx1>MAXX) dx1=MAXX;
        dx2=(int)(wx+28.0f+vx)/32; if (dx2>MAXX) dx2=MAXX; if (dx2<0) dx2=0;
        dy1=(int)(wy+32.0f+vy)/32; if (dy1>MAXY) dy1=MAXY; if (dy1<0) dy1=0;
        //左右ともに空間か判定
        if (map.MAP[dy1][dx1] == 0 && map.MAP[dy1][dx2] == 0) { //足下が空間だから左右移動させず、落下
            vy = vy + ay;
            vx = 0.0f;
            if (vy > 6.0f) vy = 6.0f;
            base_index = 0;
        } else if (d == 2) {
            vx = 1.0f;
            vy = 0.0f;
            base_index = 0;
            //行き先の足元に地面があるか確認のためマップ座標算出
            //dx1=(int)(wx+4.0f+vx)/32; if (dx1<0) dx1=0; if (dx1>29) dx1=29;
            dx2=(int)(wx+32.0f+vx)/32; if (dx2>MAXX) dx2=MAXX; if (dx2<0) dx2=0;
            dy1=(int)(wy+32.0f+vy)/32; if (dy1>MAXY) dy1=MAXY; if (dy1<0) dy1=0;
            if (map.MAP[dy1][dx2] ==0) { //足元がブロックでないなら反転
                vx = -1.0f;
                base_index = 2;
                d = 4;
            }
            //画面右端判定
            if (wx+vx > MAXX*32.0f){
                vx = -1.0f;
                base_index = 2;
                d = 4;
            }
        } else if  (d == 4) {
            vx = -1.0f;
            vy = 0.0f;
            base_index = 2;
            //行き先の足元に地面があるか確認のためマップ座標算出
            dx1=(int)(wx-1.0f+vx)/32; if (dx1<0) dx1=0; if (dx1>MAXX) dx1=MAXX;
            //dx2=(int)(wx+28.0f+vx)/32; if (dx2>29) dx2=29; if (dx2<0) dx2=0;
            dy1=(int)(wy+32.0f+vy)/32; if (dy1>MAXY) dy1=MAXY; if (dy1<0) dy1=0;
            if (map.MAP[dy1][dx1] == 0) { //足元に地面がないから反転
                vx = 1.0f;
                base_index = 0;
                d = 2;
            }
            //画面左端判定
            if (wx+vx < 0.0f){
                vx = 1.0f;
                base_index=0;
                d = 2;
            }
        } else if (d == 0) {
            vy = 0.0f;
            vx = 0.0f;
        }
        //当たり判定用マップ座標算出  左右のみにして計算処理減
        x1 = (int) (wx + 4.0f + vx) / 32; if (x1 < 0) x1 = 0; if (x1 > MAXX) x1 = MAXX;
        y1 = (int) (wy + 4.0f + vy) / 32; if (y1 < 0) y1 = 0; if (y1 > MAXY) y1 = MAXY;
        x2 = (int) (wx + 28.0f + vx) / 32; if (x2 > MAXX) x2 = MAXX; if (x2 < 0) x2 = 0;
        //カベ判定
        if (map.MAP[y1][x1] > 4 || map.MAP[y1][x2] > 4) {
            vx = -1*vx; //壁に当たると反転
            vy = 0.0f;
            if (d == 2 ) { d = 4; base_index = 4; }
            if (d == 4 ) { d = 2; base_index = 2;}
        }
        //ワールド座標更新
        wx = wx + vx;
        if (wx < 0.0f) wx = 0.0f;
        if (wx > 9*32.0f) wx = 9*32.0f;
        wy = wy + vy;
        if (wy < 0.0f) wy = 0.0f;
        if (wy > 14*32.0f) wy = 14*32.0f;
        //ワールド当たり判定移動
        l = wx + 4.0f + vx;
        r = wx + 28.0f + vx;
        t = wy + 4.0f + vy;
        b = wy + 30.0f + vy;
        //プレイヤーとの当たり判定
        if ( l < ac.m.r && ac.m.l < r && t < ac.m.b && ac.m.t < b  ){
            //if (ac.gs!=0) ac.playSound(3);
            //モンスター移動逆向き
            vx=-1*vx;
            if ( d == 2 ) { d = 4; } else { d = 2; }
        }
        //画面座標更新
        x = x + vx;
        y = y + vy;
        //アニメーションインデックス変更処理
        index++;
        if (index > 19) index = 0;
    }
}
