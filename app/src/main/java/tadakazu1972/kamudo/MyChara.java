package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class MyChara {
    public float x, y;
    public float wx, wy;
    public float vx, vy;
    public float ay;
    public float l, r, t, b;
    public int hp;
    public int str;
    public int def;
    public int exp;
    public int base_index; //アニメーション基底 0:右向き 2:左向き 4:上 6:下
    public int index;
    public int second_jump; //2段ジャンプフラグ
    public int ichigo; //いちごフラグ 0:もっていない 1:もっている
    public int status; //0:ノーマル 1:
    public int animal; //動物ディスプレイのナンバー
    public int stairway; //階段フラグ 0:ない 1:階段の上にいる
    public int shotIndex; //ショットの順番
    public int damage; //ダメージ表示フラグ
    public int MAXX=9;  //マップ配列Xの最大値
    public int MAXY=14; //マップ配列Yの最大値

    public MyChara() {
        x = 3*32.0f;
        y = 13*32.0f;
        wx = 3*32.0f;
        wy = 13*32.0f;
        vx = 0.0f;
        vy = 0.0f;
        ay = 1.0f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        hp = 100;
        str= 10;
        def= 10;
        exp= 0;
        base_index=0;
        index = 0;
        second_jump=0;
        ichigo=0;
        status=0;
        animal=0;
        stairway=0;
        shotIndex=0;
        damage=0;
    }

    public void reset() {
        x = 3*32.0f;
        y = 13*32.0f;
        wx = 3*32.0f;
        wy = 13*32.0f;
        vx = 0.0f;
        vy = 0.0f;
        ay = 1.0f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        hp = 100;
        str= 10;
        def= 10;
        exp= 0;
        index = 0;
        ichigo=0;
        status=0;
        animal=0;
        stairway=0;
        shotIndex=0;
        damage=0;
    }

    public void move(int touch_direction, float view_width, float view_height, Map map, MainActivity ac) {
        if ( status ==0 ) {
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
            } else if (touch_direction == 4) {//上
                base_index = 4;
                //今いるところにはしごはあるか確認のためマップ座標算出
                x1 = (int) (wx +  4.0f + vx) / 32; if (x1 < 0) x1 = 0; if (x1 > MAXX) x1 = MAXX;
                y1 = (int) (wy +  4.0f + vy) / 32; if (y1 < 0) y1 = 0; if (y1 > MAXY) y1 = MAXY;
                x2 = (int) (wx + 28.0f + vx) / 32; if (x2 > MAXX) x2 = MAXX; if (x2 < 0) x2 = 0;
                y2 = (int) (wy + 28.0f + vy) / 32; if (y2 > MAXY) y2 = MAXY; if (y2 < 0) y2 = 0;
                //はしご判定
                if (map.MAP[y1][x1] == 1 || map.MAP[y1][x2] == 1 || map.MAP[y2][x1] == 1 || map.MAP[y2][x2] == 1) {
                    vx = 0.0f;
                    vy = -2.0f;
                    base_index = 4;
                }
            } else if (touch_direction == 3) { //下
                //足元にはしご判定
                if (map.MAP[dy1][dx1] == 1 || map.MAP[dy1][dx2] == 1) {
                    vx = 0.0f;
                    vy = 2.0f;
                    base_index = 4;
                }
            } else if (touch_direction == 1){ //右
                vx = 2.0f;
                vy = 0.0f;
                base_index = 0;
            } else if (touch_direction == 2){ //左
                vx = -2.0f;
                vy =  0.0f;
                base_index = 2;
            } else if (touch_direction == 0){
                vx = 0.0f;
                vy = 0.0f;
                base_index = 0;
            }
            //当たり判定用マップ座標算出
            x1=(int)(wx+4.0f+vx)/32; if (x1<0) x1=0; if (x1>MAXX) x1=MAXX;
            y1=(int)(wy+4.0f+vy)/32; if (y1<0) y1=0; if (y1>MAXY) y1=MAXY;
            x2=(int)(wx+28.0f+vx)/32; if (x2>MAXX) x2=MAXX; if (x2<0) x2=0;
            y2=(int)(wy+28.0f+vy)/32; if (y2>MAXY) y2=MAXY; if (y2<0) y2=0;
            //カベ判定
            if (map.MAP[y1][x1] > 1 || map.MAP[y1][x2] > 1 || map.MAP[y2][x1] > 1 || map.MAP[y2][x2] > 1) {
                //上または下に行こうとしたけど微妙に幅が合っておらず行けない。移動スムーズ処理
                if (touch_direction == 3 || touch_direction == 4){
                    if ((wx+15.0f)%32.0f<15.0f){
                        //右へずらす
                        vx = 1.0f; vy = 0.0f;
                    } else {
                        //左へずらす
                        vx = -1.0f; vy = 0.0f;
                    }
                } else {
                    vx = 0.0f;
                    vy = 0.0f;
                }
            }
            //ワールド座標更新
            wx=wx+vx;
            if (wx<0.0f) wx=0.0f;
            if (wx>MAXX*32.0f) wx=MAXX*32.0f;
            wy=wy+vy;
            if (wy<0.0f) wy=0.0f;
            if (wy>MAXY*32.0f) wy=MAXY*32.0f;
            //ワールド当たり判定移動
            l = wx+ 4.0f+vx;
            r = wx+28.0f+vx;
            t = wy+ 4.0f+vy;
            b = wy+28.0f+vy;
            //画面座標更新
            x = x + vx;
            if (x>MAXX*32.0f) {
                x=MAXX*32.0f;
            }
            if (x<0.0f) {
                x=0.0f;
            }
            y = y + vy;
            if (y>MAXY*32.0f){
                y=MAXY*32.0f-32.0f;
            }
            if (y<-32.0f){
                y=-32.0f;
            }
            //アニメーションインデックス変更処理
            index++;
            if (index > 19) index = 0;
        } else if (status ==1) {

        }
    }

    public void SetVx() {
        if (base_index==0) {
            base_index=2;
        } else {
            base_index=0;
        }
    }

    public void SetStatus(int s) { status=s; }

    public void SetStatus1() { status=1; }

    public void SetXY(float dx, float dy) {
        x = dx;
        y = dy;
    }
}
