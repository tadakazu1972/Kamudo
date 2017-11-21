package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Bird {
    public float x, y;
    public float vx, vy;
    public float ay;
    public float l, r, t, b;
    public double radian;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int move_direction;
    public int status; //飛んでいるか飛んでいないかフラグ 0:飛んでいない 1:飛んでいる
    public int visible;
    public int animal;
    public int MAXX=9;  //マップ配列Xの最大値
    public int MAXY=14; //マップ配列Yの最大値

    public Bird(){
        x = -1*32.0f;
        y = -1*32.0f;
        vy = 0.0f;
        ay = 0.8f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        radian = 0.0d;
        base_index=2;
        index = 0;
        status = 0;
        animal=0;
        move_direction=1;
        vx = -2.0f;
        //Random rndDirection = new Random();
        //move_direction = rndDirection.nextInt(2);
        //if ( move_direction==0) { vx= 2.0f; }
        //if ( move_direction==1) { vx=-2.0f; }
        visible = 0; //コンストラクタ時点では見えないようにセット
    }

    public void Reset() {
        x = -1*32.0f;
        y = -1*32.0f;
        vy = 0.0f;
        ay = 0.8f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        base_index=2;
        index = 0;
        status = 0;
        animal=0;
        move_direction=1;
        vx = -2.0f;
        //Random rndDirection = new Random();
        //move_direction = rndDirection.nextInt(2);
        //if ( move_direction==0) { vx= 2.0f; }
        //if ( move_direction==1) { vx=-2.0f; }
        visible = 0; //ステージ数で増えるときには見えるようにセット
    }

    public void Move(MyChara m, MainActivity ac, float view_width, float view_height, Map map) {
        //飛んでいないとき
        if (status==0) {
            //当たり判定
            l = x -  4.0f; //左大きめに設定
            r = x + 35.0f; //右大きめに設定
            t = y -  4.0f;
            b = y + 31.0f;
            //くまちゃんとの当たり判定
            if (l < m.r && m.l < r && t < m.b && m.t < b) {
                ac.PlaySound(3);
                //飛ばす
                status = 1;
                //鳥の初期座標のマップの値を0に
                int x1=(int)x/32; if (x1<0) x1=0; if (x1>MAXX) x1=MAXX;
                int y1=(int)y/32; if (y1<0) y1=0; if (y1>MAXY) y1=MAXY;
                map.MAP[y1][x1]=0;
                //vx=-3*m.vx;
                //vy=-1*m.vy;
                m.SetVx();
                ac.SetTouch();
            }
        }

        //飛んでいるなら以下を実行
        if (status==1) {
            if (move_direction == 0) {
                vx = 2.0f + ac.loop;
                base_index = 0;
            }
            if (move_direction == 1) {
                vx = -1 * (2.0f + ac.loop);
                base_index = 2;
            }
            radian = radian + 0.1d;
            if (radian > 65000.0d) radian = 0.0d;
            vy = 4.0f * (float) Math.sin(radian);
            //当たり判定移動
            l = x + vx;
            r = x + 31.0f + vx;
            t = y + vy;
            b = y + 31.0f + vy;
            //くまちゃんとの当たり判定
            if ( l < m.r && m.l < r && t < m.b && m.t < b ) {
                ac.PlaySound(3);
                status=1;
                //vx=-3*m.vx;
                //vy=-1*m.vy;
                m.SetVx();
                ac.SetTouch();
            }
            //かおにゃんとの当たり判定
            //座標更新
            x = x + vx;
            y = y + vy;
            //アニメーションインデックス変更処理
            index++;
            if (index > 19) index = 0;
            //画面端判定
            if (x < -31) {
                x = 0;
                //Random rndY = new Random();
                //int ranY = rndY.nextInt(15);
                //y = ranY*32.0f;
                move_direction = 0;
                //もしディスプレイもっていたらどこかに落とす
                if (animal != 0) {
                    ac.c[animal].ResetCake(map);
                    animal = 0;
                }
            }
            if (x > view_width) {
                x = view_width - 1;
                //Random rndY = new Random();
                //int ranY = rndY.nextInt(15);
                //y = ranY*32.0f;
                move_direction = 1;
                //もしディスプレイもっていたらどこかに落とす
                if (animal != 0) {
                    ac.c[animal].ResetCake(map);
                    animal = 0;
                }
            }
            if (y < -31) {
                y = view_height;
            }
            if (y > view_height) {
                y = view_height;
            }
        }
    }

    public void setInitXY(int _x, int _y){
        x = _x*32.0f;
        y = _y*32.0f;
    }
}
