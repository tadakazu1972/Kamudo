package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Baloon {
    public float x, y;
    public float vx, vy;
    public float l, r, t, b;
    public int visible;
    public int index; //リボンのアニメーション用

    public Baloon() {
        x = 60;
        y = -270;
        vx = 0;
        vy = 0;
        l = x;
        r = x+200;
        t = y;
        b = y+200;
        visible = 0;
        index = 0;
    }

    public void Move(MyChara m, MainActivity a) {
        if ( visible==0) {

        } else if ( visible==1 ) {
            vy = 2.0f;
            y = y + vy;
            //当たり判定移動
            l = x;
            r = x+200;
            t = y;
            b = y+280;
            if ( y > 210 ) {
                y = 210;
                //くまちゃんとの当たり判定
                if (l < m.r && m.l < r && t < m.b && m.t < b) {
                    a.PlaySound(6);
                    visible = 2; //バルーン上昇中に変更
                    m.SetStatus(1); //くまちゃんバルーンつかまり中に変更
                    m.SetXY(x + 84.0f, y + 200.0f);
                }
            }
            //バルーン上昇
        } else if ( visible==2 ) {
            if (a.gs==2){ //エンディングの時はゆっくり上昇
                vy = -0.15f;
            } else {
                vy = -2.0f;
            }
            y = y + vy;
            m.SetXY( x+84.0f, y+200.0f);
            if ( y < -264.0f ) {
                y = -260;
                visible = 0;
                //次のステージへ
                a.GotoNextStage();
            }
        }
        //リボンのアニメーション処理
        index++;
        if ( index > 19 ) index =0;
    }

    public void SetVisible(int i) {
        visible = i;
    }

    public void setY(float dy) { y = dy; }
}
