package tadakazu1972.kamudo;

import java.util.Random;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Enemy {
    public float x, y;
    public float vx, vy;
    public float ay;
    public float l, r, t, b;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int move_direction;
    public int visible;
    public int ranX, ranY, ranVY;
    public int status; //0:ノーマル 1:フライング中

    public Enemy() {
        Random rndX = new Random();
        Random rndY = new Random();
        ranX = rndX.nextInt(10);
        ranY = rndY.nextInt(13);
        x = ranX*32.0f;
        y = ranY*32.0f;
        vx = 0.0f;
        vy = -6.0f;
        ay = 0.6f;
        l = x;
        r = x + 28.0f;
        t = y;
        b = y + 28.0f;
        base_index=0;
        index = 0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
        visible = 1; //
        status = 0;
    }

    public void Reset() {
        Random rndX = new Random();
        Random rndY = new Random();
        ranX = rndX.nextInt(10);
        ranY = rndY.nextInt(2);
        x = ranX*32.0f;
        y = ranY*32.0f;
        vx = 0.0f;
        vy = 1.0f;
        ay = 0.6f;
        l = x;
        r = x + 28.0f;
        t = y;
        b = y + 28.0f;
        base_index=0;
        index = 0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
        visible = 1; //ステージ数で増えるときには見えるようにセット
        status = 0;
    }

    public void move(MyChara m, MainActivity ac, Star s) {
        switch (status) {
            case 0:
                if (move_direction == 0) {
                    vx = 1.0f;
                    base_index = 0;
                }
                if (move_direction == 1) {
                    vx = -1.0f;
                    base_index = 2;
                }
                if (move_direction == 2) {
                    vx = 1.0f;
                    base_index = 0;
                }
                if (move_direction == 3) {
                    vx = -1.0f;
                    base_index = 2;
                }
                //加速度処理
                vy = vy + ay;
                if ( vy > 6.0f ) vy = 6.0f;
                //座標更新
                x=x+vx;
                if (x<-32.0f) { this.Reset(); }
                if (x>352.0f) { this.Reset(); }
                y=y+vy;
                if (y<-32.0f) { this.Reset(); }
                if (y>512.0f) { this.Reset(); }
                //当たり判定移動
                l = x+ 2.0f+vx;
                r = x+28.0f+vx;
                t = y+ 2.0f+vy;
                b = y+28.0f+vy;
                //プレイヤーとの当たり判定
                if ( l < m.r && m.l < r && t < m.b && m.t < b ) {
                    //サウンド
                    //if (ac.gs!=0) ac.PlaySound(3);
                    //反転
                    if (move_direction != 0) {
                        move_direction = 0;
                    } else {
                        move_direction = 1;
                    }
                    Random rndDirection = new Random();
                    move_direction = rndDirection.nextInt(2);
                    //プレイヤーダメージ表現フラグON
                    m.damage = 1;
                    //プレイヤーの座標をいじわるする
                    m.vx=0.0f;
                    m.vy=0.0f;
                }
                //ショットとの当たり判定
                for (int i=0;i<48;i++){
                    if ( l < ac.shot[i].r && ac.shot[i].l < r && t < ac.shot[i].b && ac.shot[i].t < b ) {
                        //サウンド
                        if (ac.loop==0) {
                            ac.PlaySound(1); //ねこ
                        } else {
                            ac.PlaySound(5); //ひよこ
                        }
                        //ゲットエフェクト表示セット
                        if (s.visible==0){
                            s.Set(x-48.0f, y-48.0f);
                            ac.star_counter++;
                            if (ac.star_counter>ac.SN-1) ac.star_counter = 0;
                        }
                        //ショット消去と初期化
                        ac.shot[i].Reset(m, ac.shot[i].vx);
                        //位置初期化
                        this.Reset();
                        //ねこ救出数増加
                        ac.counter++;
                        //100匹救出するとバルーン起動　AND条件でバルーン未起動としないとなんども戻ってしまう
                        if (ac.counter>99 && ac.b.visible==0) {
                            ac.b.visible=1;
                        }
                    }
                }
                break;

            //フライング中
            case 1:
                break;
            default:
                break;

        }
        //アニメーションインデックス変更処理
        index++;
        if ( index > 19 ) index =0;
    }

    public void setEndingPosition() {
        Random rndX = new Random();
        Random rndY = new Random();
        Random rndVY = new Random();
        ranX = rndX.nextInt(10);
        ranY = rndY.nextInt(10);
        ranVY = rndVY.nextInt(10);
        x = ranX*32.0f;
        y = ranY*32.0f + 480; //一画面下
        vx = 0.0f;
        vy = -1 * ranVY/3;
        base_index=0;
        index = 0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
    }

    public void moveEnding(MainActivity ac, Map map) {
        //左右にぶれる
        Random leftright = new Random();
        Boolean _leftright = leftright.nextBoolean();
        if (_leftright) {
            vx = 1.0f;
        } else {
            vx = -1.0f;
        }

        //画面上で消えたらリセット
        if ( y < -16.0f) {
            //位置初期化
            this.setEndingPosition();
        }

        //座標更新
        x = x + vx;
        y = y + vy;
        //アニメーションインデックス変更処理
        index++;
        if ( index > 19 ) index =0;
    }
}
