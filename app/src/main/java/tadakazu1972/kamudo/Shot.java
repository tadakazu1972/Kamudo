package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Shot {
    public float x, y;
    public float vx, vy;
    public float l, r, t, b;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int visible;

    public Shot(MyChara m, float _vx) {
        x = m.x +  7.0f; //最初の剣はこの位置
        y = m.y + 11.0f;
        vx =  _vx;
        vy = -10.0f;
        l = x;
        r = x + 15.0f; //最初の剣の当たり判定
        t = y;
        b = y + 15.0f;
        base_index=0;
        index = 0;
        visible = 0;
    }

    public void Reset(MyChara m, float _vx) {
        x = m.x +  7.0f;
        y = m.y + 11.0f;
        vx =  _vx;
        vy = -10.0f;
        l = x;
        r = x + 15.0f;
        t = y;
        b = y + 15.0f;
        base_index=0;
        index = 0;
        visible = 0; //ステージ数で増えるときには見えるようにセット
    }

    public void move(MyChara m) {
        //発射されていたら飛んでいく
        if (visible!=0) {
            //画面はみ出しチェック
            x = x + vx;
            if (x < -32.0f) Reset(m, vx);
            if (x > 352.0f) Reset(m, vx);
            y = y + vy;
            if (y < -32.0f) Reset(m, vx);
            if (y > 512.0f) Reset(m, vx);
            //当たり判定移動
            l = x + vx;
            r = x + 15.0f + vx;
            t = y + vy;
            b = y + 15.0f + vy;
            //アニメーションインデックス変更処理
            index++;
            if (index > 19) index = 0;
        } else {
            //発射されていないなら、主人公の動きと合わせる
            x = m.x +  7.0f;
            y = m.y + 11.0f;
        }
    }
}
