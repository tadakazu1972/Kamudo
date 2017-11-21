package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Shot {
    public float x, y;
    public float wx, wy;
    public float vx, vy;
    public float l, r, t, b;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int visible;
    public int MAXX=9;  //マップ配列Xの最大値
    public int MAXY=14; //マップ配列Yの最大値

    public Shot(MyChara m) {
        x = m.x +  7.0f; //最初の剣はこの位置
        y = m.y + 11.0f;
        wx = m.wx +  7.0f;
        wy = m.wy + 11.0f;
        vx =  0.0f;
        vy = -6.0f;
        l = wx;
        r = wx + 15.0f; //最初の剣の当たり判定
        t = wy;
        b = wy + 15.0f;
        base_index=0;
        index = 0;
        visible = 0;
    }

    public void Reset(MyChara m) {
        x = m.x +  7.0f;
        y = m.y + 11.0f;
        wx = m.wx +  7.0f;
        wy = m.wy + 11.0f;
        vx =  0.0f;
        vy = -6.0f;
        l = wx;
        r = wx + 15.0f;
        t = wy;
        b = wy + 15.0f;
        base_index=0;
        index = 0;
        visible = 0; //ステージ数で増えるときには見えるようにセット
    }

    public void move(MyChara m, MainActivity ac, Map map) {
        //発射されていたら飛んでいく
        if (visible!=0) {
            //当たり判定用マップ座標算出
            int x1 = (int) (wx + vx) / 32; if (x1 < 0) x1 = 0; if (x1 > MAXX) x1 = MAXX;
            int y1 = (int) (wy + vy) / 32; if (y1 < 0) y1 = 0; if (y1 > MAXY) y1 = MAXY;
            int x2 = (int) (wx + 15.0f + vx) / 32; if (x2 > MAXX) x2 = MAXX; if (x2 < 0) x2 = 0;
            int y2 = (int) (wy + 15.0f + vy) / 32; if (y2 > MAXY) y2 = MAXY; if (y2 < 0) y2 = 0;
            //カベ判定
            if (map.MAP[y1][x1] > 1 || map.MAP[y1][x2] > 1) {
                vx = 0.0f;
                vy = -6.0f;
                Reset(m);
            }
            //ワールド座標更新
            wx = wx + vx;
            if (wx < -32.0f) Reset(m);
            if (wx > MAXX * 32.0f) Reset(m);
            wy = wy + vy;
            if (wy < -32.0f) Reset(m);
            if (wy > MAXY * 32.0f) Reset(m);
            //ワールド当たり判定移動
            l = wx + vx;
            r = wx + 15.0f + vx;
            t = wy + vy;
            b = wy + 15.0f + vy;
            //座標更新
            x = x + vx;
            y = y + vy;
            //アニメーションインデックス変更処理
            index++;
            if (index > 19) index = 0;
        } else {
            //発射されていないなら、主人公の動きと合わせる
            x = m.x +  7.0f;
            y = m.y + 11.0f;
            wx = m.wx +  7.0f;
            wy = m.wy + 11.0f;
        }
    }
}
