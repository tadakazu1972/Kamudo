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
            if (touch_direction == 4) {//上
                base_index = 4;
                vx = 0.0f;
                vy = -2.0f;
                base_index = 4;
            } else if (touch_direction == 3) { //下
                vx = 0.0f;
                vy = 2.0f;
                base_index = 4;
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
            //ワールド座標更新
            wx=wx+vx;
            if (wx<0.0f) wx=0.0f;
            if (wx>288.0f) wx=288.0f;
            wy=wy+vy;
            if (wy<0.0f) wy=0.0f;
            if (wy>448.0f) wy=448.0f;
            //ワールド当たり判定移動
            l = wx+ 4.0f+vx;
            r = wx+28.0f+vx;
            t = wy+ 4.0f+vy;
            b = wy+28.0f+vy;
            //画面座標更新
            x = x + vx;
            if (x>288.0f) {
                x=288.0f;
            }
            if (x<0.0f) {
                x=0.0f;
            }
            y = y + vy;
            if (y>448.0f){
                y=448.0f;
            }
            if (y<0.0f){
                y=0.0f;
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
